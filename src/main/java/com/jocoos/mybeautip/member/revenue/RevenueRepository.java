package com.jocoos.mybeautip.member.revenue;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.video.Video;

public interface RevenueRepository extends JpaRepository<Revenue, Long> {

  List<Revenue> findByVideoId(Long videoId);

  Slice<Revenue> findByVideo(Video video, Pageable pageable);

  Slice<Revenue> findByVideoAndCreatedAtBefore(Video video, Date createdAt, Pageable pageable);

  Slice<Revenue> findByRevenuePaymentAndConfirmedAtIsNotNullAndIdAfter(RevenuePayment revenuePayment, long id, Pageable pageable);

}
