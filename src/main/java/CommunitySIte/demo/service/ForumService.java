package CommunitySIte.demo.service;

import CommunitySIte.demo.domain.*;
import CommunitySIte.demo.exception.IllegalSetForumManagerException;
import CommunitySIte.demo.exception.ObjectNotExistsException;
import CommunitySIte.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ForumService {

    private final ForumJpaRepository forumRepository;

    public Forum createForum(Forum forum) {
        return forumRepository.save(forum);
    }

    @Transactional(readOnly = true)
    public Forum getForum(Long id) throws ObjectNotExistsException {
        Optional<Forum> forum = forumExistenceCheck(id);
        return forum.get();
    }

    @Transactional(readOnly = true)
    public List<Forum> getForums() {
        return forumRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Post> getPosts(Long id) throws ObjectNotExistsException {
        Optional<Forum> forum = forumExistenceCheck(id);
        return forum.get().getPosts();
    }

    public void setManager(Long forumId,final Users user) throws ObjectNotExistsException, IllegalSetForumManagerException {
        Forum forum = forumExistenceCheck(forumId).get();
        if(forum.getForumType()!=ForumType.MINOR) throw new IllegalSetForumManagerException();
        forum.addManager(user);
    }



    @Transactional(readOnly = true)
    public List<Category> showCategories(Long forumId) throws ObjectNotExistsException {
        Optional<Forum> forum = forumExistenceCheck(forumId);
        return forum.get().getCategories();
    }

    public void addCategory(Long forumId, String categoryName) throws ObjectNotExistsException {
        Optional<Forum> forum = forumExistenceCheck(forumId);
        forum.get().addCategory(new Category(categoryName));
    }


    public Forum showForumWithManager(Long forumId) throws ObjectNotExistsException {
        Optional<Forum> forum = forumRepository.findForumAndManager(forumId);
        if(!forum.isPresent()) throw new ObjectNotExistsException();
        else return forum.get();
    }

    //template
    private Optional<Forum> forumExistenceCheck(Long id) throws ObjectNotExistsException {
        Optional<Forum> forum = forumRepository.findById(id);
        if(!forum.isPresent()) throw new ObjectNotExistsException();
        return forum;
    }
}
