package com.jocoos.mybeautip.config;

import com.jocoos.mybeautip.app.AppInfo;
import com.jocoos.mybeautip.goods.*;
import com.jocoos.mybeautip.member.coupon.MemberCoupon;
import com.jocoos.mybeautip.member.order.Purchase;
import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.member.report.Report;
import com.jocoos.mybeautip.recommendation.MotdRecommendationBase;
import com.jocoos.mybeautip.store.Store;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

import com.jocoos.mybeautip.banner.Banner;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.coupon.Coupon;
import com.jocoos.mybeautip.member.order.Order;
import com.jocoos.mybeautip.post.Post;
import com.jocoos.mybeautip.video.report.VideoReport;

@Configuration
public class RepositoryConfig extends RepositoryRestConfigurerAdapter {

  @Override
  public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
    config.exposeIdsFor(Coupon.class, Post.class, Banner.class, Member.class, Order.class,
        Category.class, DeliveryCharge.class, DeliveryChargeDetail.class, Store.class,
        Goods.class, GoodsOption.class, VideoReport.class, Report.class, Post.class, Purchase.class,
       MemberCoupon.class, MemberPoint.class, MotdRecommendationBase.class, AppInfo.class);
  }
}