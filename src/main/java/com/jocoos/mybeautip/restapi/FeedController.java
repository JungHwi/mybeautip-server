package com.jocoos.mybeautip.restapi;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import redis.clients.jedis.*;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.video.Video;

@Slf4j
@RestController
@RequestMapping("/api/1/feeds")
public class FeedController {

  private static final String FEED_KEY = "feed:%s";
  private final JedisPool jedisPool;
  private final ObjectMapper objectMapper;
  private final MemberService memberService;
  private final MemberRepository memberRepository;

  public FeedController(JedisPool jedisPool,
                        ObjectMapper objectMapper,
                        MemberService memberService,
                        MemberRepository memberRepository) {
    this.jedisPool = jedisPool;
    this.objectMapper = objectMapper;
    this.memberService = memberService;
    this.memberRepository = memberRepository;
  }

  @GetMapping
  public CursorResponse getFeeds(@RequestParam(defaultValue = "20") int count,
                                 @RequestParam(required = false) String cursor) {
    Long memberId = memberService.currentMemberId();
    String key = String.format(FEED_KEY, memberId);
    List<Video> videos = getVideos(key, cursor,count);
    List<VideoController.VideoInfo> result = Lists.newArrayList();

    videos.stream().forEach(v -> {
      result.add(new VideoController.VideoInfo(v, memberService.getMemberInfo(v.getMember())));
    });

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<>("/api/1/feeds", videos)
       .withCount(count)
       .withCursor(nextCursor).toBuild();
  }

  private List<Video> getVideos(String key, String cursor, int count) {
    List<Video> videos = Lists.newArrayList();
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();

      Set<Tuple> tuples = jedis.zrevrangeByScoreWithScores(key, cursor, "-inf", 0, count);
      for (Tuple t: tuples) {
        log.debug("tuple: {}", t);
        Video video = getVideo(t.getElement());
        videos.add(video);
      }
    } finally {
      close(jedis);
    }

    return videos;
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
