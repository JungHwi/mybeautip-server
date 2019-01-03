package com.jocoos.mybeautip.restapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.tag.TagService;
import com.jocoos.mybeautip.video.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/callbacks/video", produces = MediaType.APPLICATION_JSON_VALUE)
public class CallbackController {
  private final VideoService videoService;
  private final TagService tagService;
  private final MessageService messageService;
  private final MemberRepository memberRepository;
  private final VideoRepository videoRepository;
  private final GoodsRepository goodsRepository;
  private final VideoGoodsRepository videoGoodsRepository;
  private final ObjectMapper objectMapper;
  
  private static final String MEMBER_NOT_FOUND = "member.not_found";
  
  public CallbackController(VideoService videoService,
                            TagService tagService,
                            MessageService messageService,
                            VideoRepository videoRepository,
                            MemberRepository memberRepository,
                            GoodsRepository goodsRepository,
                            VideoGoodsRepository videoGoodsRepository,
                            ObjectMapper objectMapper) {
    this.videoService = videoService;
    this.tagService = tagService;
    this.messageService = messageService;
    this.videoRepository = videoRepository;
    this.memberRepository = memberRepository;
    this.goodsRepository = goodsRepository;
    this.videoGoodsRepository = videoGoodsRepository;
    this.objectMapper = objectMapper;
  }
  
  @Transactional
  @PostMapping
  public Video startVideo(@Valid @RequestBody CallbackStartVideoRequest request,
                           @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    log.info("callback startVideo: {}", request.toString());
  
    Member member = memberRepository.findByIdAndDeletedAtIsNull(request.getUserId())
        .orElseGet(() -> {
          log.error("Invalid UserID: " + request.getUserId());
          throw new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang));
        });
    
    // Ignore when videoKey is already exist
    if (videoRepository.findByVideoKey(request.getVideoKey()).isPresent()) {
      log.debug("VideoKey is already exist, videoKey: " + request.getVideoKey());
      return null;
    }
    
    return videoService.startVideo(request, member);
  }
  
  @Transactional
  @PatchMapping
  public Video updateVideo(@Valid @RequestBody CallbackUpdateVideoRequest request) {
    log.info("callback updateVideo: {}", request.toString());
    Video video = videoRepository.findByVideoKeyAndDeletedAtIsNull(request.getVideoKey())
        .map(v -> {
          if (v.getMember().getId() != request.getUserId().longValue()) {
            log.error("Invalid UserID: " + request.getUserId());
            throw new BadRequestException("invalid_user_id", "Invalid user_id: " + request.getUserId());
          }
          return updateVideoProperties(request, v);})
        .orElseGet(() -> {
          log.error("Cannot find video " + request.getVideoKey());
          throw new NotFoundException("video_not_found", "video not found, videoKey: " + request.getVideoKey());
        });
    
    return videoService.update(video);
  }
  
  @Transactional
  @DeleteMapping
  public Video deleteVideo(@Valid @RequestBody CallbackDeleteVideoRequest request) {
    log.info("deleteVideo {}", request.toString());
    return videoService.deleteVideo(request.getUserId(), request.getVideoKey());
  }
  
  @Transactional
  private Video updateVideoProperties(CallbackUpdateVideoRequest source, Video target) {
    // immutable properties: video_id, video_key, type, owner, likecount, commentcount, relatedgoodscount, relatedgoodsurl
    // mutable properties: title, content, url, thumbnail_url, chatroomid, data, state, duration, visibility, banned, watchcount, heartcount, viewcount
    
    // Can be modified with empty string
    if (source.getContent() != null) {
      List<String> tags = tagService.getHashTagsAndUpdateRefCount(target.getTagInfo(), source.getContent());
      if (tags != null && tags.size() > 0) {
        try {
          target.setTagInfo(objectMapper.writeValueAsString(tags));
        } catch (JsonProcessingException e) {
          log.warn("tag parsing failed, tags: ", tags.toString());
        }
  
        // Log TagHistory
        tagService.logHistory(tags, TagService.TagCategory.VIDEO, target.getMember());
      }
      target.setContent(source.getContent());
    }
    
    if (source.getData() != null) {
      target.setData(source.getData());
    }
    
    if (source.getDuration() != null) {
      target.setDuration(source.getDuration());
    }
    
    if (source.getChatRoomId() != null) {
      target.setChatRoomId(source.getChatRoomId());
    }
    
    // Cannot be modified with empty string
    if (source.getTitle() != null) {
      if (StringUtils.strip(source.getTitle()).length() > 0) {
        target.setTitle(source.getTitle());
      }
    }
    
    if (source.getUrl() != null) {
      if (StringUtils.strip(source.getUrl()).length() > 0) {
        target.setUrl(source.getUrl());
      }
    }
    
    if (source.getThumbnailPath() != null) {
      if (StringUtils.strip(source.getThumbnailPath()).length() > 0) {
        target.setThumbnailPath(source.getThumbnailPath());
      }
    }
    
    if (source.getThumbnailUrl() != null) {
      if (StringUtils.strip(source.getThumbnailUrl()).length() > 0) {
        target.setThumbnailUrl(source.getThumbnailUrl());
      }
    }
    
    if (source.getState() != null) {
      if (StringUtils.containsAny(source.getState(), "LIVE", "VOD")) {
        target.setState(source.getState());
      }
    }
    
    if (source.getVisibility() != null) {
      String prevState = target.getVisibility();
      String newState = source.getVisibility();
      
      Member member = target.getMember();
      if ("PUBLIC".equalsIgnoreCase(prevState) && "PRIVATE".equalsIgnoreCase(newState)) {
        member.setVideoCount(member.getVideoCount() - 1);
        log.debug("Video state will be changed PUBLIC to PRIVATE: {}", target.getId());
        target.setVisibility(newState);
      }
      
      if ("PRIVATE".equalsIgnoreCase(prevState) && "PUBLIC".equalsIgnoreCase(newState)) {
        member.setVideoCount(member.getVideoCount() + 1);
        log.debug("Video state will be changed PRIVATE to PUBLIC: {}", target.getId());
        target.setVisibility(newState);
      }
      memberRepository.save(member);
    }
    
    return target;
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
    Integer duration = 0;
    String title ="";
    String content = "";
    String url ="";
    String thumbnailPath = "";
    String thumbnailUrl = "";
    String chatRoomId ="";
    String data = "";
    
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
    String url;
    String thumbnailPath;
    String thumbnailUrl;
    String chatRoomId;
    Integer duration;
    String data;
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