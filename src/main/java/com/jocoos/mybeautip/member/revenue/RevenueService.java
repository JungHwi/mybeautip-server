package com.jocoos.mybeautip.member.revenue;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.order.Purchase;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoRepository;

@Slf4j
@Service
public class RevenueService {

  @Value("${mybeautip.revenue.revenue-ratio}")
  private int revenueRatio;

  @Value("${mybeautip.revenue.platform-ratio}")
  private int platformRatio;


  private final RevenueRepository revenueRepository;
  private final VideoRepository videoRepository;
  private final MemberRepository memberRepository;

  public RevenueService(RevenueRepository revenueRepository,
                        VideoRepository videoRepository,
                        MemberRepository memberRepository) {
    this.revenueRepository = revenueRepository;
    this.videoRepository = videoRepository;
    this.memberRepository = memberRepository;
  }

  public RevenueOverview getOverview(Long videoId, Member member) {
    Video video = videoRepository.findByIdAndMemberId(videoId, member.getId())
       .orElseThrow(() -> new NotFoundException("video_not_fount", "invalid video id or member id"));

    List<Revenue> revenues = revenueRepository.findByVideoId(video.getId());
    RevenueOverview overview = new RevenueOverview(platformRatio, revenues, member.getRevenue());
    log.debug("{}", overview);

    return overview;
  }

  private int getRevenue(Long totalPrice) {
    return Math.toIntExact(((totalPrice * revenueRatio) / 100));
  }

  public Revenue save(Video video, Purchase purchase) {
    Revenue revenue = revenueRepository.save(new Revenue(video, purchase, getRevenue(purchase.getTotalPrice())));
    log.debug("revenue: {}", revenue);

    memberRepository.updateRevenue(video.getMember().getId(), revenue.getRevenue());
    return revenue;
  }
}
