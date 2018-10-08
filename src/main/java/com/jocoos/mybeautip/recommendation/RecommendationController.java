package com.jocoos.mybeautip.recommendation;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsService;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.restapi.VideoController;
import com.jocoos.mybeautip.tag.Tag;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoRepository;
import com.jocoos.mybeautip.video.VideoService;

@Slf4j
@RestController
@RequestMapping("/api/1/recommendations")
public class RecommendationController {

  private final GoodsService goodsService;
  private final MemberService memberServie;
  private final VideoService videoService;
  private final VideoRepository videoRepository;
  private final MemberRecommendationRepository memberRecommendationRepository;
  private final GoodsRecommendationRepository goodsRecommendationRepository;
  private final MotdRecommendationRepository motdRecommendationRepository;
  private final KeywordRecommendationRepository keywordRecommendationRepository;

  public RecommendationController(GoodsService goodsService,
                                  MemberService memberServie,
                                  VideoService videoService,
                                  VideoRepository videoRepository,
                                  MemberRecommendationRepository memberRecommendationRepository,
                                  GoodsRecommendationRepository goodsRecommendationRepository,
                                  MotdRecommendationRepository motdRecommendationRepository,
                                  KeywordRecommendationRepository keywordRecommendationRepository) {
    this.goodsService = goodsService;
    this.memberServie = memberServie;
    this.videoService = videoService;
    this.videoRepository = videoRepository;
    this.memberRecommendationRepository = memberRecommendationRepository;
    this.goodsRecommendationRepository = goodsRecommendationRepository;
    this.motdRecommendationRepository = motdRecommendationRepository;
    this.keywordRecommendationRepository = keywordRecommendationRepository;
  }

  @GetMapping("/members")
  public ResponseEntity<List<MemberInfo>> getRecommendedMembers(
      @RequestParam(defaultValue = "100") int count) {
    Slice<MemberRecommendation> members = memberRecommendationRepository.findAll(
        PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "seq")));
    List<MemberInfo> result = Lists.newArrayList();

    members.stream().forEach(r -> {
      MemberInfo memberInfo = memberServie.getMemberInfo(r.getMember());
      List<VideoController.VideoInfo> videoList = Lists.newArrayList();
      Slice<Video> slice = videoRepository.getUserAllVideos(r.getMember(), new Date(), PageRequest.of(0, 3));
      if (slice.hasContent()) {
        for (Video video : slice) {
          videoList.add(videoService.generateVideoInfo(video));
        }
        memberInfo.setVideos(videoList);
      }
      result.add(memberInfo);
    });

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @GetMapping("/goods")
  public ResponseEntity<List<GoodsInfo>> getRecommendedGoods(
      @RequestParam(defaultValue = "100") int count) {
    Slice<GoodsRecommendation> goods = goodsRecommendationRepository.findAll(
        PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "seq")));

    List<GoodsInfo> result = Lists.newArrayList();
    goods.stream().forEach(recommendation
        -> result.add(goodsService.generateGoodsInfo(recommendation.getGoods())));

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @GetMapping("/motd")
  public ResponseEntity<List<VideoController.VideoInfo>> getRecommendedMotds(
    @RequestParam(defaultValue = "100") int count) {
    Slice<MotdRecommendation> videos = motdRecommendationRepository.findAll(
      PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "seq")));

    List<VideoController.VideoInfo> result = Lists.newArrayList();
    for (MotdRecommendation recommendation : videos) {
      videoRepository.findById(recommendation.getVideoId()).map(video ->
        result.add(videoService.generateVideoInfo(video)));
    }
    return new ResponseEntity<>(result, HttpStatus.OK);
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
  class TagInfo {
    String name;
    Integer refCount;

    TagInfo(Tag tag) {
      BeanUtils.copyProperties(tag, this);
    }
  }
}