package com.jocoos.mybeautip.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.restapi.VideoController;
import com.jocoos.mybeautip.video.VideoRepository;
import com.jocoos.mybeautip.video.VideoService;

@Slf4j
@RestController
@RequestMapping("/api/admin/manual/videos")
public class AdminVideoController {
  private final VideoService videoService;
  private final VideoRepository videoRepository;
  
  public AdminVideoController(VideoService videoService,
                              VideoRepository videoRepository) {
    this.videoService = videoService;
    this.videoRepository = videoRepository;
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
  
  @Data
  private static class UpdateVideoRequest {
    private Boolean locked;
  }
}
