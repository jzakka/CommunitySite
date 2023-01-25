package CommunitySIte.demo.repository;

import CommunitySIte.demo.domain.Category;
import CommunitySIte.demo.domain.Forum;
import CommunitySIte.demo.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostJpaRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"comments"})
    @Query("select p from Post p where p.id=:postId")
    Optional<Post> findPostAndComments(@Param("postId") Long postId);

    @EntityGraph(attributePaths = {"forum"})
    @Query("select p from Post p where p.id=:postId")
    Optional<Post> findPostAndForum(@Param("postId") Long postId);

    List<Post> findByTitle(String title);

    @EntityGraph(attributePaths = {"comments"})
    Page<Post> findByForum(Forum forum, Pageable pageable);

    Page<Post> findByForumAndCategory(Forum forum, Category category, Pageable pageable);
}
