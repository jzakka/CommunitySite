package CommunitySIte.demo.repository;

import CommunitySIte.demo.domain.Forum;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ForumJpaRepository extends JpaRepository<Forum, Long> {

    @EntityGraph(attributePaths = {"forumManagers"})
    @Query("select f from Forum f where f.id=:forumId")
    Optional<Forum> findForumAndManager(@Param("forumId") Long forumId);



}
