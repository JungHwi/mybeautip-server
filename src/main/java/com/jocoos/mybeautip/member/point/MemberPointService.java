package com.jocoos.mybeautip.member.point;

import com.jocoos.mybeautip.admin.Dates;
import com.jocoos.mybeautip.domain.event.code.EventProductType;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventJoin;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventProduct;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.order.Order;
import com.jocoos.mybeautip.support.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.global.constant.PointConstant.EVENT_POINT_EXPIRATION_DAY;
import static com.jocoos.mybeautip.member.point.MemberPoint.STATE_EARNED_POINT;
import static com.jocoos.mybeautip.member.point.MemberPoint.STATE_USE_POINT;

@Slf4j
@Service
public class MemberPointService {

    private final MemberRepository memberRepository;
    private final MemberPointRepository memberPointRepository;
    private final MemberPointDetailRepository memberPointDetailRepository;
    @Value("${mybeautip.point.earn-ratio}")
    private int pointRatio;
    @Value("${mybeautip.point.remind-expiring-point}")
    private int reminder;

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

        member.earnPoint(eventProduct.getQuantity());
        memberRepository.save(member);

        Date expiryDate = DateUtils.addDay(EVENT_POINT_EXPIRATION_DAY);
        MemberPoint memberPoint = MemberPoint.builder()
                .member(member)
                .eventId(eventId)
                .point(eventProduct.getQuantity())
                .state(STATE_EARNED_POINT)
                .earnedAt(new Date())
                .expiryAt(expiryDate)
                .remind(true)
                .build();

        memberPoint = memberPointRepository.save(memberPoint);

        MemberPointDetail memberPointDetail = MemberPointDetail.builder()
                .memberId(memberId)
                .eventId(eventId)
                .parentId(memberPoint.getId())
                .memberPointId(memberPoint.getId())
                .point(eventProduct.getQuantity())
                .state(STATE_EARNED_POINT)
                .expiryAt(expiryDate)
                .build();

        memberPointDetailRepository.save(memberPointDetail);
    }

    public void usePoints(EventJoin eventJoin) {
        Event event = eventJoin.getEvent();

        Member member = memberRepository.findById(eventJoin.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException("No such member. id - " + eventJoin.getMemberId()));

        member.usePoint(event.getNeedPoint());

        MemberPoint memberPoint = MemberPoint.builder()
                .member(member)
                .eventId(event.getId())
                .point(event.getNeedPoint())
                .state(MemberPoint.STATE_USE_POINT)
                .build();
        memberPoint = memberPointRepository.save(memberPoint);
        usePoints(memberPoint, UsePointService.EVENT, event.getId());
    }

    public void usePoints(Order order, int point) {
        if (order.getCreatedBy() == null) {
            return;
        }

        log.debug("member id: {}, order id: {}, use point: {}", order.getCreatedBy().getId(), order.getId(), point);

        MemberPoint memberPoint = new MemberPoint(order.getCreatedBy(), order, point, MemberPoint.STATE_USE_POINT);
        memberPoint = memberPointRepository.save(memberPoint);
        usePoints(memberPoint, UsePointService.ORDER, order.getId());
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

    private void usePoints(MemberPoint memberPoint, UsePointService usePointService, long usePointServiceId) {
        long cursor = 0;
        int usedPoint = 0;
        List<MemberPointDetail> detailList = new ArrayList<>();

        MemberPointDetail lastUsedPoint =  memberPointDetailRepository.findTopByMemberIdAndStateAndExpiryAtAfterOrderByIdDesc(memberPoint.getMember().getId(), MemberPoint.STATE_USE_POINT, Date.from(Instant.now()))
                .orElse(null);

        if (lastUsedPoint != null) {
            cursor = lastUsedPoint.getParentId();
        }

        List<MemberPoint> pointList = memberPointRepository.getAvailablePoint(memberPoint.getMember().getId(), cursor);

        List<MemberPointDetail> pointDetailList = memberPointDetailRepository.findByParentIdAndState(cursor, STATE_USE_POINT);
        Map<Long, Integer> pointDetailMap = pointDetailList.stream()
                .collect(Collectors.groupingBy(MemberPointDetail::getParentId, Collectors.summingInt(MemberPointDetail::getPoint)));

        for (MemberPoint availablePoint : pointList) {
            if (usedPoint == memberPoint.getPoint()) {
                break;
            }

            int remainingPoint = pointDetailMap.get(availablePoint.getId()) == null ? availablePoint.getPoint() : availablePoint.getPoint() - Math.abs((pointDetailMap.get(availablePoint.getId())));
            if (remainingPoint > 0) {
                if (remainingPoint > memberPoint.getPoint() - usedPoint) {
                    detailList.add(new MemberPointDetail(availablePoint, memberPoint.getId(), memberPoint.getPoint() - usedPoint, usePointService, usePointServiceId));
                    usedPoint += memberPoint.getPoint() - usedPoint;
                } else {
                    detailList.add(new MemberPointDetail(availablePoint, memberPoint.getId(), remainingPoint, usePointService, usePointServiceId));
                    usedPoint += remainingPoint;
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
