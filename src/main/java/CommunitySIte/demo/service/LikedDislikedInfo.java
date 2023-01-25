package CommunitySIte.demo.service;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.jodah.expiringmap.ExpiringMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static net.jodah.expiringmap.ExpirationPolicy.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikedDislikedInfo {
    protected Map<Long, AddressAndPost> liked = ExpiringMap.builder()
            .expirationPolicy(CREATED)
            .expiration(1, TimeUnit.DAYS)
            .build();
    ;
    protected Map<Long, AddressAndPost> disLiked = ExpiringMap.builder()
            .expirationPolicy(CREATED)
            .expiration(1, TimeUnit.DAYS)
            .build();
    ;
    private static LikedDislikedInfo likedDislikedInfo = new LikedDislikedInfo();

    private static long likedSeqNum = 0;
    private static long disLikedSeqNum = 0;

    public  Map<Long, AddressAndPost> getLiked() {
        return liked;
    }

    public  Map<Long, AddressAndPost> getDisLiked() {
        return disLiked;
    }

    public static LikedDislikedInfo getInstance() {
        return likedDislikedInfo;
    }
    public  void like(String address, Long postId) {
        liked.put(likedSeqNum++, new AddressAndPost(address, postId));
    }

    public  void disLike(String address, Long postId) {
        disLiked.put(disLikedSeqNum++, new AddressAndPost(address, postId));
    }

    public  boolean hasUserLiked(String address, Long postId) {
        return liked.containsValue(new AddressAndPost(address, postId));
    }

    public  boolean hasUserDisLiked(String address, Long postId) {
        return disLiked.containsValue(new AddressAndPost(address, postId));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class AddressAndPost {
        String address;
        Long postId;
    }
}
