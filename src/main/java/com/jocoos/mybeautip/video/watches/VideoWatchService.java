package com.jocoos.mybeautip.video.watches;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


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

  @Value("${mybeautip.video.watch-duration}")
  private long watchDuration;

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
    long duration = now - watchDuration;

    List<VideoWatch> list = videoWatchRepository.findByVideoIdAndModifiedAtAfter(video.getId(), new Date(duration));

    long totalCount = list.size();
    long watchCount = list.stream().filter(v -> !v.getIsGuest()).count();
    long guestCount = list.stream().filter(v -> v.getIsGuest()).count();

    int elapsedTime = (int) (now - video.getCreatedAt().getTime()) / 1000;

    log.info("videoId: {}. elapsedTime: {}, total: {}, watch: {}, guest: {}",
        video.getId(), elapsedTime, totalCount, watchCount, guestCount);

    try {
      String countList = objectMapper.writeValueAsString(Arrays.asList(totalCount, watchCount, guestCount));
      saveVideoWatchCount(String.format(VIDEO_STATS_KEY, video.getId()), elapsedTime, countList);
    } catch (JsonProcessingException e) {
      log.error("Failed to parse the watch count list to json", e);
    }
  }

  private void saveVideoWatchCount(String key, int elapsedTime, String counts) {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();
      jedis.zadd(key, elapsedTime, String.format("%s:%s", counts, elapsedTime));
    } finally {
      if (jedis != null) {
        jedis.disconnect();
        close(jedis);
      }
    }
  }

  public Map<Integer, Integer[]> getVideoWatchStats(Long videoId) {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();
      Set<Tuple> tuples = jedis.zrangeByScoreWithScores(String.format(VIDEO_STATS_KEY, videoId), "-inf", "+inf");

      Map<Integer, Integer[]> stats = new TreeMap<>();
      for (Tuple t: tuples) {
        log.debug("{}: {}", t.getScore(), t.getElement());
        try {
          String[] split = t.getElement().split(":");
          Integer[] counts = objectMapper.readValue(split[0], Integer[].class);
          stats.put((int) t.getScore(), counts);
        } catch (IOException e) {
          log.error("Failed to read the watch count list to object", e);
        }
      }

      return stats;
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
