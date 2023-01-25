package CommunitySIte.demo.web.modelset;

import CommunitySIte.demo.domain.Category;
import CommunitySIte.demo.domain.Forum;
import CommunitySIte.demo.domain.Post;
import CommunitySIte.demo.domain.Users;
import CommunitySIte.demo.exception.ObjectNotExistsException;
import CommunitySIte.demo.service.page.PageInfo;
import CommunitySIte.demo.service.page.PageInfoJpa;
import CommunitySIte.demo.service.page.PagingCallBack;
import CommunitySIte.demo.web.controller.form.CommentForm;
import CommunitySIte.demo.web.controller.form.PostFeedForm;
import CommunitySIte.demo.web.controller.form.PostUpdateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static CommunitySIte.demo.web.controller.problemcheck.AccessibilityChecker.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ModelSet {

    public void modelSetPostInfo(Long forumId, Users loginUser, CommentForm commentForm, Model model, Post post) {
        model.addAttribute("forumId", forumId);
        model.addAttribute("forumName", post.getForum().getForumName());
        model.addAttribute("post", post);
        model.addAttribute("commentForm", commentForm);

        //게시글 수정 삭제 버튼이 사용자 타입, 게시글타입에 따라 보여야할지 말아야 할지 결정
        model.addAttribute("isPostAnonymous", isAnonymousCrudObject(post));
        model.addAttribute("isUserWriter", isUserWriter(loginUser, post));
        model.addAttribute("isUserManager", isUserManagerOrAdmin(loginUser, post.getForum()));
    }

    public void modelSetPostUpdateForm(Long postId, Model model, Post post) {
        model.addAttribute("forumId", post.getForum().getId());
        model.addAttribute("postId", postId);
        String uploadFileName = null;
        String storeFileName = null;
        if(post.getImageFile()!=null){
            uploadFileName = post.getImageFile().getUploadFileName();
            storeFileName = post.getImageFile().getStoreFileName();
        }
        model.addAttribute("postForm", new PostUpdateForm(post.getContent(), post.getContent(),
                post.getPostType(), uploadFileName, storeFileName));
    }

    public void listPosts(PostFeedForm postForm, HttpServletRequest request, Model model, Forum forum, List<Category> categories, Page<Post> postPage) {
        model.addAttribute("postForm", postForm);
        model.addAttribute("forum", forum);
        model.addAttribute("forumId", forum.getId());
        model.addAttribute("forumName", forum.getForumName());
        model.addAttribute("categories", categories);

        PageInfo pageInfoJpa = new PageInfoJpa(postPage);
        model.addAttribute("currentUrl", request.getRequestURL());
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("pageInfo", pageInfoJpa);
    }

    public String listPostsTemplate(List<Category> categories, PostFeedForm postForm, Users loginUser,
                                           HttpServletRequest request, Model model, Forum forum, PagingCallBack pagingCallBack) throws ObjectNotExistsException {
        Page<Post> postPage = pagingCallBack.page();

        listPosts(postForm, request, model, forum, categories, postPage);

        modelSetUserAuthorization(loginUser, model, forum);

        return "forums/forum";
    }

    public void modelSetUserAuthorization(Users loginUser, Model model, Forum forum) {
        boolean hasAuthorization = isUserManagerOrAdmin(loginUser, forum);
        model.addAttribute("user", loginUser);
        model.addAttribute("isManager", hasAuthorization);
    }
}
