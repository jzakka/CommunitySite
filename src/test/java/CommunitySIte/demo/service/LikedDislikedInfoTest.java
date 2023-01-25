package CommunitySIte.demo.service;

import net.jodah.expiringmap.ExpiringMap;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import static net.jodah.expiringmap.ExpirationPolicy.CREATED;

class LikedDislikedInfoTest {

    LikedDislikedInfo likedDislikedInfo = LikedDislikedInfo.getInstance();

    private class TestLikedDislikedInfo extends LikedDislikedInfo {
        {
            liked = ExpiringMap.builder()
                    .expirationPolicy(CREATED)
                    .expiration(1, TimeUnit.SECONDS)
                    .build();
            disLiked = ExpiringMap.builder()
                    .expirationPolicy(CREATED)
                    .expiration(1, TimeUnit.SECONDS)
                    .build();
        }
    }


    @Test
    void hasUserLiked() {
        likedDislikedInfo.like("address", 1l);
        Assertions.assertThat(likedDislikedInfo.hasUserLiked("address", 1l)).isTrue();
    }

    @Test
    void hasUserDisLiked() {
        likedDislikedInfo.disLike("address", 1l);
        Assertions.assertThat(likedDislikedInfo.hasUserDisLiked("address", 1l)).isTrue();
    }

    @Test
    void expire() throws InterruptedException {
        LikedDislikedInfo instance = new TestLikedDislikedInfo();
        instance.like("address", 1l);
        Assertions.assertThat(instance.getLiked().size()).isEqualTo(1);
        Thread.sleep(1500);
        Assertions.assertThat(instance.getLiked().size()).isEqualTo(0);
        instance.disLike("address", 1l);
        Assertions.assertThat(instance.getDisLiked().size()).isEqualTo(1);
        Thread.sleep(1500);
        Assertions.assertThat(instance.getDisLiked().size()).isEqualTo(0);
    }
}