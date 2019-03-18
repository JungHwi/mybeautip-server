package com.jocoos.mybeautip.admin;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.recoding.ViewRecoding;
import com.jocoos.mybeautip.recoding.ViewRecodingRepository;
import com.jocoos.mybeautip.restapi.VideoController;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoRepository;
import com.jocoos.mybeautip.video.VideoService;

@Slf4j
@RestController
@RequestMapping("/api/admin/manual/videos")
public class AdminVideoController {
  private final VideoService videoService;
  private final MemberService memberService;
  private final VideoRepository videoRepository;
  private final ViewRecodingRepository viewRecodingRepository;
  
  public AdminVideoController(VideoService videoService,
                              MemberService memberService,
                              VideoRepository videoRepository,
                              ViewRecodingRepository viewRecodingRepository) {
    this.videoService = videoService;
    this.memberService = memberService;
    this.videoRepository = videoRepository;
    this.viewRecodingRepository = viewRecodingRepository;
  }
  
  
  @PatchMapping("/{id:.+}")
  public ResponseEntity<VideoController.VideoInfo> updateVideo(@PathVariable Long id,
                                                               @RequestBody UpdateVideoRequest request) {
    log.debug("request: {}", request);
    
    VideoController.VideoInfo videoInfo = videoRepository.findByIdAndDeletedAtIsNull(id)
        .map(video -> {
          if (request.getLocked() != null && request.getLocked()) {
            if (video.getLocked()) {
              throw new BadRequestException("already_locked", "Video already locked");
            }
            video = videoService.lockVideo(video);
          }
          if (request.getLocked() != null && !request.getLocked()) {
            if (!video.getLocked()) {
              throw new BadRequestException("already_unlocked", "Video does not lock.");
            }
            video = videoService.unLockVideo(video);
          }
          return videoService.generateVideoInfo(video);
        })
        .orElseThrow(() -> new NotFoundException("video_not_found", "Video not found: " + id));
    
    return new ResponseEntity<>(videoInfo, HttpStatus.OK);
  }


  @GetMapping("/recently")
  public ResponseEntity<List<RecentVideoInfo>> getRecentVideos(
     @RequestParam(defaultValue = "10") int days,
     @RequestParam String type,
     @RequestParam List<String> states) {

    LocalDate localDate = LocalDate.now();
    PageRequest pageRequest = PageRequest.of(0, 100, Sort.Direction.DESC, "id");
    List<RecentVideoInfo> recentVideos = Lists.newArrayList();
    LocalDateTime today = localDate.atStartOfDay();

    for (int i = 0; i < days; i++) {
      LocalDateTime startOfDay = null;
      if (i > 0) {
        startOfDay = today.minusDays(i);
      }  else {
        startOfDay = today;
      }

      LocalDateTime endOfDay = startOfDay.plus(Duration.ofSeconds(86399));
      Date startDate = Date.from(startOfDay.toInstant(ZoneOffset.UTC));

      Page<Video> betweens = videoRepository.findByTypeAndStateInAndAndCreatedAtBetweenAndDeletedAtIsNull(
         type, states,
         startDate, Date.from(endOfDay.toInstant(ZoneOffset.UTC)),
         pageRequest
      );

      List<VideoController.VideoInfo> videos =
         betweens.stream().map(v -> videoService.generateVideoInfo(v)).collect(Collectors.toList());

      recentVideos.add(new RecentVideoInfo(startDate, videos));
    }

    return new ResponseEntity<>(recentVideos, HttpStatus.OK);
  }
  
  
  /**
   * This API is not for retrieve realtime watcher list
   * @param video_id
   * @return watcher list who have watched a video when on streaming
   */
  @GetMapping("/{id:.+}/on-live-watchers")
  public ResponseEntity<Page<MemberInfo>> getOnLiveWatcherList(@PathVariable Long id,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "100") int size) {
    Video video = videoRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("video_not_found", "Video not found: " + id));
    
    if (!"BROADCASTED".equals(video.getType())) {
      throw new BadRequestException("invalid_video_type", "Valid video type is BROADCASTED");
    }
    
    Date endedAt = video.getEndedAt();
    if (endedAt == null) {
      endedAt = new Date(video.getCreatedAt().getTime() + video.getDuration());
    }
  
    Pageable pageable = PageRequest.of(page, size, new Sort(Sort.Direction.ASC, "createdAt"));
    Page<ViewRecoding> viewRecodings = viewRecodingRepository.findByItemIdAndCategoryAndCreatedAtLessThanEqual(
        video.getId().toString(), ViewRecoding.CATEGORY_VIDEO, endedAt, pageable);
    Page<MemberInfo> watchers = viewRecodings.map(m -> memberService.getMemberInfo(m.getCreatedBy()));
    
    return new ResponseEntity<>(watchers, HttpStatus.OK);
  }


  @Data
  private static class UpdateVideoRequest {
    private Boolean locked;
  }

  @Data
  private static class RecentVideoInfo {
    private Date date;
    private int videoCount;
    private List<VideoController.VideoInfo> videos;

    public RecentVideoInfo(Date date, List<VideoController.VideoInfo> videos) {
      this.date = date;
      this.videos = videos;

      if (!CollectionUtils.isEmpty(videos)) {
        this.videoCount = videos.size();
      }
    }
  }
}
