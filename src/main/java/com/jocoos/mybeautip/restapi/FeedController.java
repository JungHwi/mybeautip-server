package com.jocoos.mybeautip.restapi;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.feed.FeedService;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoService;

@Slf4j
@RestController
@RequestMapping("/api/1/feeds")
public class FeedController {

  private final FeedService feedService;
  private final MemberService memberService;
  private final VideoService videoService;
  private final MemberRepository memberRepository;

  public FeedController(FeedService feedService,
                        MemberService memberService,
                        VideoService videoService,
                        MemberRepository memberRepository) {
    this.feedService = feedService;
    this.memberService = memberService;
    this.videoService = videoService;
    this.memberRepository = memberRepository;
  }

  @GetMapping
  public CursorResponse getFeeds(@RequestParam(defaultValue = "20") int count,
                                 @RequestParam(required = false) String cursor) {
    Long memberId = memberService.currentMemberId();
    List<Video> videos = feedService.getVideos(memberId, cursor, count);
    List<VideoController.VideoInfo> result = Lists.newArrayList();

    videos.stream()
       .forEach(v -> result.add(videoService.generateVideoInfo(v)));

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<>("/api/1/feeds", videos)
       .withCount(count)
       .withCursor(nextCursor).toBuild();
  }
}
