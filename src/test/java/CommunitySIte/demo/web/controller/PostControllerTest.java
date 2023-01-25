package CommunitySIte.demo.web.controller;

import CommunitySIte.demo.domain.file.FileStore;
import CommunitySIte.demo.exception.ObjectNotExistsException;
import CommunitySIte.demo.repository.CategoryRepository;
import CommunitySIte.demo.service.ForumService;
import CommunitySIte.demo.service.LikedDislikedInfo;
import CommunitySIte.demo.service.PostService;
import CommunitySIte.demo.web.modelset.ModelSet;
import net.jodah.expiringmap.ExpiringMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.concurrent.TimeUnit;

import static net.jodah.expiringmap.ExpirationPolicy.CREATED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PostController.class)
class PostControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    PostService postService;

    @MockBean
    ForumService forumService;

    @MockBean
    FileStore fileStore;
    @MockBean
    CategoryRepository categoryRepository;
    @MockBean
    ModelSet modelSet;

    @Test
    void good() throws Exception {
        LikedDislikedInfo likedDislikedInfo = LikedDislikedInfo.getInstance();
        mvc.perform(get("/forum/1/post/2/good")).andExpect(MockMvcResultMatchers.status().is3xxRedirection());
        mvc.perform(get("/forum/1/post/2/good")).andExpect(MockMvcResultMatchers.status().is3xxRedirection());
        verify(postService, times(1)).like(2l);
    }

    @Test
    void bad() throws Exception {
        LikedDislikedInfo likedDislikedInfo = LikedDislikedInfo.getInstance();
        mvc.perform(get("/forum/1/post/2/bad")).andExpect(MockMvcResultMatchers.status().is3xxRedirection());
        mvc.perform(get("/forum/1/post/2/bad")).andExpect(MockMvcResultMatchers.status().is3xxRedirection());
        verify(postService, times(1)).dislike(2l);
    }
}