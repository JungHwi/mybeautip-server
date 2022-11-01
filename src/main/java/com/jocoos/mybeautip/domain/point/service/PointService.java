package com.jocoos.mybeautip.domain.point.service;

import com.jocoos.mybeautip.domain.event.service.EventService;
import com.jocoos.mybeautip.domain.point.code.PointStatusGroup;
import com.jocoos.mybeautip.domain.point.converter.MemberPointConverter;
import com.jocoos.mybeautip.domain.point.dto.PointHistoryResponse;
import com.jocoos.mybeautip.domain.point.dto.PointMonthlyStatisticsResponse;
import com.jocoos.mybeautip.member.order.Order;
import com.jocoos.mybeautip.member.order.OrderService;
import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.member.point.MemberPointDetail;
import com.jocoos.mybeautip.member.point.MemberPointDetailRepository;
import com.jocoos.mybeautip.member.point.MemberPointRepository;
import com.jocoos.mybeautip.support.LocalDateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.member.point.MemberPoint.STATE_USE_POINT;

@Service
@RequiredArgsConstructor
public class PointService {

    private final OrderService orderService;
    private final EventService eventService;
    private final MemberPointRepository pointRepository;
    private final MemberPointDetailRepository pointDetailRepository;

    private final MemberPointConverter memberPointConverter;

    @Transactional(readOnly = true)
    public PointMonthlyStatisticsResponse getPointMonthlyHistory(long memberId) {
        int earnedPoint = getEarnedPoint(memberId);
        int expiryPoint = getExpiryPoint(memberId);

        return PointMonthlyStatisticsResponse.builder()
                .earnedPoint(earnedPoint)
                .expiryPoint(expiryPoint)
                .build();
    }


    @Transactional(readOnly = true)
    public List<PointHistoryResponse> getPointHistoryList(long memberId, PointStatusGroup pointStatusGroup, int size, long cursor) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));

        Slice<MemberPoint> memberPointList;
        if (pointStatusGroup == null) {
            memberPointList = pointRepository.findByMemberId(memberId, cursor, pageable);
        } else {
            memberPointList = pointRepository.findByMemberIdAndStateIn(memberId, pointStatusGroup.getLegacyCodeGroup(), cursor, pageable);
        }

        List<PointHistoryResponse> result = memberPointConverter.convertToResponse(memberPointList.getContent());

        Map<Long, String> eventTitleMap = getEventTitleMap(memberPointList);
        Map<Long, String> orderPurchaseMap = getOrderTitleMap(memberPointList);

        for (PointHistoryResponse response : result) {
            if (response.getOrder() != null) {
                response.setTitle(orderPurchaseMap.get(response.getOrder().getId()));
            }

            if (response.getEventId() != null) {
                response.setTitle(eventTitleMap.get(response.getEventId()));
            }

            if (response.getActivityType() != null) {
                response.setTitle(response.getActivityType().getDescription());
            }
        }

        return result;
    }

    private Map<Long, String> getEventTitleMap(Slice<MemberPoint> memberPointList) {
        Set<Long> eventIds = memberPointList.stream()
                .map(MemberPoint::getEventId)
                .collect(Collectors.toSet());

        return eventService.getEventTitleMap(eventIds);
    }

    private Map<Long, String> getOrderTitleMap(Slice<MemberPoint> memberPointList) {
        List<Order> orderList = memberPointList.stream()
                .filter(point -> point.getOrder() != null)
                .map(MemberPoint::getOrder)
                .collect(Collectors.toList());

        Set<Long> orderIds = orderList.stream()
                .map(Order::getId)
                .collect(Collectors.toSet());

        return orderService.getPurchaseNameMap(orderIds);
    }

    private int getEarnedPoint(long memberId) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<MemberPoint> memberPointList = pointRepository.findByMemberPointHistory(memberId, PointStatusGroup.EARN.getLegacyCodeGroup(), LocalDateTimeUtils.getStartDateByMonth(), LocalDateTimeUtils.getEndDateByMonth(), pageable);

        return memberPointList.stream()
                .mapToInt(MemberPoint::getPoint)
                .sum();
    }

    @Transactional(readOnly = true)
    public int getExpiryPoint(long memberId) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<MemberPoint> memberPointList = pointRepository.findByExpiryPoint(memberId, PointStatusGroup.EARN.getLegacyCodeGroup(), LocalDateTimeUtils.getStartDateByMonth(), LocalDateTimeUtils.getEndDateByMonth(), pageable);
        List<Long> pointIds = memberPointList.stream()
                .map(MemberPoint::getId)
                .collect(Collectors.toList());

        List<MemberPointDetail> memberPointDetailList = pointDetailRepository.findByParentIdInAndState(pointIds, STATE_USE_POINT);

        int totalEarned = memberPointList.stream()
                .mapToInt(MemberPoint::getPoint)
                .sum();

        int totalUsed = memberPointDetailList.stream()
                .mapToInt(MemberPointDetail::getPoint)
                .sum();

        return totalEarned + totalUsed;
    }
}
