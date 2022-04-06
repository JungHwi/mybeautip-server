package com.jocoos.mybeautip.restapi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Locale;

import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.apache.commons.lang3.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.video.*;
import com.jocoos.mybeautip.video.watches.VideoWatchService;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/callbacks/video", produces = MediaType.APPLICATION_JSON_VALUE)
public class CallbackController {
  private static final String VIDEO_LOCKED = "video.locked";

  private final VideoService videoService;
  private final MessageService messageService;
  private final MemberRepository memberRepository;
  private final VideoRepository videoRepository;
  private final VideoWatchService videoWatchService;
  private final VideoDataService videoDataService;
  
  private static final String MEMBER_NOT_FOUND = "member.not_found";
  private static final String LIVE_NOT_ALLOWED = "video.live_not_allowed";
  private static final String MOTD_UPLOAD_NOT_ALLOWED = "video.motd_upload_not_allowed";
  
  public CallbackController(VideoService videoService,
                            MessageService messageService,
                            VideoRepository videoRepository,
                            MemberRepository memberRepository,
                            VideoWatchService videoWatchService,
                            VideoDataService videoDataService) {
    this.videoService = videoService;
    this.messageService = messageService;
    this.videoRepository = videoRepository;
    this.memberRepository = memberRepository;
    this.videoWatchService = videoWatchService;
    this.videoDataService = videoDataService;
  }
  
  @PostMapping
  public Video startVideo(@Valid @RequestBody CallbackStartVideoRequest request,
                           BindingResult bindingResult,
                           @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    if (bindingResult.hasErrors()) {
      log.info("{}", bindingResult.getTarget());
    }
    log.info("callback startVideo: {}", request.toString());
  
    Member member = memberRepository.findByIdAndDeletedAtIsNull(request.getUserId())
        .orElseGet(() -> {
          log.error("Invalid UserID: " + request.getUserId());
          throw new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang));
        });

    return videoService.startVideo(request, member);
  }

  @PatchMapping
  public Video updateVideo(@Valid @RequestBody CallbackUpdateVideoRequest request) {
    log.info("callback updateVideo: {}", request.toString());
    Video video = videoRepository.findByVideoKeyAndDeletedAtIsNull(request.getVideoKey())
        .orElseGet(() -> {
          log.error("Cannot find video " + request.getVideoKey());
          throw new NotFoundException("video_not_found", "video not found, videoKey: " + request.getVideoKey());
        });
    
    if (video.getMember().getId() != request.getUserId().longValue()) {
      log.error("Invalid UserID: " + request.getUserId());
      throw new BadRequestException("invalid_user_id", "Invalid user_id: " + request.getUserId());
    }

    if (video.getLocked() && "VOD".equals(video.getState()) && "PUBLIC".equals(request.getVisibility())) {
      throw new BadRequestException("video_locked", messageService.getMessage(VIDEO_LOCKED, Locale.KOREAN));
    }

    VideoExtraData extraData = null;
    if (!StringUtils.isBlank(request.getData())) {
      extraData = videoDataService.getDataObject(request.getData());
      log.info("{}", extraData);
    }

    String oldState = video.getState();

    video = videoService.updateVideoProperties(request, video, extraData);
    video = videoService.update(video);

    if (extraData != null && !StringUtils.isBlank(extraData.getGoods())) {
      log.info("goods {}, request goods: {}", video.getData(), extraData.getGoods());
      videoService.updateVideoGoods(video, extraData.getGoods());
    } else {
      videoService.clearVideoGoods(video);
    }

    // Send on-live stats using slack when LIVE ended
    if ("BROADCASTED".equals(video.getType()) && "LIVE".equals(oldState) && "VOD".equals(request.getState())) {
      videoService.sendStats(video);
    }

    // Send collect watch counts on LIVE
    if ("BROADCASTED".equals(video.getType()) && "LIVE".equals(request.getState())) {
      videoWatchService.collectVideoWatchCount(video);
    }

    return video;
  }
  
  @DeleteMapping
  public Video deleteVideo(@Valid @RequestBody CallbackDeleteVideoRequest request) {
    log.info("deleteVideo {}", request.toString());
    return videoService.deleteVideo(request.getUserId(), request.getVideoKey());
  }
  
  @Data
  public static class CallbackStartVideoRequest {
    @NotNull
    Long userId;
  
    @NotNull
    String videoKey;
  
    @NotNull
    String type;
  
    String visibility;
    String state;
    Boolean muted = false;
    Boolean locked = false;
    Integer duration = 0;
    String liveKey;
    String outputType;
    String title ="";
    String content = "";
    String originalFilename;
    String url ="";
    String thumbnailPath = "";
    String thumbnailUrl = "";
    String chatRoomId ="";
    String data = "";
    String data2 = "";
    
    @NotNull
    Date createdAt;
  }
  
  @Data
  public static class CallbackUpdateVideoRequest {
    @NotNull
    Long userId;
    
    @NotNull
    String videoKey;
    
    String visibility;
    String state;
    String title;
    String content;
    String originalFilename;
    String url;
    String thumbnailPath;
    String thumbnailUrl;
    String chatRoomId;
    Integer duration;
    String liveKey;
    String outputType;
    String data;
    String data2;
    Integer watchCount;
    Integer heartCount;
    Integer viewCount;
  }
  
  @Data
  public static class CallbackDeleteVideoRequest {
    @NotNull
    Long userId;
    
    @NotNull
    String videoKey;
  }
}