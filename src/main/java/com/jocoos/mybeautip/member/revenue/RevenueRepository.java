package com.jocoos.mybeautip.member.revenue;

import com.jocoos.mybeautip.video.Video;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface RevenueRepository extends JpaRepository<Revenue, Long> {

    Optional<Revenue> findByPurchaseId(Long purchaseId);

    List<Revenue> findByVideoId(Long videoId);

    Slice<Revenue> findByVideo(Video video, Pageable pageable);

    Slice<Revenue> findByVideoAndCreatedAtBefore(Video video, Date createdAt, Pageable pageable);

    Slice<Revenue> findByRevenuePaymentAndConfirmedIsTrueAndIdGreaterThanEqual(RevenuePayment revenuePayment, long id, Pageable pageable);

}
