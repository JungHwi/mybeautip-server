package com.jocoos.mybeautip.restapi;

import java.util.List;

import com.jocoos.mybeautip.member.Member;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.feed.FeedService;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoService;

@Slf4j
@RestController
@RequestMapping("/api/1/members/me/feeds")
public class FeedController {

  private final FeedService feedService;
  private final MemberService memberService;
  private final VideoService videoService;

  public FeedController(FeedService feedService,
                        MemberService memberService,
                        VideoService videoService) {
    this.feedService = feedService;
    this.memberService = memberService;
    this.videoService = videoService;
  }

  @GetMapping
  public CursorResponse getFeeds(@RequestParam(defaultValue = "20") int count,
                                 @RequestParam(required = false) String cursor) {
    Member me = memberService.currentMember();
  
    if (me.getFollowingCount() == 0) {
      List<VideoController.VideoInfo> videos = Lists.newArrayList();
      videoService.findVideos(null, null, null, count)
          .stream().forEach(v -> videos.add(videoService.generateVideoInfo(v)));
      return new CursorResponse.Builder<>(null, videos).toBuild();
    }
    
    List<Video> videos = feedService.getVideoKeys(me.getId(), cursor, count);
    
    List<VideoController.VideoInfo> result = Lists.newArrayList();
    videos
       .forEach(v -> {
         if (StringUtils.isBlank(v.getVideoKey())) {
           log.info("feed has invalid videoKey, member_id: " + me.getId());
         } else {
           result.add(videoService.generateVideoInfo(v));
         }
       });

    String nextCursor = null;
    if (result.size() > 0) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<>("/api/1/members/me/feeds", result)
       .withCount(count)
       .withCursor(nextCursor).toBuild();
  }
}
