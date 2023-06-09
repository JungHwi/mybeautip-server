package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.feed.FeedService;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.video.LegacyVideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/1/members/me/feeds")
public class FeedController {

    private final FeedService feedService;
    private final LegacyMemberService legacyMemberService;
    private final LegacyVideoService legacyVideoService;

    public FeedController(FeedService feedService,
                          LegacyMemberService legacyMemberService,
                          LegacyVideoService legacyVideoService) {
        this.feedService = feedService;
        this.legacyMemberService = legacyMemberService;
        this.legacyVideoService = legacyVideoService;
    }

//    @GetMapping
//    public CursorResponse getFeeds(@RequestParam(defaultValue = "20") int count,
//                                   @RequestParam(required = false) String cursor) {
//        Member me = legacyMemberService.currentMember();
//
//    if (me.getFollowingCount() == 0) {
//      List<VideoController.VideoInfo> videos = new ArrayList<>();
//      videoService.findVideos(null, null, null, count)
//          .stream().forEach(v -> videos.add(videoService.generateVideoInfo(v)));
//      return new CursorResponse.Builder<>(null, videos).toBuild();
//    }
//
//        List<Video> videos = feedService.getVideoKeys(me.getId(), cursor, count);
//
//        List<LegacyVideoController.VideoInfo> result = new ArrayList<>();
//        videos
//                .forEach(v -> {
//                    if (StringUtils.isBlank(v.getVideoKey())) {
//                        log.info("feed has invalid videoKey, member_id: " + me.getId());
//                    } else {
//                        result.add(legacyVideoService.generateVideoInfo(v));
//                    }
//                });
//
//        String nextCursor = null;
//        if (result.size() > 0) {
//            nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
//        }
//
//        return new CursorResponse.Builder<>("/api/1/members/me/feeds", result)
//                .withCount(count)
//                .withCursor(nextCursor).toBuild();
//    }
}
