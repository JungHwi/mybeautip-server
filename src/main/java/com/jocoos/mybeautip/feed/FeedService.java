package com.jocoos.mybeautip.feed;

import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class FeedService {

    private static final String MEMBER_KEY = "member:%s";
    private static final String FEED_KEY = "feed:%s";

    private final JedisPool jedisPool;
    private final FollowingRepository followingRepository;
    private final VideoRepository videoRepository;

    public FeedService(JedisPool jedisPool,
                       FollowingRepository followingRepository,
                       VideoRepository videoRepository) {
        this.jedisPool = jedisPool;
        this.followingRepository = followingRepository;
        this.videoRepository = videoRepository;
    }

    public List<Video> getVideoKeys(Long memberId, String cursor, int count) {
        String key = String.format(FEED_KEY, memberId);
        Jedis jedis = null;
        log.debug("memberId: {}, cursor: {}, count: {}", memberId, cursor, count);
        List<Video> videos = new ArrayList<>();

        try {
            jedis = jedisPool.getResource();
            Set<Tuple> tuples;
            if (StringUtils.isBlank(cursor)) {
                tuples = jedis.zrevrangeByScoreWithScores(key, "+inf", "-inf", 0, count);
            } else {
                tuples = jedis.zrevrangeByScoreWithScores(key, "(" + cursor, "-inf", 0, count);
            }

            for (Tuple t : tuples) {
                videoRepository.findByVideoKeyAndDeletedAtIsNull(t.getElement())
                        .ifPresent(videos::add);
            }
            if (tuples.size() != videos.size()) {
                log.warn("incorrect feed data - memberId: {}, video size: {}, tuples size: {}", memberId, videos.size(), tuples.size());
            }
        } finally {
            if (jedis != null) {
                jedis.disconnect();
                close(jedis);
            }
        }
        return videos;
    }

    @Async
    public void feedVideo(Video video) {
        Long creator = video.getMember().getId();
        List<Following> followers = followingRepository.findByCreatedAtBeforeAndMemberYouId(new Date(), creator);
        log.debug("followers count: {}", followers != null ? followers.size() : 0);

        addMyVideo(String.format(MEMBER_KEY, creator), video);

        List<String> receives = new ArrayList<>(Arrays.asList(String.format(FEED_KEY, creator)));
        for (Following f : followers) {
            receives.add(String.format(FEED_KEY, f.getMemberMe().getId()));
        }

        addVideoLinkFeed(receives, video);
    }

    public void followMember(Long me, Long follower) {
        String followerKey = String.format(MEMBER_KEY, follower);
        String myFeedKey = String.format(FEED_KEY, me);
        copyVideos(followerKey, myFeedKey);
    }

    private void copyVideos(String followerKey, String myFeedKey) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Set<Tuple> tuples = jedis.zrevrangeByScoreWithScores(followerKey, "+inf", "-inf");
            for (Tuple t : tuples) {
                log.debug("copy videos key: {}, element: {}", t.getScore(), t.getElement());
                jedis.zadd(myFeedKey, t.getScore(), t.getElement());
            }
        } finally {
            if (jedis != null) {
                jedis.disconnect();
                close(jedis);
            }
        }
    }

    public void unfollowMember(Long me, Long follower) {
        String followerKey = String.format(MEMBER_KEY, follower);
        String myFeedKey = String.format(FEED_KEY, me);
        log.debug("follower: {}, feed: {}", followerKey, myFeedKey);

        removeVideos(followerKey, myFeedKey);
    }

    private void removeVideos(String followerKey, String myFeedKey) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Set<Tuple> tuples = jedis.zrevrangeByScoreWithScores(followerKey, "+inf", "-inf");
            for (Tuple t : tuples) {
                jedis.zrem(myFeedKey, t.getElement());
            }
        } finally {
            if (jedis != null) {
                jedis.disconnect();
                close(jedis);
            }
        }
    }

    public void feedDeletedVideo(Long videoId) {
        videoRepository.findById(videoId).ifPresent(
                v -> {
                    Long creator = v.getMember().getId();
                    List<Following> followers = followingRepository.findByCreatedAtBeforeAndMemberYouId(new Date(), creator);
                    log.debug("followers: {}", followers != null ? followers.size() : 0);

                    String videoKey = removeMyVideoAndGetVideoKey(creator, String.valueOf(v.getCreatedAt().getTime()));
                    log.debug("video element: {}", videoKey);

                    List<String> receives = Arrays.asList(String.format(FEED_KEY, v.getMember().getId()));
                    if (videoKey != null) {
                        for (Following f : followers) {
                            receives.add(String.format(FEED_KEY, f.getMemberMe().getId()));
                        }
                        removeFeedVideos(receives, videoKey);
                    }
                }
        );
    }

    private String removeMyVideoAndGetVideoKey(Long creatorId, String createdAt) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String myVideo = String.format(MEMBER_KEY, creatorId);
            Set<Tuple> videos = jedis.zrevrangeByScoreWithScores(myVideo, createdAt, createdAt);
            if (videos.size() == 1) {
                for (Tuple t : videos) {
                    String element = t.getElement();
                    jedis.zrem(myVideo, element);
                    return element;
                }
            }
            return null;
        } finally {
            if (jedis != null) {
                jedis.disconnect();
                close(jedis);
            }
        }
    }

    private void removeFeedVideos(List<String> receives, String videoKey) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            for (String key : receives) {
                log.debug("key: {}, videoKey: {}", key, videoKey);
                jedis.zrem(key, videoKey);
            }
        } finally {
            if (jedis != null) {
                jedis.disconnect();
                close(jedis);
            }
        }
    }

    private void addMyVideo(String key, Video video) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.zadd(key, video.getCreatedAt().getTime(), video.getVideoKey());
        } finally {
            if (jedis != null) {
                jedis.disconnect();
                close(jedis);
            }
        }
    }

    private void addVideoLinkFeed(List<String> keys, Video video) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            for (String key : keys) {
                jedis.zadd(key, video.getCreatedAt().getTime(), video.getVideoKey());
            }
        } finally {
            if (jedis != null) {
                jedis.disconnect();
                close(jedis);
            }
        }
    }

    private void close(Closeable... closeables) {
        for (Closeable c : closeables) {
            try {
                c.close();
            } catch (IOException e) {
                log.error("Cann't close exception", e);
            }
        }
    }
}
