package com.jocoos.mybeautip.domain.point.service;

import com.jocoos.mybeautip.domain.point.dao.MemberPointDao;
import com.jocoos.mybeautip.domain.point.dao.MemberPointDetailDao;
import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.member.point.MemberPointDetail;
import com.jocoos.mybeautip.member.point.UsePointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.member.point.MemberPoint.STATE_UNDER_ZERO_POINT;
import static com.jocoos.mybeautip.member.point.UsePointService.ACTIVITY;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberPointDetailService {

    private final MemberPointDao memberPointDao;
    private final MemberPointDetailDao memberPointDetailDao;

    @Transactional
    public void saveMemberPointDetail(MemberPoint memberPoint,
                                      int currentMemberPoint,
                                      UsePointService service,
                                      long serviceId) {
        int earnPoint = memberPoint.getPoint();
        if (currentMemberPoint < 0) {
            saveAllWithUnderZero(memberPoint, service, serviceId);
        } else {
            MemberPointDetail memberPointDetail =
                    MemberPointDetail.earnStatus(memberPoint, earnPoint, service, serviceId);
            memberPointDetailDao.save(memberPointDetail);
        }
    }

    @Transactional
    public void retrievePoints(int point, MemberPoint memberPoint, long usePointServiceId) {
        if (point > 0) {
            usePoints(memberPoint, ACTIVITY, usePointServiceId);
        } else {
            MemberPointDetail memberPointDetail =
                    MemberPointDetail.useUnderZeroStatus(memberPoint, memberPoint.getPoint(), ACTIVITY, usePointServiceId);
            memberPointDetailDao.save(memberPointDetail);
        }
    }

    @Transactional
    public void usePoints(MemberPoint memberPoint, UsePointService usePointService, long usePointServiceId) {
        final long memberId = memberPoint.getMember().getId();
        int usedPoint = 0;
        long cursor = setCursorByLastUsedPoint(memberId);
        List<MemberPointDetail> detailList = new ArrayList<>();

        List<MemberPoint> availablePoints = memberPointDao.getAvailablePoint(memberId, cursor);
        Map<Long, Integer> alreadyUsedPointMap = setAlreadyUsedPointMap(cursor);

        for (MemberPoint availablePoint : availablePoints) {
            if (usedPoint == memberPoint.getPoint()) {
                break;
            }

            int remainingPoint = getRemainingPoint(alreadyUsedPointMap, availablePoint);

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

        if (usedPoint < memberPoint.getPoint()) {
            detailList.add(MemberPointDetail.useUnderZeroStatus(memberPoint, memberPoint.getPoint() - usedPoint, usePointService, usePointServiceId));
        }

        if (!CollectionUtils.isEmpty(detailList)) {
            memberPointDetailDao.saveAll(detailList);
        }
    }

    private int getRemainingPoint(Map<Long, Integer> pointDetailMap, MemberPoint availablePoint) {
        return pointDetailMap.get(availablePoint.getId()) == null ?
                availablePoint.getPoint() : availablePoint.getPoint() + (pointDetailMap.get(availablePoint.getId()));
    }

    private Map<Long, Integer> setAlreadyUsedPointMap(long cursor) {
        List<MemberPointDetail> usedPointOfCursor = memberPointDetailDao.findUsedPointOfParent(cursor);
        return usedPointOfCursor.stream()
                .collect(Collectors.groupingBy(m -> m.getState() == STATE_UNDER_ZERO_POINT ? m.getMemberPointId() : m.getParentId(),
                        Collectors.summingInt(m -> m.getState() == STATE_UNDER_ZERO_POINT ? -m.getPoint() : m.getPoint())));
    }

    private long setCursorByLastUsedPoint(Long memberId) {
        MemberPointDetail lastUsedPoint = memberPointDetailDao.findLastUsedOrUnderZeroPoint(memberId);
        if (lastUsedPoint != null) {
            return lastUsedPoint.getState() == STATE_UNDER_ZERO_POINT ?
                    lastUsedPoint.getMemberPointId() : lastUsedPoint.getParentId();
        } else {
            return 0;
        }
    }

    private void saveAllWithUnderZero(MemberPoint memberPoint,
                                      UsePointService usePointService,
                                      long usePointServiceId) {

        List<MemberPointDetail> details = new ArrayList<>();
        int earnPoint = memberPoint.getPoint();
        int prevPlusPoints = 0;

        List<MemberPointDetail> underZeroPointDetails =
                memberPointDetailDao.findUnderZeroPoints(memberPoint.getMember().getId());

        if (!underZeroPointDetails.isEmpty()) {
            prevPlusPoints = setPrevPlusPoints(underZeroPointDetails);
        }

        for (MemberPointDetail underZeroDetail : underZeroPointDetails) {
            if (earnPoint <= 0) {
                break;
            }

            int underZeroPoints = underZeroDetail.getPoint();
            if (prevPlusPoints > 0) {
                underZeroPoints += prevPlusPoints;
                prevPlusPoints = 0;
            }

            int inputPoint;
            if (Math.abs(underZeroPoints) > Math.abs(earnPoint)) {
                inputPoint = earnPoint;
                earnPoint += underZeroPoints;
            } else {
                inputPoint = -underZeroPoints;
                earnPoint += underZeroPoints;
                underZeroDetail.underZeroPointAllAddedUp();
            }
            details.add(MemberPointDetail.earnUnderZeroStatus(memberPoint, underZeroDetail.getId(), inputPoint, usePointService, usePointServiceId));
        }

        if (earnPoint > 0) {
            details.add(MemberPointDetail.earnStatus(memberPoint, earnPoint, usePointService, usePointServiceId));
        }

        memberPointDetailDao.saveAll(details);
    }

    private int setPrevPlusPoints(List<MemberPointDetail> underZeroPointDetails) {
        Long earliestZeroPointId = underZeroPointDetails.get(NumberUtils.INTEGER_ZERO).getId();
        return memberPointDetailDao
                .findByParentId(earliestZeroPointId).stream().mapToInt(MemberPointDetail::getPoint).sum();
    }
}
