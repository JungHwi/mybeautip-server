package com.jocoos.mybeautip.restapi;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.feed.FeedService;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.video.Video;

@Slf4j
@RestController
@RequestMapping("/api/1/members/me/feeds")
public class FeedController {

  private final FeedService feedService;
  private final MemberService memberService;

  public FeedController(FeedService feedService,
                        MemberService memberService) {
    this.feedService = feedService;
    this.memberService = memberService;
  }

  @GetMapping
  public CursorResponse getFeeds(@RequestParam(defaultValue = "20") int count,
                                 @RequestParam(required = false) String cursor) {
    Long memberId = memberService.currentMemberId();
    List<Video> videos = feedService.getVideoKeys(memberId, cursor, count);

    List<String> videoKeys = Lists.newArrayList();
    if (videos != null && videos.size() > 0) {
      videos.forEach(v -> {
        videoKeys.add(v.getVideoKey());
      });
    }

    List<VideoController.VideoInfo> result = Lists.newArrayList();
    videos.stream()
       .forEach(v -> result.add(new VideoController.VideoInfo(v, memberService.getMemberInfo(v.getMember()))));

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<>("/api/1/feeds", videos)
       .withCount(count)
       .withCursor(nextCursor).toBuild();
  }
}
