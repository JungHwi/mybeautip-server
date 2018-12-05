package com.jocoos.mybeautip.recommendation;

import com.google.common.collect.Lists;
import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsService;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.restapi.CursorResponse;
import com.jocoos.mybeautip.restapi.VideoController;
import com.jocoos.mybeautip.tag.Tag;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoRepository;
import com.jocoos.mybeautip.video.VideoService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/1/recommendations")
public class RecommendationController {

  private final GoodsService goodsService;
  private final MemberService memberServie;
  private final VideoService videoService;
  private final VideoRepository videoRepository;
  private final MemberRepository memberRepository;
  private final MemberRecommendationRepository memberRecommendationRepository;
  private final GoodsRecommendationRepository goodsRecommendationRepository;
  private final MotdRecommendationRepository motdRecommendationRepository;
  private final MotdRecommendationBaseRepository motdRecommendationBaseRepository;
  private final KeywordRecommendationRepository keywordRecommendationRepository;
  
  private final int SEQ_START = 1;
  private final int MAX_RECOMMENDED_BJ_COUNT = 100;

  public RecommendationController(GoodsService goodsService,
                                  MemberService memberServie,
                                  VideoService videoService,
                                  VideoRepository videoRepository,
                                  MemberRepository memberRepository,
                                  MemberRecommendationRepository memberRecommendationRepository,
                                  GoodsRecommendationRepository goodsRecommendationRepository,
                                  MotdRecommendationRepository motdRecommendationRepository,
                                  MotdRecommendationBaseRepository motdRecommendationBaseRepository,
                                  KeywordRecommendationRepository keywordRecommendationRepository) {
    this.goodsService = goodsService;
    this.memberServie = memberServie;
    this.videoService = videoService;
    this.videoRepository = videoRepository;
    this.memberRepository = memberRepository;
    this.memberRecommendationRepository = memberRecommendationRepository;
    this.goodsRecommendationRepository = goodsRecommendationRepository;
    this.motdRecommendationRepository = motdRecommendationRepository;
    this.motdRecommendationBaseRepository = motdRecommendationBaseRepository;
    this.keywordRecommendationRepository = keywordRecommendationRepository;
  }

  @GetMapping("/members")
  public ResponseEntity<List<MemberInfo>> getRecommendedMembers(
      @RequestParam(defaultValue = "100") int count) {
    Date now = new Date();
    List<MemberRecommendation> members = memberRecommendationRepository.findByStartedAtBeforeAndEndedAtAfterAndMemberVisibleIsTrue(
        now, now, PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "seq")));
    List<MemberInfo> result = Lists.newArrayList();

    members.forEach(r -> {
      MemberInfo memberInfo = memberServie.getMemberInfo(r.getMember());
      if (memberInfo.getVideoCount() > 0) {
        List<VideoController.VideoInfo> videoList = Lists.newArrayList();
        Slice<Video> slice = videoRepository.getUserAllVideos(r.getMember(), new Date(), PageRequest.of(0, 3));
        if (slice.hasContent()) {
          for (Video video : slice) {
            videoList.add(videoService.generateVideoInfo(video));
          }
          memberInfo.setVideos(videoList);
        }
        result.add(memberInfo);
      }
    });

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @GetMapping("/goods")
  public ResponseEntity<List<GoodsInfo>> getRecommendedGoods(@RequestParam(defaultValue = "100") int count) {
    PageRequest page = PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "seq"));
    Date now = new Date();
    Slice<GoodsRecommendation> goods = goodsRecommendationRepository.findByStartedAtBeforeAndEndedAtAfterAndGoodsStateLessThanEqual(
        now, now, Goods.GoodsState.NO_SALE.ordinal(), page);

    List<GoodsInfo> result = Lists.newArrayList();
    goods.stream().forEach(recommendation
        -> result.add(goodsService.generateGoodsInfo(recommendation.getGoods())));

    return new ResponseEntity<>(result, HttpStatus.OK);
  }
  
  @GetMapping("/live")
  public ResponseEntity<List<VideoController.VideoInfo>> getRecommendedLiveVideos() {
    PageRequest page = PageRequest.of(0, MAX_RECOMMENDED_BJ_COUNT, new Sort(Sort.Direction.ASC, "seq"));
    Date now = new Date();
    List<MemberRecommendation> memberList = memberRecommendationRepository.findByStartedAtBeforeAndEndedAtAfterAndMemberVisibleIsTrue(now, now, page);
    List<VideoController.VideoInfo> result = Lists.newArrayList();
    
    for (MemberRecommendation member : memberList) {
      Slice<Video> memberVideos = videoService.findMemberVideos(member.getMember(), "BROADCASTED", "LIVE", null, 1);  // live count assumes always 1
      if (memberVideos.hasContent()) {
        result.add(videoService.generateVideoInfo(memberVideos.getContent().get(0)));
      }
    }
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @GetMapping("/motds")
  public ResponseEntity<List<RecommendedMotdInfo>> getRecommendedMotds(
    @RequestParam(defaultValue = "100") int count,
    @RequestParam(defaultValue = "desc") String direction) {

    Slice<MotdRecommendation> videos = motdRecommendationRepository.findAll(
      PageRequest.of(0, count, new Sort(Sort.Direction.fromString(direction), "seq")));

    return new ResponseEntity<>(createMotdList(videos), HttpStatus.OK);
  }

  private List<RecommendedMotdInfo> createMotdList(Iterable<MotdRecommendation> recommendations) {
    List<RecommendedMotdInfo> info = new ArrayList<>();
    for (MotdRecommendation recommendation : recommendations) {
      info.add(new RecommendedMotdInfo(recommendation, videoService.generateVideoInfo(recommendation.getVideo())));
    }
    return info;
  }

  @GetMapping("/motd-bases")
  public CursorResponse getBaseRecommendedMotds(
     @RequestParam(defaultValue = "10") int count,
     @RequestParam(defaultValue = "desc") String direction,
     @RequestParam(required = false) Long cursor) {

    Date createDate = null;
    if (cursor != null) {
      createDate = new Date(cursor);
    } else {
      createDate = new Date();
    }

    Slice<MotdRecommendationBase> videos = motdRecommendationBaseRepository.findByBaseDateBefore(createDate,
       PageRequest.of(0, count, new Sort(Sort.Direction.fromString(direction), "baseDate")));

    List<RecommendedMotdBaseInfo> result = new ArrayList<>();
    for (MotdRecommendationBase recommendation : videos) {
      result.add(new RecommendedMotdBaseInfo(recommendation, createMotdList(recommendation.getMotds())));
    }

    String nextCursor = null;
    if (!CollectionUtils.isEmpty(result)) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getBaseDate().getTime());
    }

    return new CursorResponse.Builder<>("/api/1/recommendations/motd-bases", result)
       .withCount(count)
       .withCursor(nextCursor)
       .toBuild();
  }


  @GetMapping("/keywords")
  public ResponseEntity<List<KeywordInfo>> getRecommendedKeywords(
    @RequestParam(defaultValue = "100") int count) {
    Slice<KeywordRecommendation> keywords = keywordRecommendationRepository.findAll(
      PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "seq")));

    List<KeywordInfo> result = Lists.newArrayList();
    for (KeywordRecommendation keyword : keywords) {
      result.add(new KeywordInfo(keyword));
    }
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @Data
  class KeywordInfo {
    Integer category;
    MemberInfo member;
    TagInfo tag;
    Integer seq;
    Date startedAt;
    Date endedAt;
    Date createdAt;

    KeywordInfo(KeywordRecommendation keyword) {
      BeanUtils.copyProperties(keyword, this);
      switch (category) {
        case 1:
          member = memberServie.getMemberInfo(keyword.getMember());
          break;

        case 2:
          tag = new TagInfo(keyword.getTag());
          break;

        default:
          break;
      }
    }
  }

  @Data
  private static class TagInfo {
    private String name;
    private Integer refCount;

    TagInfo(Tag tag) {
      BeanUtils.copyProperties(tag, this);
    }
  }
  
  @Data
  public static class RecommendedMotdInfo {
    private Integer seq;
    private Date createdAt;
    private VideoController.VideoInfo content;

    public RecommendedMotdInfo(MotdRecommendation recommendation, VideoController.VideoInfo video) {
      BeanUtils.copyProperties(recommendation, this);
      this.content = video;
    }
  }

  @Data
  public static class RecommendedMotdBaseInfo {
    private Date baseDate;
    private List<RecommendedMotdInfo> motds;
    private Date createdAt;
    private VideoController.VideoInfo content;

    public RecommendedMotdBaseInfo(MotdRecommendationBase motdRecommendationBase) {
      BeanUtils.copyProperties(motdRecommendationBase, this);
    }

    public RecommendedMotdBaseInfo(MotdRecommendationBase motdRecommendationBase, List<RecommendedMotdInfo> motds) {
      this(motdRecommendationBase);
      this.motds = motds;
    }
  }
}