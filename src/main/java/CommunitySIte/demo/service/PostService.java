package CommunitySIte.demo.service;

import CommunitySIte.demo.domain.*;
import CommunitySIte.demo.domain.file.FileStore;
import CommunitySIte.demo.domain.file.UploadFile;
import CommunitySIte.demo.exception.ObjectNotExistsException;
import CommunitySIte.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final FileStore fileStore;
    private final PostJpaRepository postJpaRepository;

    public Post feedPost(Forum forum, String title, UploadFile imageFile, String anonymousUsername, Category category,
                         String password, String content) {
        Post post = Post.createPost(forum, title, imageFile, category, anonymousUsername, password, content);
        return postJpaRepository.save(post);
    }

    public Post feedPost(Forum forum, String title, UploadFile imageFile, Users user, Category category, String content) {
        Post post = Post.createPost(forum, title, imageFile, user, category, content);
        return postJpaRepository.save(post);
    }

    public Post feedPost(Users loginUser, Forum forum, String title, UploadFile uploadFile, String content, Category category, String password, String username) {
        if ((loginUser == null)) {
            return feedPost(forum, title, uploadFile, username,
                    category, password, content);
        } else {
           return feedPost(forum, title, uploadFile, loginUser,
                    category, content);
        }
    }

    @Transactional(readOnly = true)
    public Post findPost(Long id) throws ObjectNotExistsException {
        Optional<Post> post = postJpaRepository.findById(id);
        PostQueryExecuter postQueryExecuter = () -> {
            ;
        };
        postExistenceCheckAndExecute(post, postQueryExecuter);
        return post.get();
    }

    public Post showPost(Long id) throws ObjectNotExistsException {
        Optional<Post> post = postJpaRepository.findById(id);
        PostQueryExecuter postQueryExecuter = () -> post.get().increaseViews();
        postExistenceCheckAndExecute(post, postQueryExecuter);
        return post.get();
    }

    @Transactional(readOnly = true)
    public List<Post> showPostByTitle(String title) {
        return postJpaRepository.findByTitle(title);
    }

    @Transactional(readOnly = true)
    public List<Post> showAllPost() {
        return postJpaRepository.findAll();
    }

    public void like(Long id) throws ObjectNotExistsException {
        Optional<Post> post = postJpaRepository.findById(id);
        PostQueryExecuter postQueryExecuter = () -> post.get().increaseGood();
        postExistenceCheckAndExecute(post, postQueryExecuter);
    }

    public void dislike(Long id) throws ObjectNotExistsException {
        Optional<Post> post = postJpaRepository.findById(id);
        PostQueryExecuter postQueryExecuter = () -> post.get().increaseBad();
        postExistenceCheckAndExecute(post, postQueryExecuter);
    }

    public void update(Long id, String title, UploadFile imageFile, String content) throws ObjectNotExistsException {
        Optional<Post> post = postJpaRepository.findById(id);
        PostQueryExecuter postQueryExecuter = () -> {
            try {
                new File(fileStore.getFullPath(post.get().getImageFile().getStoreFileName())).delete();
                log.info("기존 파일이 삭제되었습니다. 기존 파일 명: {}", fileStore.getFullPath(post.get().getImageFile().getUploadFileName()));
            } catch (NullPointerException e) {
                log.info("삭제할 파일 없음");
            }
            post.get().update(title, imageFile, content);};
        postExistenceCheckAndExecute(post, postQueryExecuter);
    }

    @Transactional(readOnly = true)
    public List<Comment> showComments(Long id) throws ObjectNotExistsException {
        Optional<Post> post = postJpaRepository.findPostAndComments(id);
        PostQueryExecuter postQueryExecuter = () -> {;};
        postExistenceCheckAndExecute(post, postQueryExecuter);
        return post.get().getComments();
    }

   /* @Transactional(readOnly = true)
    public List<Post> searchByPattern(String pattern) {
        return postJpaRepository.findByPattern(pattern);
    }*/

    public boolean delete(Post post) {
        boolean isFileDeleted = false;
        try {
            isFileDeleted = new File(fileStore.getFullPath(post.getImageFile().getStoreFileName())).delete();
        } catch (NullPointerException e) {
            log.info("삭제할 파일 없음");
        } finally {
            post.getForum().getPosts().remove(post);
            post.getCategory().getPosts().remove(post);
            postJpaRepository.delete(post);
        }
        return isFileDeleted;
    }

    public Post showPostWithComment(Long postId) throws ObjectNotExistsException {
        Optional<Post> post = postJpaRepository.findPostAndComments(postId);
        PostQueryExecuter postQueryExecuter = () -> {;};
        postExistenceCheckAndExecute(post, postQueryExecuter);
        return post.get();
    }

    public Post showPostWithForum(Long postId) throws ObjectNotExistsException {
        Optional<Post> post = postJpaRepository.findPostAndForum(postId);
        PostQueryExecuter postQueryExecuter = () -> {;};
        postExistenceCheckAndExecute(post, postQueryExecuter);
        return post.get();
    }

    public Page<Post> showPostsByForum(Forum forum, Pageable pageable) {
        return postJpaRepository.findByForum(forum, pageable);
    }

    public Page<Post> showPostsByForumAndCategory(Forum forum, Category category, Pageable pageable) {
        return postJpaRepository.findByForumAndCategory(forum, category, pageable);
    }

    static interface PostQueryExecuter {
        public void execute();
    }
    public static void postExistenceCheckAndExecute(Optional<Post> post, PostQueryExecuter postQueryExecuter) throws ObjectNotExistsException {
        if(post.isPresent()) postQueryExecuter.execute();
        else throw new ObjectNotExistsException("존재하지 않는 게시글");
    }
}
