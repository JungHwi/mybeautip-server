package com.jocoos.mybeautip.domain.point.service;

import com.jocoos.mybeautip.domain.point.dao.MemberPointDetailDao;
import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.member.point.MemberPointDetail;
import com.jocoos.mybeautip.member.point.UsePointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.member.point.MemberPoint.STATE_UNDER_ZERO_POINT;
import static com.jocoos.mybeautip.member.point.UsePointService.ACTIVITY;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberPointDetailService {

    private final MemberPointDetailDao memberPointDetailDao;

    private final EntityManager entityManager;

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
        List<MemberPointDetail> detailList = new ArrayList<>();
        ListIterator<MemberPointDetail> iterator = getAvailablePointsIterator(memberPoint.getMember().getId());

        int toUsePoint = memberPoint.getPoint();
        int inputPoint = 0;

        while ((toUsePoint -= inputPoint) > 0) {
            if (iterator.hasNext()) {
                MemberPointDetail availablePoint = iterator.next();
                inputPoint = Math.min(toUsePoint, availablePoint.getPoint());
                detailList.add(MemberPointDetail.useStatus(availablePoint, memberPoint.getId(), inputPoint, usePointService, usePointServiceId));
            } else {
                detailList.add(MemberPointDetail.useUnderZeroStatus(memberPoint, toUsePoint, usePointService, usePointServiceId));
                break;
            }
        }

        memberPointDetailDao.saveAll(detailList);
    }

    private ListIterator<MemberPointDetail> getAvailablePointsIterator(long memberId) {
        long cursor = setCursorByLastUsedPoint(memberId);
        List<MemberPointDetail> availablePoints = memberPointDetailDao.findAvailablePointsAfterCursor(memberId, cursor);
        final int alreadyUsedPoint = getAlreadyUsedPoint(cursor);
        subAlreadyUsedPoint(availablePoints, alreadyUsedPoint);
        return availablePoints.listIterator();
    }

    private void subAlreadyUsedPoint(List<MemberPointDetail> availablePoints, int alreadyUsedPoint) {
        availablePoints.stream().findFirst().ifPresent(m -> {
            entityManager.detach(m);
            m.setPoint(m.getPoint() + alreadyUsedPoint);
            if (m.getPoint() == 0) {
                availablePoints.remove(m);
            }
        });
    }

    private int getAlreadyUsedPoint(long cursor) {
        List<MemberPointDetail> usedPointOfCursor = memberPointDetailDao.findUsedPointOfParent(cursor);
        return usedPointOfCursor.stream().mapToInt(MemberPointDetail::getPoint).sum();
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
        List<MemberPointDetail> underZeroPointDetails =
                memberPointDetailDao.findUnderZeroPoints(memberPoint.getMember().getId());
        Map<Long, Integer> pointMap = getPointMap(underZeroPointDetails);

        int earnPoint = memberPoint.getPoint();
        int inputPoint = 0;

        Iterator<MemberPointDetail> iterator = underZeroPointDetails.iterator();
        while ((earnPoint -= inputPoint) > 0) {
            if (iterator.hasNext()) {
                MemberPointDetail underZeroDetail = iterator.next();

                int underZeroPoint = pointMap.get(underZeroDetail.getId());

                inputPoint = Math.min(Math.abs(underZeroPoint), earnPoint);
                underZeroDetail.changeParentIdIfAllAddedUp(inputPoint);
                details.add(MemberPointDetail.earnUnderZeroStatus(memberPoint, underZeroDetail.getId(), inputPoint, usePointService, usePointServiceId));
            } else {
                details.add(MemberPointDetail.earnStatus(memberPoint, earnPoint, usePointService, usePointServiceId));
                break;
            }
        }

        memberPointDetailDao.saveAll(details);
    }

    private Map<Long, Integer> getPointMap(List<MemberPointDetail> underZeroPointDetails) {
        if (!underZeroPointDetails.isEmpty()) {
            int prevPlusPoints = calPrevPlusPoints(underZeroPointDetails);
            Map<Long, Integer> pointMap = underZeroPointDetails.stream()
                    .collect(Collectors.toMap(MemberPointDetail::getId, MemberPointDetail::getPoint));
            subPrevPlusPointAndPut(pointMap, underZeroPointDetails.get(NumberUtils.INTEGER_ZERO), prevPlusPoints);
            return pointMap;
        } else {
            return Collections.emptyMap();
        }
    }

    private void subPrevPlusPointAndPut(Map<Long, Integer> pointMap, MemberPointDetail firstElement, int prevPlusPoints) {
        pointMap.put(firstElement.getId(), firstElement.getPoint() - prevPlusPoints);
    }

    private int calPrevPlusPoints(List<MemberPointDetail> underZeroPointDetails) {
        Long earliestZeroPointId = underZeroPointDetails.get(NumberUtils.INTEGER_ZERO).getId();
        return memberPointDetailDao
                .findByParentId(earliestZeroPointId).stream().mapToInt(MemberPointDetail::getPoint).sum();
    }
}
