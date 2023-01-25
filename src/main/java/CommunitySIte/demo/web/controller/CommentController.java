package CommunitySIte.demo.web.controller;

import CommunitySIte.demo.domain.Comment;
import CommunitySIte.demo.domain.Post;
import CommunitySIte.demo.domain.PostType;
import CommunitySIte.demo.domain.Users;
import CommunitySIte.demo.exception.NotAuthorizedException;
import CommunitySIte.demo.exception.ObjectNotExistsException;
import CommunitySIte.demo.service.CommentService;
import CommunitySIte.demo.service.PostService;
import CommunitySIte.demo.web.argumentresolver.Login;
import CommunitySIte.demo.web.controller.problemcheck.ErrorChecker;
import CommunitySIte.demo.web.controller.form.CommentForm;
import CommunitySIte.demo.web.controller.form.EnterPasswordForm;
import CommunitySIte.demo.web.modelset.ModelSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static CommunitySIte.demo.web.controller.problemcheck.AccessibilityChecker.*;
import static CommunitySIte.demo.web.controller.problemcheck.ErrorChecker.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/forum/{forumId}/post/{postId}/comment")
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;
    private final ModelSet modelSet;
    @ModelAttribute("forumId")
    public Long forumId(@PathVariable Long forumId) {
        return forumId;
    }
    @ModelAttribute("postId")
    public Long postId(@PathVariable Long postId) {
        return postId;
    }
    @ModelAttribute("postType")
    public PostType[] postType() {
        return PostType.values();
    }

    @ModelAttribute("user")
    public Users user(@Login Users loginUser) {
        return loginUser;
    }

    //PostMapping 할 수 있는 방법 알아봐야할 듯(JS쓰던가, Html에서 폼으로 보내던가,...)
    @GetMapping("/{commentId}/delete")
    public String enterPasswordOrNot(@Login Users user,
                                     @PathVariable Long commentId, Model model) throws ObjectNotExistsException {
        Comment comment = commentService.findComment(commentId);
        if (!canUserUpdateOrDelete(user, comment)) {
            throw new NotAuthorizedException("인증되지 않은 사용자 접근입니다.");
        }

        String nextUrl;
        if (comment.getPostType() == PostType.NORMAL) {
            commentService.delete(comment);
            nextUrl = "redirect:/forum/{forumId}/post/{postId}";
        } else {
            model.addAttribute("password", new EnterPasswordForm());
            nextUrl =  "posts/enter-password";
        }

        return nextUrl;
    }

    @PostMapping("/{commentId}/delete")
    public String delete(@PathVariable Long commentId,
                         @Login Users user,
                         @ModelAttribute(name = "password") EnterPasswordForm password,
                         BindingResult bindingResult) throws ObjectNotExistsException {
        Comment comment = commentService.findComment(commentId);
        if (!canUserUpdateOrDelete(user, comment)) {
            throw new NotAuthorizedException("인증되지 않은 사용자 접근입니다.");
        }
        passwordErrorBind(password.getPassword(), bindingResult, comment);

        String nextUrl;
        if (!bindingResult.hasErrors()) {
            nextUrl = "redirect:/forum/{forumId}/post/{postId}";
            commentService.delete(comment);
        } else {
            nextUrl =  "posts/enter-password";
        }

        return nextUrl;
    }

    @PostMapping("/new")
    public String comment(@PathVariable Long forumId,
                          @PathVariable Long postId,
                          @Login Users loginUser,
                          @ModelAttribute @Validated CommentForm commentForm,
                          BindingResult bindingResult,
                          Model model) throws ObjectNotExistsException {
        ErrorChecker.anonymousErrorBinding(commentForm,bindingResult,loginUser);
        String nextUrl;

        if(bindingResult.hasErrors()){
            Post post = postService.findPost(postId);
            modelSet.modelSetPostInfo(forumId, loginUser, commentForm, model, post);

            nextUrl = "posts/post";
        }
        else {
            commentService.writeComment(loginUser,  commentForm.getUsername(), commentForm.getPassword(), postService.findPost(postId),commentForm.getContent());

            nextUrl = "redirect:/forum/{forumId}/post/{postId}";
        }

        return nextUrl;
    }


}
