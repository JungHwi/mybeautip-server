package com.jocoos.mybeautip.admin;

import java.util.Date;

import org.springframework.data.rest.core.config.Projection;

import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.recommendation.GoodsRecommendation;

@Projection(name = "recommended_goods", types = GoodsRecommendation.class)
public interface GoodsRecommendationExcerpt {

  Goods getGoods();

  int getSeq();

  Date getCreatedAt();

  Date getStartedAt();

  Date getEndedAt();

  Member getCreatedBy();
}
