package com.jocoos.mybeautip.restapi;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.video.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/callbacks/video", produces = MediaType.APPLICATION_JSON_VALUE)
public class CallbackController {
  private final VideoService videoService;
  private final MemberRepository memberRepository;
  private final VideoRepository videoRepository;
  private final GoodsRepository goodsRepository;
  private final VideoGoodsRepository videoGoodsRepository;
  private final VideoLikeRepository videoLikeRepository;

  public CallbackController(VideoService videoService,
                            VideoRepository videoRepository,
                            MemberRepository memberRepository,
                            GoodsRepository goodsRepository,
                            VideoGoodsRepository videoGoodsRepository,
                            VideoLikeRepository videoLikeRepository) {
    this.videoService = videoService;
    this.videoRepository = videoRepository;
    this.memberRepository = memberRepository;
    this.goodsRepository = goodsRepository;
    this.videoGoodsRepository = videoGoodsRepository;
    this.videoLikeRepository = videoLikeRepository;
  }

  @Transactional
  @PostMapping
  public Video createVideo(@Valid @RequestBody CallbackCreateVideoRequest request) {
    log.info("callback createVideo: {}", request.toString());
    videoRepository.findByVideoKey(request.getVideoKey())
      .ifPresent(v -> { throw new BadRequestException("already_exist", "Already exist, videoKey: " + request.getVideoKey()); });

    Video video = new Video();
    BeanUtils.copyProperties(request, video);
    video.setVisibility("PUBLIC");
    video.setCommentCount(0);
    video.setLikeCount(0);
    video.setViewCount(0);
    video.setHeartCount(0);
    video.setWatchCount(0);
    video.setTotalWatchCount(0);
    video.setOrderCount(0);

    memberRepository.findByIdAndDeletedAtIsNull((request.getUserId()))
      .map(m -> {
        video.setMember(m);
        return Optional.empty();
      })
      .orElseThrow(() -> new MemberNotFoundException(request.getUserId()));

    Video createdVideo = videoService.save(video);

    // Set related goods info
    if (StringUtils.isNotEmpty(request.getData())) {
      String[] userData = StringUtils.deleteWhitespace(request.getData()).split(",");
      List<VideoGoods> videoGoods = new ArrayList<>();
      for (String goods : userData) {
        if (goods.length() != 10) { // invalid goodsNo
          continue;
        }
        goodsRepository.findByGoodsNo(goods).map(g -> {
          videoGoods.add(new VideoGoods(createdVideo, g, createdVideo.getMember()));
          return Optional.empty();
        });
      }

      if (videoGoods.size() > 0) {
        videoGoodsRepository.saveAll(videoGoods);

        // Set related goods count & one thumbnail image
        String url = videoGoods.get(0).getGoods().getListImageData().toString();
        createdVideo.setRelatedGoodsThumbnailUrl(url);
        createdVideo.setRelatedGoodsCount(videoGoods.size());
        videoService.save(createdVideo);
      }
    }

    memberRepository.updateVideoCount(video.getMember().getId(), 1);
    memberRepository.updateTotalVideoCount(video.getMember().getId(), 1);
    return createdVideo;
  }

  @Transactional
  @PatchMapping
  public Video updateVideo(@Valid @RequestBody CallbackUpdateVideoRequest request) {
    log.info("callback updateVideo: {}", request.toString());
    Video video = videoRepository.findByVideoKeyAndDeletedAtIsNull(request.getVideoKey())
      .map(v -> {
        if (v.getMember().getId() != request.getUserId().longValue()) {
          throw new BadRequestException("invalid_user_id", "Invalid user_id: " + request.getUserId());
        }
        return updateVideoProperties(request, v);})
      .orElseThrow(() -> new NotFoundException("not_found_video", "video not found, videoKey: " + request.getVideoKey()));

    return videoService.update(video);
  }

  @Transactional
  @DeleteMapping
  public Video deleteVideo(@Valid @RequestBody CallbackDeleteVideoRequest request) {
    log.info("deleteVideo {}", request.toString());
    return videoRepository.findByVideoKeyAndDeletedAtIsNull(request.getVideoKey())
      .map(v -> {
        if (v.getMember().getId() != request.getUserId().longValue()) {
          throw new BadRequestException("invalid_user_id", "Invalid user_id: " + request.getUserId());
        }
        v.setDeletedAt(new Date());
        videoService.saveWithDeletedAt(v);
        videoLikeRepository.deleteByVideoId(v.getId());
        memberRepository.updateVideoCount(v.getMember().getId(), -1);
        memberRepository.updateTotalVideoCount(v.getMember().getId(), -1);
        return v;
      })
      .orElseThrow(() -> new NotFoundException("not_found_video", "video not found, videoKey: " + request.getVideoKey()));
  }

  private Video updateVideoProperties(CallbackUpdateVideoRequest source, Video target) {
    // immutable properties: video_id, video_key, type, owner, likecount, commentcount, relatedgoodscount, relatedgoodsurl
    // mutable properties: title, content, url, thumbnail_url, chatroomid, data, state, duration, visibility, banned, watchcount, heartcount, viewcount

    // Can be modified with empty string
    if (source.getContent() != null) {
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
      if (StringUtils.containsAny(source.getVisibility(), "PUBLIC", "PRIVATE")) {
        if ("PUBLIC".equalsIgnoreCase(target.getVisibility()) && "PRIVATE".equalsIgnoreCase(source.getVisibility())) {
          memberRepository.updateVideoCount(target.getMember().getId(), -1);
        }

        if ("PRIVATE".equalsIgnoreCase(target.getVisibility()) && "PUBLIC".equalsIgnoreCase(source.getVisibility())) {
          memberRepository.updateVideoCount(target.getMember().getId(), 1);
        }
        target.setVisibility(source.getVisibility());
      }
    }

    return target;
  }

  @Data
  public static class CallbackCreateVideoRequest {
    @NotNull
    Long userId;

    @NotNull
    String videoKey;

    @NotNull
    String type;

    String state;
    Integer duration = 0;
    String title ="";
    String content = "";
    String url ="";
    String thumbnailPath = "";
    String thumbnailUrl = "";
    String chatRoomId ="";
    String data = "";
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