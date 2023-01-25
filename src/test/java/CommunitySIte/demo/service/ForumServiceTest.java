package CommunitySIte.demo.service;

import CommunitySIte.demo.domain.Category;
import CommunitySIte.demo.domain.Forum;
import CommunitySIte.demo.domain.ForumType;
import CommunitySIte.demo.domain.Users;
import CommunitySIte.demo.exception.IllegalSetForumManagerException;
import CommunitySIte.demo.exception.ObjectNotExistsException;
import CommunitySIte.demo.repository.CategoryRepository;
import CommunitySIte.demo.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ForumServiceTest {

    @Autowired
    ForumService forumService;
    @Autowired
    PostService postService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CategoryRepository categoryRepository;

    Forum mainForum = null;
    Forum minorForum = null;

    Users user = null;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        categoryRepository.save(category);

        Forum mainForum = new Forum();
        mainForum.setForumName("mainForum");
        mainForum.setForumType(ForumType.MAIN);
        mainForum.getCategories().add(category);
        this.mainForum = forumService.createForum(mainForum);

        user = new Users();
        userRepository.save(user);
        postService.feedPost(mainForum, "유동글", null, "ㅇㅇ", category, "1111", "1234");
        postService.feedPost(mainForum, "고닉글", null, user, category, "1234");

        Forum minorForum = new Forum();
        minorForum.setForumName("minorForum");
        minorForum.setForumType(ForumType.MINOR);
        minorForum.getCategories().add(category);
        this.minorForum = forumService.createForum(minorForum);

        postService.feedPost(minorForum, "유동글", null, "ㅇㅇ", category, "1111", "1234");
        postService.feedPost(minorForum, "고닉글", null, user, category, "1234");


    }

    private void checkForumEqual(Forum forum1, Forum forum2) {
        assertThat(forum1.getForumName()).isEqualTo(forum2.getForumName());
        assertThat(forum1.getForumType()).isEqualTo(forum2.getForumType());
        assertThat(forum1.getForumManagers()).isEqualTo(forum2.getForumManagers());
        assertThat(forum1.getCategories()).isEqualTo(forum2.getCategories());
        assertThat(forum1.getPosts()).isEqualTo(forum2.getPosts());
        assertThat(forum1.getId()).isEqualTo(forum2.getId());
    }

    @Test
    void getForum() throws ObjectNotExistsException {
        Forum mainForum = forumService.getForum(this.mainForum.getId());
        Forum minorForum = forumService.getForum(this.minorForum.getId());

        checkForumEqual(mainForum, this.mainForum);
        checkForumEqual(minorForum, this.minorForum);
    }

    @Test
    void getForums() {
        List<Forum> forums = forumService.getForums();
        assertThat(forums.size()).isEqualTo(2);
    }

    @Test
    void getPosts() {
        assertThat(mainForum.getPosts().size()).isEqualTo(2);
        assertThat(minorForum.getPosts().size()).isEqualTo(2);
    }

    @Test
    void setManager() throws ObjectNotExistsException, IllegalSetForumManagerException {
        forumService.setManager(minorForum.getId(), user);
        assertThat(minorForum.getForumManagers().get(0).getUser()).isEqualTo(user);
    }

    @Test
    void setManagerToMainForum(){
        assertThrows(IllegalSetForumManagerException.class, () -> {
            forumService.setManager(mainForum.getId(), user);
        });
    }

    @Test
    void showCategories() throws ObjectNotExistsException {
        assertThat(forumService.showCategories(mainForum.getId()).size()).isEqualTo(2);
        assertThat(forumService.showCategories(minorForum.getId()).size()).isEqualTo(2);
        assertThat(mainForum.getCategories().get(0).getCategoryName()).isEqualTo("FreeBoard");
        assertThat(minorForum.getCategories().get(0).getCategoryName()).isEqualTo("FreeBoard");
    }

    @Test
    void addCategory() throws ObjectNotExistsException {
        forumService.addCategory(mainForum.getId(), "newCategory");
        List<Category> mainForumCategories = mainForum.getCategories();
        assertThat(mainForumCategories.size()).isEqualTo(3);
        assertThat(mainForumCategories.get(mainForumCategories.size()-1).getCategoryName()).isEqualTo("newCategory");
    }

    @Test
    void showForumWithManager() throws ObjectNotExistsException {
        Forum mainForum = forumService.showForumWithManager(this.mainForum.getId());
        mainForum.getForumManagers();
        Forum minorForum = forumService.showForumWithManager(this.minorForum.getId());
        minorForum.getForumManagers();
    }
}