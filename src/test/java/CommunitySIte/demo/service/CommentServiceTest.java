package CommunitySIte.demo.service;

import CommunitySIte.demo.domain.*;
import CommunitySIte.demo.exception.ObjectNotExistsException;
import CommunitySIte.demo.repository.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentServiceTest {
    @Autowired
    PostService postService;
    @Autowired
    PostJpaRepository postJpaRepository;
    @Autowired
    ForumJpaRepository forumRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CommentService commentService;

    Long forumId;
    Long categoryId;
    Users user;
    Post post1;
    Post post2;

    Comment comment1 = null;
    Comment comment2 = null;
    Comment comment3 = null;
    Comment comment4 = null;


    String[] post1Comments = {"유동 글 고닉 댓", "유동 글 유동 댓"};
    String[] post2Comments = {"고닉 글 고닉 댓", "고닉 글 유동 댓"};
    @BeforeEach
    void setUp() {
        Forum forum = new Forum();
        forumRepository.save(forum);
        forumId = forum.getId();
        Category category = new Category();
        categoryRepository.save(category);
        categoryId = category.getId();
        user = new Users();
        user.setUserName("고닉");
        userRepository.save(user);
        post1 = postService.feedPost(forum, "유동글", null, "ㅇㅇ", category, "1111", "1234");
        post2 = postService.feedPost(forum, "고닉글", null, user, category, "1234");

        comment1 = commentService.writeComment(user, post1, "유동 글 고닉 댓");
        comment2 = commentService.writeComment(user, post2, "고닉 글 고닉 댓");
        comment3 = commentService.writeComment("ㅇㅇ","1234", post1, "유동 글 유동 댓");
        comment4 = commentService.writeComment("ㅇㅇ", "1234", post2, "고닉 글 유동 댓");
    }

    @Test
    void writeComment() {
        Assertions.assertThat(post1.getComments().size()).isEqualTo(2);
        Assertions.assertThat(post2.getComments().size()).isEqualTo(2);

        for (int i = 0; i < post1Comments.length; i++) {
            Assertions.assertThat(post1.getComments().get(i).getContent()).isEqualTo(post1Comments[i]);
        }
        for (int i = 0; i < post2Comments.length; i++) {
            Assertions.assertThat(post2.getComments().get(i).getContent()).isEqualTo(post2Comments[i]);
        }
    }

    @Test
    void findComment() throws ObjectNotExistsException {
        Comment findComment = commentService.findComment(comment1.getId());
        Assertions.assertThat(findComment).isEqualTo(comment1);
    }

    @Test
    void findCommentInvalidId() {
        assertThrows(ObjectNotExistsException.class, () -> {
            Comment findComment = commentService.findComment(0L);
        });
    }

    @Test
    void findByWriter() {
        List<Comment> userComments = commentService.findByWriter("고닉");
        Assertions.assertThat(userComments.get(0)).isEqualTo(comment1);
        Assertions.assertThat(userComments.get(1)).isEqualTo(comment2);
        List<Comment> anonymousComments = commentService.findByWriter("ㅇㅇ");
        Assertions.assertThat(anonymousComments.get(0)).isEqualTo(comment3);
        Assertions.assertThat(anonymousComments.get(1)).isEqualTo(comment4);
    }

    @Test
    void delete() {
        commentService.delete(comment1);
        commentService.delete(comment2);
        commentService.delete(comment3);
        commentService.delete(comment4);

        Assertions.assertThat(post1.getComments().size()).isEqualTo(0);
        Assertions.assertThat(post2.getComments().size()).isEqualTo(0);
    }
}