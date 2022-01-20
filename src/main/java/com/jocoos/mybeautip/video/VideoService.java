package com.jocoos.mybeautip.video;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.jocoos.mybeautip.member.block.Block;
import com.jocoos.mybeautip.support.DateUtils;
import com.jocoos.mybeautip.video.scrap.VideoScrapRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.feed.FeedService;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.block.BlockRepository;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentLike;
import com.jocoos.mybeautip.member.comment.CommentLikeRepository;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import com.jocoos.mybeautip.member.order.Order;
import com.jocoos.mybeautip.member.order.OrderRepository;
import com.jocoos.mybeautip.recoding.ViewRecoding;
import com.jocoos.mybeautip.recoding.ViewRecodingRepository;
import com.jocoos.mybeautip.restapi.CallbackController;
import com.jocoos.mybeautip.restapi.VideoController;
import com.jocoos.mybeautip.support.slack.SlackService;
import com.jocoos.mybeautip.tag.TagService;
import com.jocoos.mybeautip.video.report.VideoReport;
import com.jocoos.mybeautip.video.report.VideoReportRepository;
import com.jocoos.mybeautip.video.view.VideoView;
import com.jocoos.mybeautip.video.view.VideoViewRepository;
import com.jocoos.mybeautip.video.watches.VideoWatch;
import com.jocoos.mybeautip.video.watches.VideoWatchRepository;

@Slf4j
@Service
public class VideoService {

  private final MemberService memberService;
  private final TagService tagService;
  private final FeedService feedService;
  private final SlackService slackService;
  private final VideoRepository videoRepository;
  private final CommentRepository commentRepository;
  private final VideoLikeRepository videoLikeRepository;
  private final VideoWatchRepository videoWatchRepository;
  private final BlockRepository blockRepository;
  private final MemberRepository memberRepository;
  private final GoodsRepository goodsRepository;
  private final VideoGoodsRepository videoGoodsRepository;
  private final VideoViewRepository videoViewRepository;
  private final CommentLikeRepository commentLikeRepository;
  private final VideoReportRepository videoReportRepository;
  private final ViewRecodingRepository viewRecodingRepository;
  private final OrderRepository orderRepository;
  private final VideoDataService videoDataService;
  private final VideoCategoryRepository videoCategoryRepository;
  private final VideoScrapRepository videoScrapRepository;

  @Value("${mybeautip.video.watch-duration}")
  private long watchDuration;

  private static final String VIDEO_NOT_FOUND = "video.not_found";
  
  public VideoService(MemberService memberService,
                      TagService tagService,
                      FeedService feedService,
                      SlackService slackService,
                      VideoRepository videoRepository,
                      CommentRepository commentRepository,
                      VideoLikeRepository videoLikeRepository,
                      VideoWatchRepository videoWatchRepository,
                      BlockRepository blockRepository,
                      MemberRepository memberRepository,
                      GoodsRepository goodsRepository,
                      VideoGoodsRepository videoGoodsRepository,
                      VideoViewRepository videoViewRepository,
                      CommentLikeRepository commentLikeRepository,
                      VideoReportRepository videoReportRepository,
                      ViewRecodingRepository viewRecodingRepository,
                      OrderRepository orderRepository,
                      VideoDataService videoDataService,
                      VideoCategoryRepository videoCategoryRepository,
                      VideoScrapRepository videoScrapRepository) {
    this.memberService = memberService;
    this.tagService = tagService;
    this.feedService = feedService;
    this.slackService = slackService;
    this.videoRepository = videoRepository;
    this.commentRepository = commentRepository;
    this.videoLikeRepository = videoLikeRepository;
    this.videoWatchRepository = videoWatchRepository;
    this.blockRepository = blockRepository;
    this.memberRepository = memberRepository;
    this.goodsRepository = goodsRepository;
    this.videoGoodsRepository = videoGoodsRepository;
    this.videoViewRepository = videoViewRepository;
    this.commentLikeRepository = commentLikeRepository;
    this.videoReportRepository = videoReportRepository;
    this.viewRecodingRepository = viewRecodingRepository;
    this.orderRepository = orderRepository;
    this.videoDataService = videoDataService;
    this.videoCategoryRepository = videoCategoryRepository;
    this.videoScrapRepository = videoScrapRepository;
  }

  public Slice<Video> findVideosWithKeyword(String keyword, String cursor, int count) {
    Date startCursor = StringUtils.isBlank(cursor) ? new Date() : new Date(Long.parseLong(cursor));
    PageRequest page = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "createdAt"));

    return videoRepository.searchVideos(keyword, startCursor, page);
  }

  public Slice<Video> findVideosWithTag(String keyword, String cursor, int count) {
    Date startCursor = StringUtils.isBlank(cursor) ? new Date() : new Date(Long.parseLong(cursor));
    PageRequest page = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "createdAt"));

    return videoRepository.searchVideosWithTag(keyword, startCursor, page);
  }

  public Slice<Video> findVideos(String type, String state, String cursor, int count) {
    return findVideos(type, state, cursor, count, null);
  }

  public Slice<Video> findVideos(String type, String state, String cursor, int count, String sort) {
    Date startCursor = StringUtils.isBlank(cursor) ? new Date() : new Date(Long.parseLong(cursor));

    switch (getRequestFilter(type, state)) {
      case "all":
        if (sort == null) {
          return videoRepository.getAnyoneAllVideos(startCursor, PageRequest.of(0, count));
        }

        try {
          int descCursor = (cursor == null) ? Integer.MAX_VALUE : Integer.parseInt(cursor);
          return findVideosBySort(descCursor, count, sort);
        } catch (NumberFormatException e) {
          return videoRepository.getAnyoneAllVideos(startCursor, PageRequest.of(0, count));
        }
      case "live":
        return videoRepository.getAnyoneLiveVideos(startCursor, PageRequest.of(0, count));
      case "vod":
        return videoRepository.getAnyoneVodVideos(startCursor, PageRequest.of(0, count));
      case "motd":
        return videoRepository.getAnyoneMotdVideos(startCursor, PageRequest.of(0, count));
      case "vod+motd":
        return videoRepository.getAnyoneVodAndMotdVideos(startCursor, PageRequest.of(0, count));
      case "live+vod":
        return videoRepository.getAnyoneLiveAndVodVideos(startCursor, PageRequest.of(0, count));
      default:
        return null;
    }
  }

  private Slice<Video> findVideosBySort(int cursor, int count, String sort) {
    Instant instant = Instant.now();
    Date fromDate = Date.from(instant.minus(120, ChronoUnit.DAYS));
    Date toDate = Date.from(instant.minus(30, ChronoUnit.DAYS));

    switch (sort) {
      case "like":
        return videoRepository.getAnyoneAllVideosOrderByLikeCount(cursor, fromDate, toDate, PageRequest.of(0, count));
      case "view":
      default:

        return videoRepository.getAnyoneAllVideosOrderByViewCount(cursor, fromDate, toDate, PageRequest.of(0, count));
    }
  }

  public Slice<Video> findMyVideos(Member me, String type, String state, String cursor, int count) {
    Date startCursor = StringUtils.isBlank(cursor) ? new Date() : new Date(Long.parseLong(cursor));

    switch (getRequestFilter(type, state)) {
      case "all":
        return videoRepository.getMyAllVideos(me, startCursor, PageRequest.of(0, count));
      case "live":
        return videoRepository.getMyLiveVideos(me, startCursor, PageRequest.of(0, count));
      case "vod":
        return videoRepository.getMyVodVideos(me, startCursor, PageRequest.of(0, count));
      case "motd":
        return videoRepository.getMyMotdVideos(me, startCursor, PageRequest.of(0, count));
      case "vod+motd":
        return videoRepository.getMyVodAndMotdVideos(me, startCursor, PageRequest.of(0, count));
      case "live+vod":
        return videoRepository.getMyLiveAndVodVideos(me, startCursor, PageRequest.of(0, count));
      default:
        return null;
    }
  }

  public Slice<Video> findMemberVideos(Member member, String type, String state, String cursor, int count) {
    Date startCursor = StringUtils.isBlank(cursor) ? new Date() : new Date(Long.parseLong(cursor));

    switch (getRequestFilter(type, state)) {
      case "all":
        return videoRepository.getUserAllVideos(member, startCursor, PageRequest.of(0, count));
      case "live":
        return videoRepository.getUserLiveVideos(member, startCursor, PageRequest.of(0, count));
      case "vod":
        return videoRepository.getUserVodVideos(member, startCursor, PageRequest.of(0, count));
      case "motd":
        return videoRepository.getUserMotdVideos(member, startCursor, PageRequest.of(0, count));
      case "vod+motd":
        return videoRepository.getUserVodAndMotdVideos(member, startCursor, PageRequest.of(0, count));
      case "live+vod":
        return videoRepository.getUserLiveAndVodVideos(member, startCursor, PageRequest.of(0, count));
      default:
        return null;
    }
  }

  private String getRequestFilter(String type, String state) {
    if (type == null && state == null) {
      return "all";
    }

    if (type == null && "live".equalsIgnoreCase(state)) {
      return "live";
    }

    if (type == null && "vod".equalsIgnoreCase(state)) {
      return "vod+motd";
    }

    if ("broadcasted".equalsIgnoreCase(type) && state == null) {
      return "live+vod";
    }

    if ("broadcasted".equalsIgnoreCase(type) && "live".equalsIgnoreCase(state)) {
      return "live";
    }

    if ("broadcasted".equalsIgnoreCase(type) && "vod".equalsIgnoreCase(state)) {
      return "vod";
    }

    if ("uploaded".equalsIgnoreCase(type) && state == null) {
      return "motd";
    }

    if ("uploaded".equalsIgnoreCase(type) && "live".equalsIgnoreCase(state)) {
      return "invalid";
    }

    if ("uploaded".equalsIgnoreCase(type) && "vod".equalsIgnoreCase(state)) {
      return "motd";
    }

    return "all";
  }

  public Slice<Comment> findCommentsByVideoId(Long videoId, Long cursor, Pageable pageable, String direction) {
    Slice<Comment> comments;
    if (cursor != null) {
      if ("next".equals(direction)) {
        comments = commentRepository.findByVideoIdAndIdGreaterThanEqualAndParentIdIsNull(videoId, cursor, pageable);
      } else {
        comments = commentRepository.findByVideoIdAndIdLessThanEqualAndParentIdIsNull(videoId, cursor, pageable);
      }
    } else {
      comments = commentRepository.findByVideoIdAndParentIdIsNull(videoId, pageable);
    }
    return comments;
  }

  public Slice<Comment> findCommentsByParentId(Long parentId, Long cursor, Pageable pageable, String direction) {
    Slice<Comment> comments;
    if (cursor != null) {
      if ("next".equals(direction)) {
        comments = commentRepository.findByParentIdAndIdGreaterThanEqual(parentId, cursor, pageable);
      } else {
        comments = commentRepository.findByParentIdAndIdLessThanEqual(parentId, cursor, pageable);
      }
    } else {
      comments = commentRepository.findByParentId(parentId, pageable);
    }
    return comments;
  }

  public VideoController.VideoInfo generateVideoInfo(Video video) {
    Long likeId = null;
    Long scrapId = null;
    boolean blocked = false;
    
    Long me = memberService.currentMemberId();
    // Set likeID
    if (me != null) {
      Optional<VideoLike> optional = videoLikeRepository.findByVideoIdAndCreatedById(video.getId(), me);
      likeId = optional.map(VideoLike::getId).orElse(null);
      scrapId = videoScrapRepository.findByVideoIdAndCreatedById(video.getId(), me)
          .map(s -> s.getId()).orElse(null);
      log.debug("{}, {}, {}", video.getId(), me, scrapId);
      blocked = blockRepository.findByMeAndMemberYouId(video.getMember().getId(), me).isPresent();
    }
    // Set Watch count
    if ("live".equalsIgnoreCase(video.getState())) {
      long duration = new Date().getTime() - watchDuration;
      video.setWatchCount(videoWatchRepository.countByVideoIdAndModifiedAtAfter(video.getId(), new Date(duration)));
    }

    VideoController.VideoInfo videoInfo = new VideoController.VideoInfo(video, memberService.getMemberInfo(video.getMember()), likeId, blocked);
    if (scrapId != null) {
      videoInfo.setScrapId(scrapId);
    }
    videoInfo.setWatchCount(video.getViewCount());
    videoInfo.setRealWatchCount(video.getWatchCount());
    videoInfo.setCategory(
        video.getCategory().stream().map(c -> c.getCategory()).collect(Collectors.toList())
    );
    return videoInfo;
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public Video setWatcher(Video video, Member me) {
    VideoWatch watch = videoWatchRepository.findByVideoIdAndCreatedById(video.getId(), me.getId()).orElse(null);
    if (watch == null) {
      videoWatchRepository.save(new VideoWatch(video, me));
      video.setTotalWatchCount(video.getTotalWatchCount() + 1);
    } else {
      watch.setModifiedAt(new Date());
      videoWatchRepository.save(watch);
    }

    video = addView(video, me);
    return videoRepository.save(video);
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public Video setWatcherWithGuest(Video video, String guestUsername) {
    VideoWatch watch = videoWatchRepository.findByVideoIdAndUsername(video.getId(), guestUsername).orElse(null);
    if (watch == null) {
      videoWatchRepository.save(new VideoWatch(video, guestUsername));
      video.setTotalWatchCount(video.getTotalWatchCount() + 1);
    } else {
      watch.setModifiedAt(new Date());
      videoWatchRepository.save(watch);
    }

    video = addViewWithGuest(video, guestUsername);
    return videoRepository.save(video);
  }
  
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public Video updateWatcher(Video video, Member me) {
    VideoWatch watch = videoWatchRepository.findByVideoIdAndCreatedById(video.getId(), me.getId()).orElse(null);
    if (watch == null) {
      videoWatchRepository.save(new VideoWatch(video, me));
      video.setTotalWatchCount(video.getTotalWatchCount() + 1);
    } else {
      watch.setModifiedAt(new Date());
      videoWatchRepository.save(watch);
    }
    return videoRepository.save(video);
  }
  
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public Video updateWatcherWithGuest(Video video, String guestUsername) {
    VideoWatch watch = videoWatchRepository.findByVideoIdAndUsername(video.getId(), guestUsername).orElse(null);
    if (watch == null) {
      videoWatchRepository.save(new VideoWatch(video, guestUsername));
      video.setTotalWatchCount(video.getTotalWatchCount() + 1);
    } else {
      watch.setModifiedAt(new Date());
      videoWatchRepository.save(watch);
    }
    return videoRepository.save(video);
  }
  
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public void removeGuestWatcher(Video video, String guestName) {
    videoWatchRepository.findByVideoIdAndUsername(video.getId(), guestName)
        .ifPresent(videoWatchRepository::delete);
  }
  
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public void removeWatcher(Video video, Member me) {
    videoWatchRepository.findByVideoIdAndCreatedById(video.getId(), me.getId())
        .ifPresent(videoWatchRepository::delete);
  }
  
  @Transactional
  public Video create(VideoController.CreateVideoRequest request) {
    Video video = new Video(memberService.currentMember());
    BeanUtils.copyProperties(request, video);

    // Set categories after video is saved
    video.setCategory(null);
    log.debug("{}", video);

    Video createdVideo = videoRepository.save(video); // do not notify
    createdVideo.setVideoKey(String.valueOf(video.getId()));

    if (request.getCategory() != null) {
      List<Integer> category = request.getCategory();
      createdVideo.setCategory(createCategory(createdVideo.getId(), category));
    }

    if (StringUtils.isNotEmpty(createdVideo.getContent())) {
      tagService.increaseRefCount(createdVideo.getContent());
      tagService.addHistory(createdVideo.getContent(), TagService.TAG_VIDEO, createdVideo.getId(), createdVideo.getMember());
    }
  
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
        videoRepository.save(createdVideo);
      }
    }
    
    return createdVideo;
  }

  @Transactional
  public Video startVideo(CallbackController.CallbackStartVideoRequest request, Member member) {
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
        List<VideoCategory> categories = Lists.newArrayList();
        if (!Strings.isNullOrEmpty(extraData.getStartedAt())) {
          String startedAt = String.valueOf(extraData.getStartedAt());

          log.info("startedAt: {}", startedAt);
          Date date = DateUtils.stringFormatToDate(startedAt);
          video.setStartedAt(date);
        }

        // if data had category array
        if (!Strings.isNullOrEmpty(extraData.getCategory())) {
          categories = parseCategory(video.getId(), extraData.getCategory());
          log.info("categories: {}", categories);
          video.setCategory(categories);
        }

        List<VideoGoods> videoGoods = new ArrayList<>();
        if (!Strings.isNullOrEmpty(extraData.getGoods())) {
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
    }

    if ("PUBLIC".equals(request.getVisibility())) {
      memberRepository.updatePublicVideoCount(member.getId(), 1);
    }

    memberRepository.updateTotalVideoCount(member.getId(), 1);

    if (!Strings.isNullOrEmpty(request.getData2())) {
      List<Integer> category = videoDataService.getCategory(request.getData2());
      video.setCategory(createCategory(video.getId(), category));
    }

    return videoRepository.save(video);
  }

  private Date parseStartedAt(String date) {
    LocalDateTime dateTime = LocalDateTime.parse("2018-05-05T11:50:55");
    return new Date();
  }

  private List<VideoCategory> parseCategory(Long videoId, String category) {
    if (Strings.isNullOrEmpty(category)) {
      return Lists.newArrayList();
    }
    String[] categories = category.split(",");
    List<Integer> collect = Arrays.stream(categories)
        .filter(c -> !"0".equals(c)).map(c -> Integer.valueOf(c))
        .collect(Collectors.toList());
    return createCategory(videoId, collect);
  }

  private List<VideoCategory> createCategory(Long videoId, List<Integer> category) {
    List<VideoCategory> categories = Lists.newArrayList();
    for (int c : category) {
      if (c > 0) {
        categories.add(new VideoCategory(videoId, c));        
      }
    }

    return categories;
  }

  @Transactional
  public void updateVideoGoods(Video video, String goods) {
    if (video.getRelatedGoodsCount() != null && video.getRelatedGoodsCount() > 0) {
      videoGoodsRepository.deleteByVideoId(video.getId());
    }

    List<VideoGoods> videoGoods = new ArrayList<>();
    String[] goodses = StringUtils.deleteWhitespace(goods).split("\\,");
    log.info("goodses: {}", Arrays.toString(goodses));

    for (String goodsNo : goodses) {
      if (goodsNo.length() != 10) { // invalid goodsNo
        continue;
      }
      goodsRepository.findByGoodsNo(goodsNo).ifPresent(g -> {
        videoGoods.add(new VideoGoods(video, g, video.getMember()));
      });
    }

    log.info("videoGoods size: {}", videoGoods.size());
    if (videoGoods.size() > 0) {
      videoGoodsRepository.saveAll(videoGoods);

      // Set related goods count & one thumbnail image
      String url = videoGoods.get(0).getGoods().getListImageData().toString();
      video.setRelatedGoodsThumbnailUrl(url);
      video.setRelatedGoodsCount(videoGoods.size());
      videoRepository.save(video);
    }
  }

  @Transactional
  public void clearVideoGoods(Video video) {
    if (video.getRelatedGoodsCount() != null && video.getRelatedGoodsCount() > 0) {
      videoGoodsRepository.deleteByVideoId(video.getId());
    }

    video.setRelatedGoodsThumbnailUrl("");
    video.setRelatedGoodsCount(0);
    videoRepository.save(video);
  }

  public Video updateVideoProperties(CallbackController.CallbackUpdateVideoRequest source, Video target, VideoExtraData extraData) {
    // immutable properties: video_id, video_key, type, owner, likecount, commentcount, relatedgoodscount, relatedgoodsurl
    // mutable properties: title, content, url, thumbnail_url, chatroomid, data, state, duration, visibility, banned, watchcount, heartcount, viewcount
    
    // Can be modified with empty string
    if (source.getContent() != null) {
      tagService.updateRefCount(target.getContent(), source.getContent());
      tagService.updateHistory(target.getContent(), source.getContent(), TagService.TAG_VIDEO, target.getId(), target.getMember());
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

    if(!Strings.isNullOrEmpty(source.getOriginalFilename()) && source.getOriginalFilename().length() > 0) {
      target.setOriginalFilename(source.getOriginalFilename());
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
      if ("BROADCASTED".equals(target.getType()) && "LIVE".equals(target.getState()) && "VOD".equals(source.getState())) {
        target.setEndedAt(new Date());
      }
      
      if (StringUtils.containsAny(source.getState(), "LIVE", "VOD")) {
        target.setState(source.getState());
      }
    }

    if (!Strings.isNullOrEmpty(source.getLiveKey())) {
      target.setLiveKey(source.getLiveKey());
    }

    if (!Strings.isNullOrEmpty(source.getOutputType())) {
      target.setOutputType(source.getOutputType());
    }

    if (source.getVisibility() != null) {
      String prevState = target.getVisibility();
      String newState = source.getVisibility();
      
      Member member = target.getMember();
      if ("PUBLIC".equalsIgnoreCase(prevState) && "PRIVATE".equalsIgnoreCase(newState)) {
        if (member.getPublicVideoCount() > 0) {
          memberRepository.updatePublicVideoCount(member.getId(), -1);
        }
        log.debug("Video state will be changed PUBLIC to PRIVATE: {}", target.getId());
        target.setVisibility(newState);
      }
      
      if ("PRIVATE".equalsIgnoreCase(prevState) && "PUBLIC".equalsIgnoreCase(newState)) {
        memberRepository.updatePublicVideoCount(member.getId(), 1);
        log.debug("Video state will be changed PRIVATE to PUBLIC: {}", target.getId());
        target.setVisibility(newState);
      }
      memberRepository.save(member);
    }

    if (extraData != null) {
      if (!Strings.isNullOrEmpty(extraData.getCategory())) {
        String[] split = extraData.getCategory().split(",");
        List<Integer> category = Arrays.stream(split).map(s -> Integer.valueOf(s)).collect(Collectors.toList());

        if (target.getCategory() != null) {
          videoCategoryRepository.deleteByVideoId(target.getId());
        }

        target.setCategory(createCategory(target.getId(), category));
      }

      if (!Strings.isNullOrEmpty(extraData.getStartedAt())) {
        Date startedAt = DateUtils.stringFormatToDate(extraData.getStartedAt());
        log.info("{}", startedAt);
        target.setStartedAt(startedAt);
      }
    }

    return target;
  }
  
  @Transactional
  public Video deleteVideo(long memberId, Long videoId) {
    return videoRepository.findByIdAndDeletedAtIsNull(videoId)
        .map(v -> {
          if (v.getMember().getId() != memberId) {
            throw new BadRequestException("invalid_user_id", "Invalid user_id: " + memberId);
          }
          tagService.decreaseRefCount(v.getContent());
          tagService.removeHistory(v.getContent(), TagService.TAG_VIDEO, v.getId(), v.getMember());
          saveWithDeletedAt(v);
          videoLikeRepository.deleteByVideoId(v.getId());
          Member member = v.getMember();
          if ("PUBLIC".equals(v.getVisibility())) {
            if (member.getPublicVideoCount() > 0) {
              memberRepository.updatePublicVideoCount(member.getId(), -1);
            }
          }

          if (member.getTotalVideoCount() > 0) {
            memberRepository.updateTotalVideoCount(member.getId(), -1);
          }
          return v;
        })
        .orElseThrow(() -> new NotFoundException("video_not_found", "video not found, videoId: " + videoId));
  }
  
  @Transactional
  public Video deleteVideo(long memberId, String videoKey) {
    return videoRepository.findByVideoKeyAndDeletedAtIsNull(videoKey)
        .map(v -> {
          if (v.getMember().getId() != memberId) {
            throw new BadRequestException("invalid_user_id", "Invalid user_id: " + memberId);
          }
          tagService.decreaseRefCount(v.getContent());
          tagService.removeHistory(v.getContent(), TagService.TAG_VIDEO, v.getId(), v.getMember());
          saveWithDeletedAt(v);
          videoLikeRepository.deleteByVideoId(v.getId());
          Member member = v.getMember();
          if ("PUBLIC".equals(v.getVisibility())) {
            if (member.getPublicVideoCount() > 0) {
              memberRepository.updatePublicVideoCount(member.getId(), -1);
            }
          }
          
          if (member.getTotalVideoCount() > 0) {
            memberRepository.updateTotalVideoCount(member.getId(), -1);
          }
          return v;
        })
        .orElseThrow(() -> new NotFoundException("video_not_found", "video not found, videoKey: " + videoKey));
  }
  
  // Delete All user's videos when member left
  @Transactional
  public void deleteVideos(Member member) {
    videoRepository.findByMemberAndDeletedAtIsNull(member)
        .forEach(video -> {
          tagService.decreaseRefCount(video.getContent());
          tagService.removeHistory(video.getContent(), TagService.TAG_VIDEO, video.getId(), video.getMember());
          video.setDeletedAt(new Date());
          saveWithDeletedAt(video);
          videoLikeRepository.deleteByVideoId(video.getId());
          feedService.feedDeletedVideo(video.getId());
        });
  }
  
  @Transactional
  public Video addViewWithGuest(Video video, String guestName) {
    VideoView view = videoViewRepository.findByVideoIdAndGuestName(video.getId(), guestName).orElse(null);
    if (view == null) {
      videoViewRepository.save(new VideoView(video, guestName));
    } else {
      view.setViewCount(view.getViewCount() + 1);
      videoViewRepository.save(view);
    }
    
    video.setViewCount(video.getViewCount() + 1);
    return videoRepository.save(video);
  }
  
  @Transactional
  public Video addView(Video video, Member me) {
    VideoView view = videoViewRepository.findByVideoIdAndCreatedById(video.getId(), me.getId()).orElse(null);
    if (view == null) {
      videoViewRepository.save(new VideoView(video, me));
    } else {
      view.setViewCount(view.getViewCount() + 1);
      videoViewRepository.save(view);
    }
  
    video.setViewCount(video.getViewCount() + 1);
    return videoRepository.save(video);
  }
  
  @Transactional
  public Video lockVideo(Video video) {
    if (video.getLocked()) {  // Already locked
      return video;
    }
  
    log.debug("Video locked: " + video.getId());
  
    if ("PUBLIC".equals(video.getVisibility())) {
      Member member = video.getMember();
      if (member.getPublicVideoCount() > 0) {
        memberRepository.updatePublicVideoCount(member.getId(), -1);
      }
      video.setVisibility("PRIVATE");
    }
    
    video.setLocked(true);
    feedService.feedDeletedVideo(video.getId());
    return videoRepository.save(video);
  }
  
  @Transactional
  public Video unLockVideo(Video video) {
    log.debug("Video unlocked: " + video.getId());
    video.setLocked(false);
    return videoRepository.save(video);
  }
  
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public Video reportVideo(Video video, Member me, int reasonCode, String reason) {
    videoReportRepository.save(new VideoReport(video, me, reasonCode, reason));
    video.setReportCount(video.getReportCount() + 1);
    return videoRepository.save(video);
  }
  
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public VideoLike likeVideo(Video video) {
    videoRepository.updateLikeCount(video.getId(), 1);
    video.setLikeCount(video.getLikeCount() + 1);
    return videoLikeRepository.save(new VideoLike(video));
  }
  
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public void unLikeVideo(VideoLike liked) {
    videoLikeRepository.delete(liked);
    if (liked.getVideo().getLikeCount() > 0) {
      videoRepository.updateLikeCount(liked.getVideo().getId(), -1);
    }
  }
  
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public CommentLike likeVideoComment(Comment comment) {
    commentRepository.updateLikeCount(comment.getId(), 1);
    return commentLikeRepository.save(new CommentLike(comment));
  }
  
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public void unLikeVideoComment(CommentLike liked) {
    commentLikeRepository.delete(liked);
    if (liked.getComment().getLikeCount() > 0) {
      commentRepository.updateLikeCount(liked.getComment().getId(), -1);
    }
  }
  
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public void increaseHeart(Video video, int count) {
    videoRepository.updateHeartCount(video.getId(), count);
  }
  
  @Async
  public void sendStats(Video video) {
    String statMessage;
    
    // On-live watchers
    String onLiveWatchers;
    List<ViewRecoding> viewRecodings = viewRecodingRepository.findByItemIdAndCategoryAndCreatedAtLessThanEqual(
        video.getId().toString(), ViewRecoding.CATEGORY_VIDEO, video.getEndedAt());
  
    if (viewRecodings.size() > 0) {
      StringBuilder sb = new StringBuilder();
      for (ViewRecoding viewRecoding : viewRecodings) {
        sb.append(viewRecoding.getCreatedBy().getUsername()).append(", ");
      }
      onLiveWatchers = String.format("시청(%d명): %s",
          viewRecodings.size(), StringUtils.left(sb.toString(), sb.toString().length() - 2));
    } else {
      onLiveWatchers = String.format("시청(0명)");
    }
    statMessage = onLiveWatchers;

    // guest count
    int guestCount = videoViewRepository.countByVideoIdAndGuestNameIsNotNull(video.getId());
    statMessage = statMessage + String.format("\n손님(%d명)", guestCount);
    
    // On-live order summary
    if (video.getRelatedGoodsCount() > 0) {
      List<Order> onLiveOrders = orderRepository.findByStateLessThanAndVideoIdAndOnLiveIsTrue(
          Order.State.READY.getValue(), video.getId());
      String orderSummary;
      if (onLiveOrders.size() > 0) {
        int amount = 0;
        StringBuilder sb = new StringBuilder();
        for (Order onLiveOrder : onLiveOrders) {
          amount += onLiveOrder.getPrice();
          sb.append(onLiveOrder.getCreatedBy().getUsername()).append(", ");
        }
        orderSummary = String.format("\n주문(%d건, %d원): %s",
            onLiveOrders.size(), amount, StringUtils.left(sb.toString(), sb.toString().length() - 2));
      } else {
        orderSummary = String.format("\n주문(0건)");
      }
      statMessage = statMessage + orderSummary;
    }
  
    slackService.sendStatsForLiveEnded(video.getId(), statMessage);
  }

  @Transactional
  public Video remove(Video video) {
    feedService.feedDeletedVideo(video.getId());

    video.setDeletedAt(new Date());
    return videoRepository.save(video);
  }

  @Transactional
  public Video restore(Video video) {
    feedService.feedVideo(video);

    video.setDeletedAt(null);
    return videoRepository.save(video);
  }

  /**
   * Wrap method to avoid duplication for feed aspect
   * @param video
   * @return
   */
  public Video save(Video video) {
    return videoRepository.save(video);
  }

  /**
   * Wrap method to avoid duplication for feed aspect
   * @param video
   * @return
   */
  public Video update(Video video) {
    return videoRepository.save(video);
  }

  /**
   * Wrap method to avoid duplication for feed aspect
   * @param video
   * @return
   */
  public Video saveWithDeletedAt(Video video) {
    video.setDeletedAt(new Date());
    return videoRepository.save(video);
  }
}
