package CommunitySIte.demo.web.controller;

import CommunitySIte.demo.domain.*;
import CommunitySIte.demo.exception.ObjectNotExistsException;
import CommunitySIte.demo.service.ForumManageService;
import CommunitySIte.demo.service.ForumService;
import CommunitySIte.demo.service.PostService;
import CommunitySIte.demo.service.UserService;
import CommunitySIte.demo.web.argumentresolver.Login;
import CommunitySIte.demo.web.controller.form.ForumForm;
import CommunitySIte.demo.web.controller.form.ManagerForm;
import CommunitySIte.demo.web.controller.form.PostFeedForm;
import CommunitySIte.demo.service.page.PagingCallBack;
import CommunitySIte.demo.web.modelset.ModelSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/forum")
public class ForumController {

    private final ForumService forumService;
    private final UserService userService;
    private final ForumManageService forumManageService;
    private final PostService postService;
    private final ModelSet modelSet;

    @ModelAttribute("postType")
    public PostType[] postType() {
        return PostType.values();
    }

    @ModelAttribute("forumTypes")
    public ForumType[] forumTypes() {
        return ForumType.values();
    }

    @GetMapping("/new")
    public String forumForm(@ModelAttribute("forumForm") ForumForm form) {
        return "forums/forum-form";
    }

    @PostMapping("/new")
    public String newForum(@ModelAttribute @Validated ForumForm form) {
        Forum forum = new Forum();
        forum.setForumName(form.getForumName());
        forum.setForumType(form.getForumType());
        forumService.createForum(forum);

        return "redirect:/";
    }

    /**
     * Spring JPA 페이징 혹은 직접 구현한 페이징 클래스를 이용할 수 있도록
     * 다형성을 이용한 리팩토링 필요
     *
     * @param forumId
     * @param loginUser
     * @param postFeedForm
     * @param request
     * @param page
     * @param model
     * @return
     * @throws ObjectNotExistsException
     */
    @GetMapping("/{id}")
    public String listForum(@PathVariable("id") Long forumId,
                            @Login Users loginUser,
                            @ModelAttribute(name = "postFeedForm") PostFeedForm postFeedForm,
                            HttpServletRequest request,
                            @RequestParam(required = false, defaultValue = "1") Integer page,
                            Model model) throws ObjectNotExistsException {
        Forum forum = forumService.showForumWithManager(forumId);
        PagingCallBack postsByForumAndCallBack =
                () -> postService.showPostsByForum(forum, PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "id")));
        return modelSet.listPostsTemplate(forumService.showCategories(forumId), postFeedForm, loginUser, request, model, forum, postsByForumAndCallBack);
    }


    @GetMapping("/{forumId}/manager")
    public String managerForm(@ModelAttribute("managerForm") ManagerForm managerForm,
                              @PathVariable Long forumId,
                              Model model) {
        model.addAttribute("forumId", forumId);
        return "forums/manager-form";
    }

    @PostMapping("/{forumId}/manager")
    public String userToManager(@PathVariable Long forumId,
                                @ModelAttribute @Validated ManagerForm managerForm,
                                BindingResult bindingResult) throws ObjectNotExistsException {
        Optional<Users> user = userService.searchUser(managerForm.getUserId());

        String errorUrl = userExistCheck(managerForm, bindingResult, user);
        if (errorUrl != null) return errorUrl;

        Forum forum = forumService.getForum(forumId);
        forumManageService.updateUserToManager(user.get(), forum);

        return "redirect:/";
    }

    private String userExistCheck(ManagerForm managerForm, BindingResult bindingResult, Optional<Users> user) {
        if (StringUtils.hasText(managerForm.getUserId()) && !user.isPresent()) {
            bindingResult.rejectValue("userId", "userId", "UserNotFound");
        }

        if (bindingResult.hasErrors()) {
            log.info("bindingResult={}", bindingResult);
            return "forums/manager-form";
        }
        return null;
    }
}
