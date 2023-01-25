package CommunitySIte.demo.repository;

import CommunitySIte.demo.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByAnonymousUserName(String name);

    @Query("select c from Comment c where c.user.userName = :username ")
    List<Comment> findByUserName(@Param("username") String name);
}
