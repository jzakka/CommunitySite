package CommunitySIte.demo.service.page;

import CommunitySIte.demo.domain.Post;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

@Slf4j
@Getter
@Setter
public class PageInfoJpa implements PageInfo{

    private int start;
    private int end;
    private boolean prev;
    private boolean next;


    private final double DISPLAY_PAGE_NUMBER = 10.0;

    public PageInfoJpa(Page<Post> pagePost) {
        infoCalculate(pagePost);
    }

    private void infoCalculate(Page<Post> pagePost) {
        if (pagePost.isEmpty()) {
            start = end = 1;
            return;
        }

        end = (int) (Math.ceil((pagePost.getNumber()+1) /  DISPLAY_PAGE_NUMBER) * DISPLAY_PAGE_NUMBER);
        start = (end - (int)DISPLAY_PAGE_NUMBER) + 1;
        start = start <= 0 ? 1 : start;

        int tempEnd = (int) (Math.ceil(pagePost.getTotalElements() / (double) pagePost.getSize()));
        end = end > tempEnd ? tempEnd : end;
        prev = start!=1;
        next = end*pagePost.getSize()<pagePost.getTotalElements();
        log.debug("start = {}, end = {}, prev = {}, next = {}, pagePost.getNumber() = {}, pagePost.getTotalElements()={}, pagePost.getSize()={}",start,end,prev,next,
                pagePost.getNumber(),pagePost.getTotalElements(),pagePost.getSize());
    }
}
