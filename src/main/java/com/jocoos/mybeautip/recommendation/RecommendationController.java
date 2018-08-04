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
import com.jocoos.mybeautip.member.MemberInfo;

@Slf4j
@RestController
@RequestMapping("/api/1/recommendations")
public class RecommendationController {

  private final MemberRecommendationRepository memberRecommendationRepository;
  private final GoodsRecommendationRepository goodsRecommendationRepository;

  public RecommendationController(MemberRecommendationRepository memberRecommendationRepository,
                                  GoodsRecommendationRepository goodsRecommendationRepository) {
    this.memberRecommendationRepository = memberRecommendationRepository;
    this.goodsRecommendationRepository = goodsRecommendationRepository;
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
    goods.stream().forEach(recommendation -> {
      GoodsInfo goodsInfo = new GoodsInfo();
      BeanUtils.copyProperties(recommendation.getGoods(), goodsInfo);
      log.debug("goods info: {}", goodsInfo);

      result.add(goodsInfo);
    });

    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}