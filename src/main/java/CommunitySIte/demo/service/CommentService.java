package CommunitySIte.demo.service;

import CommunitySIte.demo.domain.Comment;
import CommunitySIte.demo.domain.Post;
import CommunitySIte.demo.domain.Users;
import CommunitySIte.demo.exception.ObjectNotExistsException;
import CommunitySIte.demo.repository.CommentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static CommunitySIte.demo.domain.Comment.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentJpaRepository commentRepository;

    @Transactional
    public Comment writeComment(Users user, Post post, String content) {
        return commentRepository.save(createComment(user, post, content));
    }

    @Transactional
    public Comment writeComment(String username, String password, Post post, String content) {
        return commentRepository.save(createComment(username, password, post, content));
    }

    @Transactional
    public Comment writeComment(Users user, String username, String password, Post post, String content) {
        if ((user == null)) {
            return writeComment(username, password, post, content);
        } else {
            return writeComment(user, post, content);
        }
    }

    public Comment findComment(Long id) throws ObjectNotExistsException {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isPresent()) return comment.get();
        else throw new ObjectNotExistsException("존재하지 않는 댓글");
    }

    public List<Comment> findByWriter(String name) {
        List<Comment> comments = commentRepository.findByUserName(name);
        comments.addAll(commentRepository.findByAnonymousUserName(name));
        return comments;
    }

    @Transactional
    public void delete(Comment comment) {
        comment.getPost().getComments().remove(comment);
        commentRepository.delete(comment);
    }
}
