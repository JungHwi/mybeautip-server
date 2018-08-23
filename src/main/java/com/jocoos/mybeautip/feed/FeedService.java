package com.jocoos.mybeautip.feed;

import java.io.Closeable;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoRepository;

@Slf4j
@Service
public class FeedService {

  private static final String MEMBER_KEY = "member:%s";
  private static final String FEED_KEY = "feed:%s";

  private final ObjectMapper objectMapper;
  private final JedisPool jedisPool;
  private final FollowingRepository followingRepository;
  private final VideoRepository videoRepository;

  public FeedService(ObjectMapper objectMapper,
                     JedisPool jedisPool,
                     FollowingRepository followingRepository,
                     VideoRepository videoRepository) {

    this.objectMapper = objectMapper;
    this.jedisPool = jedisPool;
    this.followingRepository = followingRepository;
    this.videoRepository = videoRepository;
  }

  public List<Video> getVideos(Long memberId, String cursor, int count) {
    String key = String.format(FEED_KEY, memberId);
    List<Video> videos = Lists.newArrayList();
    Jedis jedis = null;
    log.debug("memberId: {}, cursor: {}, count: {}", memberId, cursor, count);

    try {
      jedis = jedisPool.getResource();
      Set<Tuple> tuples = null;
      if (Strings.isNullOrEmpty(cursor)) {
        tuples = jedis.zrevrangeByScoreWithScores(key, "+inf", "-inf", 0, count);
      } else {
        tuples = jedis.zrevrangeByScoreWithScores(key, "(" + cursor, "-inf", 0, count);
      }
      for (Tuple t: tuples) {
        Video video = getVideo(t.getElement());
        videos.add(video);
      }
    } finally {
      if (jedis != null) {
        jedis.disconnect();
        close(jedis);
      }
    }

    return videos;
  }

  public void feedVideo(Video video) {
    Long creator = video.getMember().getId();
    List<Following> followers = followingRepository.findByCreatedAtBeforeAndMemberYouId(new Date(), creator);
    log.debug("followers: {}", followers);

    addMyVideo(String.format(MEMBER_KEY, creator), video);

    List<String> receives = Lists.newArrayList(String.format(FEED_KEY, creator));
    for (Following f: followers) {
      receives.add(String.format(FEED_KEY, f.getMemberMe().getId()));
    }

    addVideoLinkFeed(receives, video);
  }

  public void followMember(Long me, Long follower) {
    String followerKey = String.format(MEMBER_KEY, follower);
    String myFeedKey = String.format(FEED_KEY, me);
    copyFollowerVideoToMe(followerKey, myFeedKey);
  }

  private void copyFollowerVideoToMe(String followerKey, String myFeedKey) {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();
      Set<Tuple> tuples = jedis.zrevrangeByScoreWithScores(followerKey, "+inf", "-inf");
      for (Tuple t: tuples) {
        Video video = readValue(t.getElement());
        log.debug("video key: {}", video.getVideoKey());
        jedis.zadd(myFeedKey, t.getScore(), video.getVideoKey());
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
      for (Tuple t: tuples) {
        Video video = readValue(t.getElement());
        log.debug("video key: {}", video.getVideoKey());
        jedis.zrem(myFeedKey, video.getVideoKey());
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
        log.debug("followers: {}", followers);

        String videoKey = removeMyVideoAndGetVideoKey(creator, String.valueOf(v.getCreatedAt().getTime()));
        log.debug("video element: {}", videoKey);

        List<String> receives = Lists.newArrayList(String.format(FEED_KEY, v.getMember().getId()));
        if (videoKey != null) {
          for (Following f: followers) {
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
      String myFeed = String.format(FEED_KEY, creatorId);
      Set<Tuple> videos = jedis.zrevrangeByScoreWithScores(myVideo, createdAt, createdAt);
      if (videos.size() == 1) {
        for (Tuple t: videos) {
          jedis.zrem(myVideo, t.getElement());
        }
      }

      Set<Tuple> feeds = jedis.zrevrangeByScoreWithScores(myFeed, createdAt, createdAt);
      log.debug("removed: {}", feeds.size());

      if (feeds.size() == 1) {
        for (Tuple t: feeds) {
          String element = t.getElement();
          jedis.zrem(myFeed, element);
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
      jedis.zadd(key, video.getCreatedAt().getTime(), writeAsString(video));
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
      for(String key: keys) {
        log.debug("key: {}, videoKey: {}", key, video.getVideoKey() );
        jedis.zadd(key, video.getCreatedAt().getTime(), video.getVideoKey());
      }
    } finally {
      if (jedis != null) {
        jedis.disconnect();
        close(jedis);
      }
    }
  }

  private String writeAsString(Video video) {
    try {
      return objectMapper.writeValueAsString(video);
    } catch (IOException e) {
      log.error("failed to write as string", e);
    }
    return null;
  }

  private Video readValue(String video) {
    try {
      return objectMapper.readValue(video, Video.class);
    } catch (IOException e) {
      log.error("failed to write as string", e);
    }
    return null;
  }

  private Video getVideo(String element) {
    log.debug("element: {}", element);

    try {
      return objectMapper.readValue(element, Video.class);
    } catch (IOException e) {
      log.error("cann't read element to video", e);
    }
    return null;
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
