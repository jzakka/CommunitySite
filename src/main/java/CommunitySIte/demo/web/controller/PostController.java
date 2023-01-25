package CommunitySIte.demo.web.controller;

import CommunitySIte.demo.domain.*;
import CommunitySIte.demo.domain.file.FileStore;
import CommunitySIte.demo.exception.NotAuthorizedException;
import CommunitySIte.demo.exception.ObjectNotExistsException;
import CommunitySIte.demo.repository.CategoryRepository;
import CommunitySIte.demo.service.ForumService;
import CommunitySIte.demo.service.LikedDislikedInfo;
import CommunitySIte.demo.service.PostService;
import CommunitySIte.demo.web.argumentresolver.Login;
import CommunitySIte.demo.web.controller.form.CommentForm;
import CommunitySIte.demo.web.controller.form.EnterPasswordForm;
import CommunitySIte.demo.web.controller.form.PostFeedForm;
import CommunitySIte.demo.web.controller.form.PostUpdateForm;
import CommunitySIte.demo.web.modelset.ModelSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

import static CommunitySIte.demo.service.LikedDislikedInfo.*;
import static CommunitySIte.demo.web.controller.problemcheck.AccessibilityChecker.*;
import static CommunitySIte.demo.web.controller.problemcheck.AccessibilityChecker.isUserManagerOrAdmin;
import static CommunitySIte.demo.web.controller.problemcheck.ErrorChecker.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/forum/{forumId}/post")
public class PostController {

    private final PostService postService;
    private final ForumService forumService;
    private final FileStore fileStore;
    private final CategoryRepository categoryRepository;
    private final ModelSet modelSet;

    @ModelAttribute("forumId")
    public Long forumId(@PathVariable Long forumId) {
        return forumId;
    }

    @ModelAttribute("postType")
    public PostType[] postType() {
        return PostType.values();
    }

    @ModelAttribute("user")
    public Users user(@Login Users loginUser) {
        return loginUser;
    }

    @GetMapping("/{postId}")
    public String post(@PathVariable Long postId,
                       @Login Users user,
                       @PathVariable Long forumId,
                       Model model) throws ObjectNotExistsException {
        Post post = postService.showPostWithComment(postId);
        post.increaseViews();

        CommentForm commentForm = new CommentForm();
        modelSet.modelSetPostInfo(forumId, user, commentForm, model, post);

        return "posts/post";
    }

    @PostMapping("/new")
    public String feed(@ModelAttribute("postFeedForm") @Validated PostFeedForm postFeedForm,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes,
                       @Login Users loginUser) throws IOException, ObjectNotExistsException {
        anonymousErrorBinding(postFeedForm, bindingResult, loginUser);

        String nextUrl = getNextUrlByBindingError(bindingResult, redirectAttributes);
        if (nextUrl == null) {
            nextUrl = "redirect:/forum/{forumId}";

            postService.feedPost(loginUser, forumService.getForum(postFeedForm.getForumId()), postFeedForm.getTitle(), fileStore.storeFile(postFeedForm.getImageFile()),
                    postFeedForm.getContent(), categoryRepository.findById(postFeedForm.getCategoryId()), postFeedForm.getPassword(), postFeedForm.getUsername());

        }
        return nextUrl;

    }


    @GetMapping("/{postId}/delete")
    public String deleteOrEnterPassword(@PathVariable Long postId,
                                        @Login Users user, Model model,
                                        @PathVariable String forumId) throws ObjectNotExistsException {
        Post post = postService.showPostWithForum(postId);

        return deleteTemplate(user, forumId, post, () -> {
            if (post.getPostType() == PostType.NORMAL) {
                postService.delete(post);
                return "redirect:/forum/" + forumId;
            } else {
                model.addAttribute("password", new EnterPasswordForm());
                return "posts/enter-password";
            }
        });
    }

    @PostMapping("/{postId}/delete")
    public String delete(@PathVariable Long postId,
                         @Login Users user,
                         @ModelAttribute(name = "password") EnterPasswordForm password,
                         BindingResult bindingResult, @PathVariable String forumId) throws ObjectNotExistsException {
        Post post = postService.showPostWithForum(postId);

        return deleteTemplate(user, forumId, post, () -> {
            passwordErrorBind(password.getPassword(), bindingResult, post);
            if (bindingResult.hasErrors()) {
                return "posts/enter-password";
            } else {
                postService.delete(post);
                return "redirect:/forum/" + forumId;
            }
        });
    }

    @GetMapping("/{postId}/update")
    public String updatePostForm(@PathVariable Long postId,
                                 @Login Users user,
                                 Model model) throws ObjectNotExistsException {
        Post post = postService.findPost(postId);
        String nextUrl;

        if (canUserUpdateOrDelete(user, post)) {
            modelSet.modelSetPostUpdateForm(postId, model, post);
            nextUrl = "posts/updateForm";
        } else throw new NotAuthorizedException();

        return nextUrl;
    }

    @PostMapping("/{postId}/update")
    public String update(@PathVariable Long postId,
                         @Login Users user,
                         @PathVariable Long forumId,
                         @ModelAttribute("postForm") @Validated PostUpdateForm postForm,
                         BindingResult bindingResult, Model model) throws IOException, ObjectNotExistsException {
        Post post = postService.showPostWithForum(postId);
        String nextUrl;

        if ((nextUrl = getNextUrlByBindingError(user, postForm, bindingResult, post)) == null) {
            if (canUserUpdateOrDelete(user, post)) {
                nextUrl = "redirect:/forum/" + forumId + "/post/{postId}";
                postService.update(postId, postForm.getTitle(), fileStore.storeFile(postForm.getImageFile()), postForm.getContent());
            }
        }
        return nextUrl;
    }

    @GetMapping("/{postId}/good")
    public String good(HttpServletRequest request,
                       @PathVariable Long postId,
                       @PathVariable Long forumId,
                       RedirectAttributes redirectAttributes) throws ObjectNotExistsException {
        LikedDislikedInfo likedDislikedInfo = getInstance();
        String ip = request.getRemoteAddr();
        if (!likedDislikedInfo.hasUserLiked(ip, postId)) {
            postService.like(postId);
            likedDislikedInfo.like(ip, postId);
        } else {
//            log.info("해당 유저({})는 이미 추천했습니다.", ip);
            redirectAttributes.addFlashAttribute("likeMessage", "추천은 1일 1회만 가능합니다.");
        }

        return "redirect:/forum/" + forumId + "/post/" + postId;
    }

    @GetMapping("/{postId}/bad")
    public String bad(HttpServletRequest request,
                      @PathVariable Long postId,
                      @PathVariable Long forumId,
                      RedirectAttributes redirectAttributes) throws ObjectNotExistsException {
        LikedDislikedInfo likedDislikedInfo = getInstance();
        String ip = request.getRemoteAddr();
        if (!likedDislikedInfo.hasUserDisLiked(ip, postId)) {
            postService.dislike(postId);
            likedDislikedInfo.disLike(ip, postId);
        } else {
//            log.info("해당 유저({})는 이미 비추천했습니다.", ip);
            redirectAttributes.addFlashAttribute("dislikeMessage", "비추천은 1일 1회만 가능합니다.");
        }

        return "redirect:/forum/" + forumId + "/post/" + postId;
    }


    interface DeleteCallBack {
        String doSomething();
    }

    public String deleteTemplate(Users user, String forumId, Post post, DeleteCallBack deleteCallBack) {
        String nextUrl;
        if (isUserManagerOrAdmin(user, post.getForum())) {
            postService.delete(post);
            nextUrl = "redirect:/forum/" + forumId;
        } else if (canUserUpdateOrDelete(user, post)) {
            nextUrl = deleteCallBack.doSomething();
        } else throw new NotAuthorizedException();
        return nextUrl;
    }
}
