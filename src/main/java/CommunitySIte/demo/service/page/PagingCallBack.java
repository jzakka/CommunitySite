package CommunitySIte.demo.service.page;


import CommunitySIte.demo.domain.Post;
import org.springframework.data.domain.Page;

public interface PagingCallBack {
    Page<Post> page();
}
