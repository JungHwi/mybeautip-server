package com.jocoos.mybeautip.domain.point.service;

import com.jocoos.mybeautip.domain.event.service.EventService;
import com.jocoos.mybeautip.member.order.Order;
import com.jocoos.mybeautip.member.order.OrderService;
import com.jocoos.mybeautip.member.point.MemberPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class PointReasonService {

    private final OrderService orderService;
    private final EventService eventService;

    @Transactional(readOnly = true)
    public void setReason(List<MemberPoint> content) {
        content.forEach(memberPoint -> setReason(content, memberPoint));
    }

    private void setReason(List<MemberPoint> content, MemberPoint memberPoint) {
        memberPoint.setReason(getEventTitleMap(content), getOrderTitleMap(content));
    }

    @Transactional(readOnly = true)
    public Map<Long, String> getEventTitleMap(List<MemberPoint> memberPointList) {
        Set<Long> eventIds = memberPointList.stream()
                .map(MemberPoint::getEventId)
                .collect(Collectors.toSet());

        return eventService.getEventTitleMap(eventIds);
    }

    @Transactional(readOnly = true)
    public Map<Long, String> getOrderTitleMap(List<MemberPoint> memberPointList) {
        List<Order> orderList = memberPointList.stream()
                .filter(point -> point.getOrder() != null)
                .map(MemberPoint::getOrder)
                .collect(Collectors.toList());

        Set<Long> orderIds = orderList.stream()
                .map(Order::getId)
                .collect(Collectors.toSet());

        return orderService.getPurchaseNameMap(orderIds);
    }
}
