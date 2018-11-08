package com.jocoos.mybeautip.config;

import com.jocoos.mybeautip.goods.*;
import com.jocoos.mybeautip.store.Store;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

import com.jocoos.mybeautip.banner.Banner;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.coupon.Coupon;
import com.jocoos.mybeautip.member.order.Order;
import com.jocoos.mybeautip.post.Post;

@Configuration
public class RepositoryConfig extends RepositoryRestConfigurerAdapter {

  @Override
  public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
    config.exposeIdsFor(Coupon.class, Post.class, Banner.class, Member.class, Order.class,
        Category.class, DeliveryCharge.class, DeliveryChargeDetail.class, Store.class,
        Goods.class, GoodsOption.class);
  }
}