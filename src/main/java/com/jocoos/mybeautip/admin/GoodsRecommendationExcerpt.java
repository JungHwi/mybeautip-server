package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.recommendation.GoodsRecommendation;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

@Projection(name = "recommended_goods", types = GoodsRecommendation.class)
public interface GoodsRecommendationExcerpt {

    Goods getGoods();

    int getSeq();

    Date getCreatedAt();

    Date getStartedAt();

    Date getEndedAt();

    Member getCreatedBy();
}
