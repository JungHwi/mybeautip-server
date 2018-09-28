package com.jocoos.mybeautip.member.revenue;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.order.OrderRepository;
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
  private final OrderRepository orderRepository;
  private final VideoRepository videoRepository;

  public RevenueService(RevenueRepository revenueRepository,
                        OrderRepository orderRepository,
                        VideoRepository videoRepository) {
    this.revenueRepository = revenueRepository;
    this.orderRepository = orderRepository;
    this.videoRepository = videoRepository;
  }

  public RevenueOverview getOverview(Long videoId, Member member) {
    Video video = videoRepository.findByIdAndMemberId(videoId, member.getId())
       .orElseThrow(() -> new NotFoundException("video_not_fount", "invalid video id or member id"));

    List<Revenue> revenues = revenueRepository.findByVideoId(video.getId());
    RevenueOverview overview = new RevenueOverview(platformRatio, revenues, member.getRevenue());
    log.debug("{}", overview);

    return overview;
  }

  public RevenueOverview getOverview(Member member) {
    List<Revenue> revenues = revenueRepository.findByVideoMemberId(member.getId());
    RevenueOverview overview = new RevenueOverview(platformRatio, revenues, member.getRevenue());
    log.debug("{}", overview);

    return overview;
  }

  public static void main(String[] args) {
    int ratio = 1;
    int price = 12340;

    int point = Math.toIntExact(((price * ratio) / 100));
    System.out.println(point);

  }
}
