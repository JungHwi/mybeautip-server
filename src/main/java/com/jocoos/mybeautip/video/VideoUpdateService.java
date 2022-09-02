package com.jocoos.mybeautip.video;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.restapi.CallbackController;
import com.jocoos.mybeautip.support.DateUtils;
import com.jocoos.mybeautip.tag.TagService;
import com.jocoos.mybeautip.video.watches.VideoWatchService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoUpdateService {

  private final VideoService videoService;
  private final VideoDataService videoDataService;
  private final VideoWatchService videoWatchService;

  private final VideoRepository videoRepository;
  private final MemberRepository memberRepository;
  private final TagService tagService;
  private final GoodsRepository goodsRepository;
  private final VideoGoodsRepository videoGoodsRepository;

  @Transactional
  public Video created(CallbackController.CallbackStartVideoRequest request) {
    Member member = findMember(Long.parseLong(request.getUserId()));
    Video video;

    if ("UPLOADED".equals(request.getType())) {
      video = new Video(member);
      BeanUtils.copyProperties(request, video);
      Video createdVideo = videoRepository.save(video);

      if (StringUtils.isNotEmpty(createdVideo.getContent())) {
        tagService.increaseRefCount(createdVideo.getContent());
        tagService.addHistory(createdVideo.getContent(), TagService.TAG_VIDEO, createdVideo.getId(), createdVideo.getMember());
      }

      // Set related goods info
      log.info("data: {}", request.getData());
      if (StringUtils.isNotEmpty(request.getData())) {
        VideoExtraData extraData = videoDataService.getDataObject(request.getData());
        log.info("{}", extraData);
        List<VideoCategory> categories = new ArrayList<>();
        if (!StringUtils.isBlank(extraData.getStartedAt())) {
          String startedAt = String.valueOf(extraData.getStartedAt());

          log.info("startedAt: {}", startedAt);
          Date date = DateUtils.stringFormatToDate(startedAt);
          video.setStartedAt(date);
        }

        // if data had category array
        if (!StringUtils.isBlank(extraData.getCategory())) {
          categories = parseCategory(video.getId(), extraData.getCategory());
          log.info("categories: {}", categories);
          video.setCategory(categories);
        }

        List<VideoGoods> videoGoods = new ArrayList<>();
        if (!StringUtils.isBlank(extraData.getGoods())) {
          String[] userData = StringUtils.deleteWhitespace(String.valueOf(extraData.getGoods())).split(",");
          for (String goodsNo : userData) {
            if (goodsNo.length() != 10) { // invalid goodsNo
              continue;
            }
            goodsRepository.findByGoodsNo(goodsNo).ifPresent(g -> {
              videoGoods.add(new VideoGoods(createdVideo, g, createdVideo.getMember()));
            });
          }

          if (videoGoods.size() > 0) {
            videoGoodsRepository.saveAll(videoGoods);

            // Set related goods count & one thumbnail image
            String url = videoGoods.get(0).getGoods().getListImageData().toString();
            createdVideo.setRelatedGoodsThumbnailUrl(url);
            createdVideo.setRelatedGoodsCount(videoGoods.size());
          }
        }
        if (videoGoods.size() > 0 || categories.size() > 0 || video.getStartedAt() != null) {
          videoRepository.save(createdVideo);
        }
      }
    } else {
      Optional<Video> videoByKey = videoRepository.findByVideoKey(request.getVideoKey());
      if (videoByKey.isPresent()) {
        video = videoByKey.get();
      } else {
        video = videoRepository.findById(Long.parseLong(request.getVideoKey()))
            .orElseGet(() -> {
              log.error("Cannot find videoId: " + request.getVideoKey());
              throw new NotFoundException("video_not_found", "video not found, video_id:" + request.getVideoKey());
            });
      }

      BeanUtils.copyProperties(request, video);
      videoRepository.save(video);
    }

    if ("PUBLIC".equals(request.getVisibility())) {
      member.setPublicVideoCount(member.getPublicVideoCount() + 1);
    }

    member.setTotalVideoCount(member.getTotalVideoCount() + 1);
    memberRepository.save(member);

    log.info("{}", member);
    return video;
  }

  public Video updated(CallbackController.CallbackUpdateVideoRequest request) {
    Video video = getVideo(request.getVideoKey());
    Member requested = findMember(Long.parseLong(request.getUserId()));
    if (isWrongUser(video.getMember().getId(), requested.getId())) {
      log.error("Invalid UserID: " + request.getUserId());
      throw new MemberNotFoundException();
    }

    if (isLockedVideo(video, request.getVisibility())) {
      throw new BadRequestException("video_locked", "");
    }

    /**
     * Requested by mybeautip-batch server at started time of video
     */
    if (request.isFirstOpen()) {
      video.setFirstOpen(true);
      video.setStartedAt(null);
    }

    VideoExtraData extraData = null;
    if (!StringUtils.isBlank(request.getData())) {
      extraData = videoDataService.getDataObject(request.getData());
      log.info("{}", extraData);

      if (request.isFirstOpen()) {
        extraData.setStartedAt(null);
      }
    }

    log.debug("{}", video);
    String oldState = video.getState();

    video = videoService.updateVideoProperties(request, video, extraData);
    video = videoService.update(video);

    if (extraData != null && !StringUtils.isBlank(extraData.getGoods())) {
      log.info("goods {}, request goods: {}", video.getData(), extraData.getGoods());
      videoService.updateVideoGoods(video, extraData.getGoods());
    } else {
      videoService.clearVideoGoods(video);
    }

    if ("BROADCASTED".equals(video.getType())) {
      // Send on-live stats using slack when LIVE ended
      if ("LIVE".equals(oldState) && "VOD".equals(request.getState())) {
        videoService.sendStats(video);
      }

      // Send collect watch counts on LIVE
      if ("LIVE".equals(request.getState())) {
        videoWatchService.collectVideoWatchCount(video);
      }
    }

    log.debug("{}", video);
    return video;
  }

  private Member findMember(Long id) {
    return memberRepository.findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new MemberNotFoundException());
  }

  private boolean isLockedVideo(Video video, String visibility) {
    return video.getLocked() && "VOD".equals(video.getState()) && "PUBLIC".equals(visibility);
  }

  private boolean isWrongUser(Long ownerId, Long userId) {
    return ownerId != userId;
  }

  private Video getVideo(String videoKey) {
    return videoRepository.findByVideoKeyAndDeletedAtIsNull(videoKey)
        .orElseGet(() -> {
          log.error("Cannot find video " + videoKey);
          throw new NotFoundException("video_not_found", "video not found, videoKey: " + videoKey);
        });
  }

  private List<VideoCategory> parseCategory(Long videoId, String category) {
    if (StringUtils.isBlank(category)) {
      return new ArrayList<>();
    }
    String[] categories = category.split(",");
    List<Integer> collect = Arrays.stream(categories)
        .filter(c -> !"0".equals(c)).map(c -> Integer.valueOf(c))
        .collect(Collectors.toList());
    return createCategory(videoId, collect);
  }

  private List<VideoCategory> createCategory(Long videoId, List<Integer> category) {
    List<VideoCategory> categories = new ArrayList<>();
    for (int c : category) {
      if (c > 0) {
        categories.add(new VideoCategory(videoId, c));
      }
    }

    return categories;
  }
}