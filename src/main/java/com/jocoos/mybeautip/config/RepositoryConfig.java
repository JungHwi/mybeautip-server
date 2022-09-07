package com.jocoos.mybeautip.config;

import com.jocoos.mybeautip.app.AppInfo;
import com.jocoos.mybeautip.banner.Banner;
import com.jocoos.mybeautip.devices.Device;
import com.jocoos.mybeautip.goods.*;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.coupon.Coupon;
import com.jocoos.mybeautip.member.coupon.MemberCoupon;
import com.jocoos.mybeautip.member.order.Order;
import com.jocoos.mybeautip.member.order.Purchase;
import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.member.report.Report;
import com.jocoos.mybeautip.notification.event.PushMessage;
import com.jocoos.mybeautip.recommendation.MotdRecommendationBase;
import com.jocoos.mybeautip.store.Store;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.report.VideoReport;
import com.jocoos.mybeautip.word.BannedWord;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class RepositoryConfig implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration repositoryRestConfiguration, CorsRegistry cors) {
        repositoryRestConfiguration.exposeIdsFor(Coupon.class, Banner.class, Member.class, Order.class,
                Category.class, DeliveryCharge.class, DeliveryChargeDetail.class, Store.class,
                Goods.class, GoodsOption.class, VideoReport.class, Report.class, Purchase.class,
                MemberCoupon.class, MemberPoint.class, MotdRecommendationBase.class, AppInfo.class, Device.class, Video.class, BannedWord.class,
                PushMessage.class);
    }
}