package com.jocoos.mybeautip.member.revenue;

import com.jocoos.mybeautip.global.exception.ErrorCode;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.order.Purchase;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class RevenueService {

    private final RevenuePaymentService revenuePaymentService;
    private final RevenueRepository revenueRepository;
    private final VideoRepository videoRepository;
    private final MemberRepository memberRepository;
    @Value("${mybeautip.revenue.revenue-ratio-live}")
    private int revenueRatioForLive;
    @Value("${mybeautip.revenue.revenue-ratio-vod}")
    private int revenueRatioForVod;
    @Value("${mybeautip.revenue.platform-ratio}")
    private int platformRatio;

    public RevenueService(RevenuePaymentService revenuePaymentService,
                          RevenueRepository revenueRepository,
                          VideoRepository videoRepository,
                          MemberRepository memberRepository) {
        this.revenueRepository = revenueRepository;
        this.revenuePaymentService = revenuePaymentService;
        this.videoRepository = videoRepository;
        this.memberRepository = memberRepository;
    }

    public RevenueOverview getOverview(Long videoId, Member member) {
        Video video = videoRepository.findByIdAndMemberId(videoId, member.getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.VIDEO_NOT_FOUND, "invalid video id or member id"));

        List<Revenue> revenues = revenueRepository.findByVideoId(video.getId());
        RevenueOverview overview = new RevenueOverview(platformRatio, revenues, member.getRevenue());
        log.debug("{}", overview);

        return overview;
    }

    public int getRevenueForLive(Long totalPrice) {
        return Math.toIntExact(((totalPrice * revenueRatioForLive) / 100));
    }

    public int getRevenueForVOD(Long totalPrice) {
        return Math.toIntExact(((totalPrice * revenueRatioForVod) / 100));
    }

    @Transactional
    public Revenue save(Video video, Purchase purchase, int revenueAmount) {
        RevenuePayment revenuePayment = revenuePaymentService.getRevenuePayment(video.getMember(), purchase.getCreatedAt());
        Revenue revenue = revenueRepository.save(new Revenue(video, purchase, revenueAmount, revenuePayment));
        log.debug("revenue: {}", revenue);

        memberRepository.updateRevenue(video.getMember().getId(), revenue.getRevenue());
        return revenue;
    }

    @Transactional
    public void remove(Revenue revenue) {
        log.debug("remove revenue: {}", revenue);

        revenueRepository.delete(revenue);
        memberRepository.updateRevenue(revenue.getVideo().getMember().getId(), -(revenue.getRevenue()));
    }

    @Transactional
    public Revenue confirm(Revenue revenue) {
        log.debug("revenue confirmed: {}", revenue.getId());

        RevenuePayment revenuePayment = revenue.getRevenuePayment();
        if (revenuePayment == null) {
            throw new NotFoundException(ErrorCode.REVENUE_PAYMENT_NOT_FOUND, "Revenue payment is null");
        }
        revenuePaymentService.appendEstimatedAmount(revenuePayment, revenue.getRevenue());

        memberRepository.findByIdAndDeletedAtIsNull(revenue.getVideo().getMember().getId())
                .ifPresent(member -> {
                    member.setRevenueModifiedAt(new Date());
                    memberRepository.save(member);
                });

        revenue.setConfirmed(true);
        return revenueRepository.save(revenue);
    }
}
