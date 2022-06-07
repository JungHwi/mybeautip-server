package com.jocoos.mybeautip.member.coupon;

import com.jocoos.mybeautip.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {

    List<MemberCoupon> findByMemberAndCreatedAtBeforeAndExpiryAtAfterAndUsedAtIsNull(Member member, Date createdAt, Date expiryAt);

    int countByMemberAndCreatedAtBeforeAndExpiryAtAfterAndUsedAtIsNull(Member member, Date createdAt, Date expiryAt);

    int countByIdAndUsedAtIsNull(Long id);

    List<MemberCoupon> findByUsedAtIsNullAndCouponEndedAtBefore(Date date);

    List<MemberCoupon> findByUsedAtIsNullAndExpiredAtIsNullAndExpiryAtBefore(Date date);

    List<MemberCoupon> findByCouponCategoryAndUsedAtIsNullAndExpiryAtBefore(Byte category, Date date);

    Page<MemberCoupon> findByMemberId(Long memberId, Pageable pageable);

    Page<MemberCoupon> findByMemberIdAndUsedAtIsNull(Long memberId, Pageable pageable);

    Page<MemberCoupon> findByMemberIdAndUsedAtIsNotNull(Long memberId, Pageable pageable);

    List<MemberCoupon> findAllByMemberId(Long memberId);

    boolean existsByMemberIdAndCouponId(Long memberId, Long couponId);
}
