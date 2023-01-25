package CommunitySIte.demo.service;

import CommunitySIte.demo.domain.*;
import CommunitySIte.demo.exception.ObjectNotExistsException;
import CommunitySIte.demo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    PostService postService;
    @Autowired
    PostJpaRepository postJpaRepository;
    @Autowired
    ForumService forumService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CommentService commentService;

    Forum forum;
    Long categoryId;
    Users user;
    Post post1;
    Post post2;

    @BeforeEach
    void setUp() {
        Forum forum = new Forum();
        this.forum = forumService.createForum(forum);
        categoryId = categoryService.newCategory(forum, "newCategory");
        Category category = categoryService.findOne(categoryId);
        user = new Users();
        userRepository.save(user);
        post1 = postService.feedPost(forum, "유동글", null, "ㅇㅇ", category, "1111", "1234");
        post2 = postService.feedPost(forum, "고닉글", null, user, category, "1234");
    }

    @Test
    @Transactional
    void feedPost() {
        assertThat(postJpaRepository.count()).isEqualTo(2);
    }

    @Test
    void findPost() throws ObjectNotExistsException {
        Post findPost1 = postService.findPost(post1.getId());
        assertThat(findPost1).isEqualTo(post1);
        Post findPost2 = postService.findPost(post2.getId());
        assertThat(findPost2).isEqualTo(post2);
    }

    @Test
    void showPost() throws ObjectNotExistsException {
        Post findPost1 = postService.showPost(post1.getId());
        assertThat(findPost1.getViews()).isEqualTo(1);
        for (int i = 0; i < 12; i++) {
            postService.showPost(post2.getId());
        }
        Post findPost2 = postService.showPost(post2.getId());
        assertThat(findPost2.getViews()).isEqualTo(13);
    }

    @Test
    void showPostByTitle() {
        List<Post> findPost1 = postService.showPostByTitle(post1.getTitle());
        assertThat(findPost1.size()).isEqualTo(1);
        assertThat(findPost1.get(0)).isEqualTo(post1);
        List<Post> findPost2 = postService.showPostByTitle(post2.getTitle());
        assertThat(findPost2.size()).isEqualTo(1);
        assertThat(findPost2.get(0)).isEqualTo(post2);
    }

    @Test
    void showAllPost() {
        List<Post> posts = postService.showAllPost();
        assertThat(posts).contains(post1, post2);
        assertThat(posts.size()).isEqualTo(2);
    }

    @Test
    void like() throws ObjectNotExistsException {
        for (int i = 0; i < 7; i++) {
            postService.like(post1.getId());
            postService.like(post2.getId());
        }
        assertThat(post1.getGood()).isEqualTo(7);
        assertThat(post2.getGood()).isEqualTo(7);
    }

    @Test
    void dislike() throws ObjectNotExistsException {
        for (int i = 0; i < 7; i++) {
            postService.dislike(post1.getId());
            postService.dislike(post2.getId());
        }
        assertThat(post1.getBad()).isEqualTo(7);
        assertThat(post2.getBad()).isEqualTo(7);
    }

    @Test
    void update() throws ObjectNotExistsException {
        postService.update(post1.getId(), "변경된 제목", null, "변경된 내용");
        postService.update(post2.getId(), "변경된 제목", null, "변경된 내용");
        assertThat(post1.getTitle()).isEqualTo("변경된 제목");
        assertThat(post2.getTitle()).isEqualTo("변경된 제목");
        assertThat(post1.getContent()).isEqualTo("변경된 내용");
        assertThat(post2.getContent()).isEqualTo("변경된 내용");
    }

    @Test
    void delete() {
        postService.delete(post1);
        postService.delete(post2);
        assertThat(postService.showAllPost().size()).isEqualTo(0);
    }


    @Test
    void findPostAndForum() throws ObjectNotExistsException {
        Post post = postService.showPostWithForum(post1.getId());
        assertThat(post.getForum()).isEqualTo(this.forum);
    }

    /**
     * comment 생성이 post와의 연관관계와 동기화되지 않으므로 아래 테스트는 실패한다.
     * comment service를 수정하면 해결가능할 것으로 예상
     */

    @Test
    void showComments() throws ObjectNotExistsException {
        commentService.writeComment(user, post1, "댓글");
        List<Comment> comments = postService.showComments(post1.getId());
        assertThat(comments.size()).isEqualTo(1);
    }

    @Test
    void showPostWithComment() throws ObjectNotExistsException {
        commentService.writeComment(user, post1, "댓글");
        Post postAndComments = postService.showPostWithComment(post1.getId());
        assertThat(postAndComments.getComments().get(0).getContent()).isEqualTo("댓글");
        /*System.out.println("postAndComments = " + postAndComments);
        assertThat(postAndComments).isEqualTo(post1);*/
    }

    @Test
    void showPostsByForum() {
        Category category = categoryService.findOne(categoryId);
        postService.feedPost(forum, "유동글", null, "ㅇㅇ", category, "1111", "1234");
        postService.feedPost(forum, "유동글", null, "ㅇㅇ", category, "1111", "1234");
        postService.feedPost(forum, "유동글", null, "ㅇㅇ", category, "1111", "1234");
        postService.feedPost(forum, "유동글", null, "ㅇㅇ", category, "1111", "1234");
        postService.feedPost(forum, "유동글", null, "ㅇㅇ", category, "1111", "1234");
        postService.feedPost(forum, "유동글", null, "ㅇㅇ", category, "1111", "1234");
        postService.feedPost(forum, "유동글", null, "ㅇㅇ", category, "1111", "1234");
        postService.feedPost(forum, "유동글", null, "ㅇㅇ", category, "1111", "1234");

        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "lastModifiedDate"));
        Page<Post> posts = postService.showPostsByForum(forum, pageRequest);

        List<Post> content = posts.getContent();
        assertThat(content.size()).isEqualTo(5);
        assertThat(posts.getTotalElements()).isEqualTo(10);
        assertThat(posts.getNumber()).isEqualTo(0);
        assertThat(posts.getTotalPages()).isEqualTo(2);
        assertThat(posts.isFirst()).isTrue();
        assertThat(posts.hasNext()).isTrue();
    }

    @Test
    void showPostsByForumAndCategory() {
        Category category = categoryService.findOne(categoryId);
        postService.feedPost(forum, "유동글", null, "ㅇㅇ", category, "1111", "1234");
        postService.feedPost(forum, "유동글", null, "ㅇㅇ", category, "1111", "1234");
        postService.feedPost(forum, "유동글", null, "ㅇㅇ", category, "1111", "1234");
        postService.feedPost(forum, "유동글", null, "ㅇㅇ", category, "1111", "1234");
        postService.feedPost(forum, "유동글", null, "ㅇㅇ", category, "1111", "1234");
        postService.feedPost(forum, "유동글", null, "ㅇㅇ", category, "1111", "1234");
        postService.feedPost(forum, "유동글", null, "ㅇㅇ", category, "1111", "1234");
        postService.feedPost(forum, "유동글", null, "ㅇㅇ", category, "1111", "1234");
        postService.feedPost(forum, "유동글", null, "ㅇㅇ", category, "1111", "1234");
        postService.feedPost(forum, "유동글", null, "ㅇㅇ", category, "1111", "1234");
        postService.feedPost(forum, "유동글", null, "ㅇㅇ", category, "1111", "1234");

        PageRequest pageRequest = PageRequest.of(2, 5, Sort.by(Sort.Direction.DESC, "lastModifiedDate"));
        Page<Post> posts = postJpaRepository.findByForumAndCategory(forum, category, pageRequest);

        List<Post> content = posts.getContent();
        assertThat(content.size()).isEqualTo(3);
        assertThat(posts.getTotalElements()).isEqualTo(13);
        assertThat(posts.getNumber()).isEqualTo(2);
        assertThat(posts.getTotalPages()).isEqualTo(3);
        assertThat(posts.isFirst()).isFalse();
        assertThat(posts.isLast()).isTrue();
        assertThat(posts.hasNext()).isFalse();
    }
}