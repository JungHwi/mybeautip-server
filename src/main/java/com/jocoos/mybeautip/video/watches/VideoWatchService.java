package com.jocoos.mybeautip.video.watches;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import com.jocoos.mybeautip.video.Video;

@Slf4j
@Service
public class VideoWatchService {

  private static final String VIDEO_STATS_KEY = "video:%s";

  private final JedisPool jedisPool;
  private final ObjectMapper objectMapper;
  private final VideoWatchRepository videoWatchRepository;

  // update time duration from lambda, ms
  private long collectDuration = 3000;

  public VideoWatchService(JedisPool jedisPool,
                           ObjectMapper objectMapper,
                           VideoWatchRepository videoWatchRepository) {
    this.jedisPool = jedisPool;
    this.videoWatchRepository = videoWatchRepository;
    this.objectMapper = objectMapper;
  }

  @Async
  public void collectVideoWatchCount(Video video) {
    long now = new Date().getTime();
    long duration = now - collectDuration;

    List<VideoWatch> list = videoWatchRepository.findByVideoIdAndModifiedAtAfter(video.getId(), new Date(duration));

    long totalCount = list.size();
    long watchCount = list.stream().filter(v -> !v.getIsGuest()).count();
    long guestCount = list.stream().filter(v -> v.getIsGuest()).count();

    int elapsedTime = (int) (now - video.getCreatedAt().getTime()) / 1000;

    log.info("videoId: {}. elapsedTime: {}, total: {}, watch: {}, guest: {}",
        video.getId(), elapsedTime, totalCount, watchCount, guestCount);

    try {
      String countList = objectMapper.writeValueAsString(Lists.newArrayList(totalCount, watchCount, guestCount));
      saveVideoWatchCount(String.format(VIDEO_STATS_KEY, video.getId()), elapsedTime, countList);
    } catch (JsonProcessingException e) {
      log.error("Failed to parse the watch count list to json", e);
    }
  }

  private void saveVideoWatchCount(String key, int elapsedTime, String counts) {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();
      jedis.zadd(key, elapsedTime, counts);
    } finally {
      if (jedis != null) {
        jedis.disconnect();
        close(jedis);
      }
    }
  }

  public VideoWatchStatsInfo getVideoWatchStats(Long videoId) {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();
      Set<Tuple> tuples = jedis.zrevrangeByScoreWithScores(String.format(VIDEO_STATS_KEY, videoId), "+inf", "-inf");

      VideoWatchStatsInfo statsInfo = new VideoWatchStatsInfo();

      for (Tuple t: tuples) {
        try {
          Integer[] counts = objectMapper.readValue(t.getElement(), Integer[].class);
          statsInfo.add((int) t.getScore(), counts);
        } catch (IOException e) {
          log.error("Failed to read the watch count list to object", e);
        }
      }

      return statsInfo;
    } finally {
      if (jedis != null) {
        jedis.disconnect();
        close(jedis);
      }
    }
  }

  @Data
  public static class VideoWatchStatsInfo {
    private List<VideoWatchGraph> total;
    private List<VideoWatchGraph> watcher;
    private List<VideoWatchGraph> guest;

    public VideoWatchStatsInfo() {
      this.total = new ArrayList<>();
      this.watcher = new ArrayList<>();
      this.guest = new ArrayList<>();
    }

    public void add(int key, Integer[] counts) {
      this.total.add(new VideoWatchGraph(key, counts[0]));
      this.watcher.add(new VideoWatchGraph(key, counts[1]));
      this.guest.add(new VideoWatchGraph(key, counts[2]));
    }
  }
  
  @Data
  public static class VideoWatchGraph {
    int x;
    int y;

    public VideoWatchGraph(int x, int y) {
      this.x = x;
      this.y = y;
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
