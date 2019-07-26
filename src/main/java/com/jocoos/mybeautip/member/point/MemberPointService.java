package com.jocoos.mybeautip.member.point;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.admin.Dates;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.order.Order;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class MemberPointService {

  @Value("${mybeautip.point.earn-ratio}")
  private int pointRatio;

  @Value("${mybeautip.point.remind-expiring-point}")
  private int reminder;

  private final MemberRepository memberRepository;
  private final MemberPointRepository memberPointRepository;
  private final MemberPointDetailRepository memberPointDetailRepository;

  public MemberPointService(MemberRepository memberRepository,
                            MemberPointRepository memberPointRepository,
                            MemberPointDetailRepository memberPointDetailRepository) {
    this.memberRepository = memberRepository;
    this.memberPointRepository = memberPointRepository;
    this.memberPointDetailRepository = memberPointDetailRepository;
  }

  public int getExpectedPoint(Member member) {
    AtomicInteger sum = new AtomicInteger();
    memberPointRepository.findByMemberAndState(member, MemberPoint.STATE_WILL_BE_EARNED).stream().forEach(p -> {
      sum.addAndGet(p.getPoint());
    });

    log.debug("points to be expected: {}", sum.get());
    return sum.get();
  }

  public void earnPoints(Order order) {
    if (order.getCreatedBy() == null || order.getExpectedPoint() <= 0) {
      return;
    }

    log.debug("member id: {}, order id: {}, earned point: {}", order.getCreatedBy().getId(), order.getId(), order.getExpectedPoint());

    MemberPoint memberPoint = new MemberPoint(order.getCreatedBy(), order, order.getExpectedPoint());
    memberPointRepository.save(memberPoint);
  }

  public void usePoints(Order order, int point) {
    if (order.getCreatedBy() == null) {
      return;
    }

    log.debug("member id: {}, order id: {}, use point: {}", order.getCreatedBy().getId(), order.getId(), point);

    MemberPoint memberPoint = new MemberPoint(order.getCreatedBy(), order, point, MemberPoint.STATE_USE_POINT);
    memberPointRepository.save(memberPoint);

    // member_point_details
    long cursor = 0;
    int usedPoint = 0;
    List<MemberPointDetail> detailList = new ArrayList<>();

    Optional<MemberPointDetail> optional = memberPointDetailRepository
            .findTopByMemberIdAndStateAndExpiryAtAfterOrderByIdDesc(
                    memberPoint.getMember().getId(), MemberPoint.STATE_USE_POINT, Date.from(Instant.now()));
    if (optional.isPresent()) {
      long parentId = optional.get().getParentId();
      // check point used from parent id
      Optional<MemberPointSum> optSum = memberPointDetailRepository.getSumByParentId(parentId);
      if (optSum.isPresent()) {
        int remainingPoint = optSum.get().getPointSum();

        if (remainingPoint >= point) {
          // point can be consumed by remaining point
          MemberPointDetail detail = new MemberPointDetail(memberPoint, MemberPoint.STATE_USE_POINT, point, parentId);
          memberPointDetailRepository.save(detail);
          return;
        }

        if (remainingPoint > 0) {
          detailList.add(new MemberPointDetail(memberPoint, MemberPoint.STATE_USE_POINT, remainingPoint, parentId));
          usedPoint += remainingPoint;
        }

        cursor = optional.get().getId();
      }
    }

    // consume earned points from cursor to current until all point is used
    List<MemberPointDetail> list = memberPointDetailRepository
            .getAllEarnedPoints(memberPoint.getMember().getId(), MemberPointDetail.getEarnedState(), cursor);
    if (!CollectionUtils.isEmpty(list)) {
      for(MemberPointDetail detail : list) {
        int earnedPoint = detail.getPoint();
        if (usedPoint + earnedPoint >= point) {
          // last point
          detailList.add(new MemberPointDetail(detail, MemberPoint.STATE_USE_POINT, memberPoint.getId(), point - usedPoint, order.getId()));
          break;
        } else {
          usedPoint += earnedPoint;
          detailList.add(new MemberPointDetail(detail, MemberPoint.STATE_USE_POINT, memberPoint.getId(), earnedPoint, order.getId()));
        }
      }
    }

    if (!CollectionUtils.isEmpty(detailList)) {
      memberPointDetailRepository.saveAll(detailList);
    }
  }

  public void revokePoints(Order order) {
    // Revoke used point
    if (order.getPoint() > 0) {
      log.info("Order canceled - used point revoked: {}, {}", order.getId(), order.getPoint());

      // member_point_details
      List<MemberPointDetail> list = memberPointDetailRepository
              .findByOrderIdAndStateAndExpiryAtAfter(order.getId(), MemberPoint.STATE_USE_POINT, Date.from(Instant.now()));
      if (!CollectionUtils.isEmpty(list)) {
        int totalPoint = 0;
        List<MemberPoint> pointList = new ArrayList<>();
        List<MemberPointDetail> pointDetailList = new ArrayList<>();

        for (MemberPointDetail detail : list) {
          int usedPoint = -detail.getPoint();

          totalPoint += usedPoint;
          pointList.add(createRefundedPoint(order.getCreatedBy(), usedPoint, detail.getExpiryAt(), true));
        }

        List<MemberPoint> savedList = memberPointRepository.saveAll(pointList);

        for (MemberPoint point : savedList) {
          pointDetailList.add(new MemberPointDetail(point, MemberPoint.STATE_REFUNDED_POINT));
        }
        memberPointDetailRepository.saveAll(pointDetailList);

        // members
        Member member = order.getCreatedBy();
        member.setPoint(member.getPoint() + totalPoint);
        memberRepository.save(member);
      }
    }

    // Remove expected earning point
    if (order.getExpectedPoint() > 0) {
      log.info("Order canceled - expected earning point removed: {}, {}", order.getId(), order.getExpectedPoint());
      memberPointRepository.findByMemberAndOrderAndPointAndState(
              order.getCreatedBy(), order, order.getExpectedPoint(), MemberPoint.STATE_WILL_BE_EARNED)
              .ifPresent(memberPointRepository::delete);
    }
  }

  // Using from confirmOrder in AdminBatchController
  @Transactional
  public void convertPoint(MemberPoint memberPoint) {
    if (memberPoint == null) {
      return;
    }

    Date now = new Date();
    Date expiry = Dates.afterMonths(now, 12);

    memberPoint.setEarnedAt(now);
    memberPoint.setExpiryAt(expiry);
    memberPoint.setState(MemberPoint.STATE_EARNED_POINT);
    memberPointRepository.save(memberPoint);

    // member_point_details
    MemberPointDetail detail = new MemberPointDetail(memberPoint, MemberPoint.STATE_EARNED_POINT);
    memberPointDetailRepository.save(detail);

    Member member = memberPoint.getMember();
    member.setPoint(member.getPoint() + memberPoint.getPoint());
    memberRepository.save(member);
  }

  @Transactional
  public MemberPoint presentPoint(Long memberId, int point, Date expiryAt) {

    if (point <= 0) {
      throw new BadRequestException("The point must be greater than 0");
    }

    Member m = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException());
    m.setPoint(m.getPoint() + point);
    memberRepository.save(m);

    boolean remind = false;
    if (Date.from(Instant.now().plus(reminder, ChronoUnit.DAYS)).after(expiryAt)) {
      // no need to remind
      remind = true;
    }

    MemberPoint memberPoint = memberPointRepository.save(createPresentPoint(m, point, expiryAt, remind));

    // member_point_details
    MemberPointDetail detail = new MemberPointDetail(memberPoint, MemberPoint.STATE_EARNED_POINT);
    memberPointDetailRepository.save(detail);

    return memberPoint;
  }

  private MemberPoint createPresentPoint(Member member, int point, Date expiryAt, boolean remind) {
    return new MemberPoint(member, null, point, MemberPoint.STATE_PRESENT_POINT, expiryAt, remind);
  }

  private MemberPoint createRefundedPoint(Member member, int point, Date expiryAt, boolean remind) {
    return new MemberPoint(member, null, point, MemberPoint.STATE_REFUNDED_POINT, expiryAt, remind);
  }

  @Transactional
  public MemberPoint expiredPoint(MemberPoint memberPoint, Date expiredAt) {
    if (memberPoint == null) {
      return null;
    }

    memberPoint.setExpiredAt(expiredAt);
    memberPointRepository.save(memberPoint);

    // member_point_details
    Optional<MemberPointSum> optSum = memberPointDetailRepository.getSumByParentId(memberPoint.getId());
    int remainingPoint;
    if (optSum.isPresent()) {
      remainingPoint = optSum.get().getPointSum();
    } else {
      remainingPoint = memberPoint.getPoint();
    }
    if (remainingPoint == 0) {
      return null;
    }

    MemberPoint expiredPoint = new MemberPoint(memberPoint.getMember(), null, remainingPoint, MemberPoint.STATE_EXPIRED_POINT);
    expiredPoint.setCreatedAt(expiredAt);
    expiredPoint.setExpiryAt(memberPoint.getExpiryAt());
    memberPointRepository.save(expiredPoint);

    MemberPointDetail detail = new MemberPointDetail(memberPoint, MemberPoint.STATE_EXPIRED_POINT, remainingPoint, memberPoint.getId(), expiredPoint.getId());
    memberPointDetailRepository.save(detail);

    Member member = memberPoint.getMember();
    if (memberPoint.getPoint() > member.getPoint()) {
      member.setPoint(0);
    } else {
      member.setPoint(member.getPoint() - remainingPoint);
    }

    memberRepository.save(member);
    return expiredPoint;
  }
}
