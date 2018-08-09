package com.jocoos.mybeautip.recommendation;

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
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.goods.GoodsService;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.video.VideoGoods;
import com.jocoos.mybeautip.video.VideoGoodsInfo;
import com.jocoos.mybeautip.video.VideoGoodsRepository;

@Slf4j
@RestController
@RequestMapping("/api/1/recommendations")
public class RecommendationController {

  private final GoodsService goodsService;
  private final VideoGoodsRepository videoGoodsRepository;
  private final MemberRepository memberRepository;
  private final MemberRecommendationRepository memberRecommendationRepository;
  private final GoodsRecommendationRepository goodsRecommendationRepository;
  private final MotdRecommendationRepository motdRecommendationRepository;

  public RecommendationController(GoodsService goodsService,
                                  VideoGoodsRepository videoGoodsRepository,
                                  MemberRepository memberRepository,
                                  MemberRecommendationRepository memberRecommendationRepository,
                                  GoodsRecommendationRepository goodsRecommendationRepository,
                                  MotdRecommendationRepository motdRecommendationRepository) {
    this.goodsService = goodsService;
    this.videoGoodsRepository = videoGoodsRepository;
    this.memberRepository = memberRepository;
    this.memberRecommendationRepository = memberRecommendationRepository;
    this.goodsRecommendationRepository = goodsRecommendationRepository;
    this.motdRecommendationRepository = motdRecommendationRepository;
  }

  @GetMapping("/members")
  public ResponseEntity<List<MemberInfo>> getRecommendedMembers(
      @RequestParam(defaultValue = "5") int count) {
    Slice<MemberRecommendation> members = memberRecommendationRepository.findAll(
        PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "seq")));
    List<MemberInfo> result = Lists.newArrayList();

    members.stream().forEach(recommendation -> {
      MemberInfo memberInfo = new MemberInfo();
      BeanUtils.copyProperties(recommendation.getMember(), memberInfo);
      log.debug("member info: {}", memberInfo);

      result.add(memberInfo);
    });

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @GetMapping("/goods")
  public ResponseEntity<List<GoodsInfo>> getRecommendedGoods(
      @RequestParam(defaultValue = "5") int count) {
    Slice<GoodsRecommendation> goods = goodsRecommendationRepository.findAll(
        PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "seq")));

    List<GoodsInfo> result = Lists.newArrayList();
    goods.stream().forEach(recommendation
        -> result.add(goodsService.generateGoodsInfo(recommendation.getGoods())));

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @GetMapping("/motd")
  public ResponseEntity<List<VideoGoodsInfo>> getRecommendedMotds(
    @RequestParam(defaultValue = "5") int count) {
    Slice<MotdRecommendation> videos = motdRecommendationRepository.findAll(
      PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "seq")));

    List<VideoGoodsInfo> result = Lists.newArrayList();
    for (MotdRecommendation recommendation : videos) {
      List<VideoGoods> list = videoGoodsRepository.findAllByVideoKey(recommendation.getVideoKey());
      if (list.size() > 0) {
        result.add(new VideoGoodsInfo(list.get(0),
          new MemberInfo(memberRepository.getOne(list.get(0).getMemberId()))));
      }
    }
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}