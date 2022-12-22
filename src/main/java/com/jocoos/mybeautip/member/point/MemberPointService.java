package com.jocoos.mybeautip.member.point;

import com.jocoos.mybeautip.admin.Dates;
import com.jocoos.mybeautip.domain.event.code.EventProductType;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventJoin;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventProduct;
import com.jocoos.mybeautip.domain.point.code.ActivityPointType;
import com.jocoos.mybeautip.domain.point.service.MemberPointDetailService;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.order.Order;
import com.jocoos.mybeautip.support.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jocoos.mybeautip.global.constant.PointConstant.DEFAULT_POINT_EXPIRATION_DAY;
import static com.jocoos.mybeautip.global.constant.PointConstant.EVENT_POINT_EXPIRATION_DAY;
import static com.jocoos.mybeautip.member.point.MemberPoint.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberPointService {

    private final MemberRepository memberRepository;
    private final MemberPointRepository memberPointRepository;
    private final MemberPointDetailRepository memberPointDetailRepository;

    private final MemberPointDetailService memberPointDetailService;

    @Value("${mybeautip.point.earn-ratio}")
    private int pointRatio;
    @Value("${mybeautip.point.remind-expiring-point}")
    private int reminder;

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

    @Transactional
    public void earnPoint(EventJoin eventJoin) {
        EventProduct eventProduct = eventJoin.getEventProduct();
        if (eventProduct.getType() != EventProductType.POINT) {
            throw new BadRequestException("Event product type is not POINT.");
        }

        long eventId = eventJoin.getEventId();
        long memberId = eventJoin.getMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("No such member. id - " + memberId));


        Date expiryDate = DateUtils.addDay(EVENT_POINT_EXPIRATION_DAY);
        MemberPoint memberPoint = MemberPoint.builder()
                .member(member)
                .eventId(eventId)
                .point(eventProduct.getPrice())
                .state(STATE_EARNED_POINT)
                .earnedAt(new Date())
                .expiryAt(expiryDate)
                .remind(true)
                .build();

        memberPoint = memberPointRepository.save(memberPoint);

        memberPointDetailService.earnPoints(memberPoint, member.getPoint());

        member.earnPoint(eventProduct.getPrice());
        memberRepository.save(member);
    }

    @Transactional
    public void earnPoint(ActivityPointType type, Long domainId, Member member) {

        Date expiryDate = DateUtils.addDay(DEFAULT_POINT_EXPIRATION_DAY);
        MemberPoint memberPoint = builder()
                .member(member)
                .activityType(type)
                .activityDomainId(domainId)
                .point(type.getPoint())
                .state(STATE_EARNED_POINT)
                .earnedAt(new Date())
                .expiryAt(expiryDate)
                .remind(true)
                .build();
        memberPointRepository.save(memberPoint);

        memberPointDetailService.earnPoints(memberPoint, member.getPoint());

        // 업데이트 쿼리 하나로 변경하는게 낫지 않을까 싶음
        member.earnPoint(type.getPoint());
        memberRepository.save(member);
    }

    @Transactional
    public void earnPoint(Member member, int point) {
        Date expiryDate = DateUtils.addDay(DEFAULT_POINT_EXPIRATION_DAY);
        MemberPoint memberPoint = builder()
                .member(member)
                .point(point)
                .state(STATE_EARNED_POINT)
                .earnedAt(new Date())
                .expiryAt(expiryDate)
                .remind(true)
                .build();

        memberPointRepository.save(memberPoint);

        memberPointDetailService.earnPoints(memberPoint, member.getPoint());

        member.earnPoint(point);
    }

    public void usePoints(Event event, Member member) {
        if (event.getNeedPoint() <= 0) {
            return;
        }

        member.usePoint(event.getNeedPoint());

        MemberPoint memberPoint = MemberPoint.builder()
                .member(member)
                .eventId(event.getId())
                .point(event.getNeedPoint())
                .state(MemberPoint.STATE_USE_POINT)
                .build();
        memberPoint = memberPointRepository.save(memberPoint);

        memberPointDetailService.usePoints(memberPoint);
    }

    @Transactional
    public void retrievePoints(ActivityPointType type, Long domainId, Member member) {
        MemberPoint memberPoint = MemberPoint.builder()
                .member(member)
                .activityType(type)
                .activityDomainId(domainId)
                .point(type.getPoint())
                .state(STATE_RETRIEVE_POINT)
                .build();
        memberPointRepository.save(memberPoint);

        memberPointDetailService.retrievePoints(memberPoint, member.getPoint());

        member.retrievePoint(type.getPoint());
        memberRepository.save(member);
    }

    public void usePoints(Order order, int point) {
        if (order.getCreatedBy() == null) {
            return;
        }

        log.debug("member id: {}, order id: {}, use point: {}", order.getCreatedBy().getId(), order.getId(), point);

        MemberPoint memberPoint = new MemberPoint(order.getCreatedBy(), order, point, MemberPoint.STATE_USE_POINT);
        memberPoint = memberPointRepository.save(memberPoint);
        memberPointDetailService.usePoints(memberPoint);
    }

    private int getRemainingPoint(MemberPointDetail pointDetail) {
        if (pointDetail.getState() == STATE_USE_POINT) {
            return memberPointDetailRepository.findByParentId(pointDetail.getParentId()).stream()
                    .mapToInt(MemberPointDetail::getPoint)
                    .sum();
        } else {
            return memberPointRepository.findById(pointDetail.getParentId())
                    .orElseThrow(() -> new BadRequestException("No such member point. member point id - " + pointDetail.getParentId()))
                    .getPoint();
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
        memberPoint.setState(STATE_EARNED_POINT);
        memberPointRepository.save(memberPoint);

        // member_point_details
        MemberPointDetail detail = new MemberPointDetail(memberPoint, STATE_EARNED_POINT);
        memberPointDetailRepository.save(detail);

        Member member = memberPoint.getMember();
        member.setPoint(member.getPoint() + memberPoint.getPoint());
        memberRepository.save(member);
    }

    @Transactional
    public MemberPoint presentPoint(Long memberId, int point, Date expiryAt) {
        Member m = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Not found Member. id - " + memberId));
        m.earnPoint(point);
        memberRepository.save(m);

        boolean remind = false;
        if (Date.from(Instant.now().plus(reminder, ChronoUnit.DAYS)).after(expiryAt)) {
            // no need to remind
            remind = true;
        }

        MemberPoint memberPoint = memberPointRepository.save(createPresentPoint(m, point, expiryAt, remind));

        // member_point_details
        MemberPointDetail detail = new MemberPointDetail(memberPoint, STATE_EARNED_POINT);
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

        MemberPointDetail detail = new MemberPointDetail(memberPoint, MemberPoint.STATE_EXPIRED_POINT, remainingPoint, memberPoint.getId(), expiredPoint.getId(), memberPoint.getExpiryAt());
        memberPointDetailRepository.save(detail);

        Member member = memberPoint.getMember();
        int point = member.getPoint() - remainingPoint;
        if (point < 0) {
            log.warn("Member point is in invalid state: member id = {}, current = {}, expired = {}", member.getId(), member.getPoint(), remainingPoint);
            point = 0;
        }
        member.setPoint(point);
        memberRepository.save(member);
        return expiredPoint;
    }
}
