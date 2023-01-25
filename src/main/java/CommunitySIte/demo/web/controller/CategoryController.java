package CommunitySIte.demo.web.controller;

import CommunitySIte.demo.domain.*;
import CommunitySIte.demo.exception.NotAuthorizedException;
import CommunitySIte.demo.exception.ObjectNotExistsException;
import CommunitySIte.demo.service.CategoryService;
import CommunitySIte.demo.service.ForumService;
import CommunitySIte.demo.service.PostService;
import CommunitySIte.demo.service.page.PagingCallBack;
import CommunitySIte.demo.web.argumentresolver.Login;
import CommunitySIte.demo.web.controller.form.CategoryForm;
import CommunitySIte.demo.web.controller.form.PostFeedForm;
import CommunitySIte.demo.web.modelset.ModelSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static CommunitySIte.demo.web.controller.problemcheck.AccessibilityChecker.isUserManagerOrAdmin;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/forum/{forumId}/category")
public class CategoryController {

    private final CategoryService categoryService;

    private final ForumService forumService;
    private final PostService postService;
    private final ModelSet modelSet;
    @ModelAttribute("postType")
    public PostType[] postType() {
        return PostType.values();
    }

    @ModelAttribute("user")
    public Users user(@Login Users loginUser) {
        return loginUser;
    }

    @GetMapping("/{categoryId}")
    public String showPostsByCategory(@PathVariable Long forumId,
                                      @PathVariable Long categoryId,
                                      PostFeedForm postForm,
                                      @RequestParam(required = false, defaultValue = "1") Integer page,
                                      @Login Users loginUser,
                                      HttpServletRequest request,
                                      Model model) throws ObjectNotExistsException {
        Category category = categoryService.findWithForumAndPosts(categoryId);
        Forum forum = category.getForum();
        PagingCallBack postsByForumAndCategoryCallBack =
                () -> postService.showPostsByForumAndCategory(forum, category, PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "id")));
        return modelSet.listPostsTemplate(forumService.showCategories(forumId), postForm,loginUser, request, model, forum, postsByForumAndCategoryCallBack);
    }

    @GetMapping("/new")
    public String addCategoryForm(@PathVariable Long forumId,
                                  @Login Users loginUser,
                                  Model model) throws ObjectNotExistsException {
        Forum forum = forumService.getForum(forumId);

        if (!isUserManagerOrAdmin(loginUser, forum)) {
            throw new NotAuthorizedException("매니저만 카테고리 추가가능");
        }

        model.addAttribute("categoryForm", new CategoryForm());
        return "categories/addCategoryForm";
    }

    @GetMapping("/delete")
    public String delete(@PathVariable Long forumId,
                                  @RequestParam Long categoryId,
                                  @Login Users loginUser) throws ObjectNotExistsException {
        Forum forum = forumService.getForum(forumId);

        if (!isUserManagerOrAdmin(loginUser, forum)) {
            throw new NotAuthorizedException("매니저만 카테고리 삭제가능");
        }

        categoryService.deleteCategory(categoryId);
        return "redirect:/forum/{forumId}";
    }

    @PostMapping("/new")
    public String addCategory(@PathVariable Long forumId,
                              @Login Users loginUser,
                              @ModelAttribute @Validated CategoryForm categoryForm,
                              BindingResult bindingResult) throws ObjectNotExistsException {
        Forum forum = forumService.getForum(forumId);

        if (!isUserManagerOrAdmin(loginUser, forum)) {
            throw new NotAuthorizedException("매니저만 카테고리 추가가능");
        }

        if(bindingResult.hasErrors()){
            return "categories/addCategoryForm";
        }

        categoryService.newCategory(forum, categoryForm.getCategoryName());
        return "redirect:/forum/{forumId}";
    }

}
