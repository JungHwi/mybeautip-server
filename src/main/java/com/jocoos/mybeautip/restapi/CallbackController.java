package com.jocoos.mybeautip.restapi;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jocoos.mybeautip.notification.MessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.tag.TagService;
import com.jocoos.mybeautip.video.*;

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
  private final VideoLikeRepository videoLikeRepository;
  private final ObjectMapper objectMapper;

  private static final String MEMBER_NOT_FOUND = "member.not_found";
  
  public CallbackController(VideoService videoService,
                            TagService tagService,
                            MessageService messageService,
                            VideoRepository videoRepository,
                            MemberRepository memberRepository,
                            GoodsRepository goodsRepository,
                            VideoGoodsRepository videoGoodsRepository,
                            VideoLikeRepository videoLikeRepository,
                            ObjectMapper objectMapper) {
    this.videoService = videoService;
    this.tagService = tagService;
    this.messageService = messageService;
    this.videoRepository = videoRepository;
    this.memberRepository = memberRepository;
    this.goodsRepository = goodsRepository;
    this.videoGoodsRepository = videoGoodsRepository;
    this.videoLikeRepository = videoLikeRepository;
    this.objectMapper = objectMapper;
  }
  
  @Transactional
  @PostMapping
  public Video startVideo(@Valid @RequestBody CallbackStartVideoRequest request,
                           @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    log.info("callback createVideo: {}", request.toString());
  
    memberRepository.findByIdAndDeletedAtIsNull(request.getUserId())
        .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));
    
    Video video = videoRepository.findById(request.getVideoKey())
        .orElseThrow(() -> new NotFoundException("video_not_found", "video not found, video_id:" + request.getVideoKey()));
    
    BeanUtils.copyProperties(request, video);
    return videoService.save(video);
  }

  @Transactional
  @PatchMapping
  public Video updateVideo(@Valid @RequestBody CallbackUpdateVideoRequest request) {
    log.info("callback updateVideo: {}", request.toString());
    Video video = videoRepository.findByIdAndDeletedAtIsNull(request.getVideoKey())
      .map(v -> {
        if (v.getMember().getId() != request.getUserId().longValue()) {
          throw new BadRequestException("invalid_user_id", "Invalid user_id: " + request.getUserId());
        }
        return updateVideoProperties(request, v);})
      .orElseThrow(() -> new NotFoundException("video_not_found", "video not found, videoKey: " + request.getVideoKey()));

    return videoService.update(video);
  }

  @Transactional
  @DeleteMapping
  public Video deleteVideo(@Valid @RequestBody CallbackDeleteVideoRequest request) {
    log.info("deleteVideo {}", request.toString());
    return videoService.deleteVideo(request.getUserId(), request.getVideoKey());
  }

  private Video updateVideoProperties(CallbackUpdateVideoRequest source, Video target) {
    // immutable properties: video_id, video_key, type, owner, likecount, commentcount, relatedgoodscount, relatedgoodsurl
    // mutable properties: title, content, url, thumbnail_url, chatroomid, data, state, duration, visibility, banned, watchcount, heartcount, viewcount

    // Can be modified with empty string
    if (source.getContent() != null) {
      List<String> tags = tagService.getHashTagsAndUpdateRefCount(target.getTagInfo(), source.getContent());
      try {
        target.setTagInfo(objectMapper.writeValueAsString(tags));
      } catch (JsonProcessingException e) {
        log.warn("tag parsing failed, tags: ", tags.toString());
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

      if ("PUBLIC".equalsIgnoreCase(prevState) && "PRIVATE".equalsIgnoreCase(newState)) {
        memberRepository.updateVideoCount(target.getMember().getId(), target.getMember().getVideoCount() - 1);
        log.debug("Video state will be changed PUBLIC to PRIVATE: {}", target.getId());
        target.setVisibility(newState);
      }

      if ("PRIVATE".equalsIgnoreCase(prevState) && "PUBLIC".equalsIgnoreCase(newState)) {
        memberRepository.updateVideoCount(target.getMember().getId(), target.getMember().getVideoCount() + 1);
        log.debug("Video state will be changed PRIVATE to PUBLIC: {}", target.getId());
        target.setVisibility(newState);
      }
    }

    return target;
  }

  @Data
  public static class CallbackStartVideoRequest {
    @NotNull
    Long userId;

    @NotNull
    Long videoKey;

    @NotNull
    String state;
    
    String url ="";
    String thumbnailPath = "";
    String thumbnailUrl = "";
    String chatRoomId ="";
  }

  @Data
  public static class CallbackUpdateVideoRequest {
    @NotNull
    Long userId;

    @NotNull
    Long videoKey;

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