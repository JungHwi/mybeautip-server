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
        final long memberId = memberPoint.getMember().getId();
        int toUsePoint = memberPoint.getPoint();
        long cursor = setCursorByLastUsedPoint(memberId);
        List<MemberPointDetail> detailList = new ArrayList<>();

        List<MemberPointDetail> availablePoints = memberPointDetailDao.findAvailablePointsAfterCursor(memberId, cursor);
        final int alreadyUsedPoint = getAlreadyUsedPoint(cursor);
        availablePoints.stream().findFirst().ifPresent(m -> {
            entityManager.detach(m);
            m.setPoint(m.getPoint() + alreadyUsedPoint);
            if (m.getPoint() == 0) {
                availablePoints.remove(m);
            }
        });

        ListIterator<MemberPointDetail> iterator = availablePoints.listIterator();
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
        int earnPoint = memberPoint.getPoint();

        List<MemberPointDetail> underZeroPointDetails =
                memberPointDetailDao.findUnderZeroPoints(memberPoint.getMember().getId());

        int prevPlusPoint = 0;
        if (!underZeroPointDetails.isEmpty()) {
            prevPlusPoint = setPrevPlusPoints(underZeroPointDetails);
        }

        int inputPoint = 0;
        Iterator<MemberPointDetail> iterator = underZeroPointDetails.iterator();
        while ((earnPoint -= inputPoint) > 0) {
            if (iterator.hasNext()) {
                MemberPointDetail underZeroDetail = iterator.next();

                int underZeroPoint = underZeroDetail.getPoint();
                if (prevPlusPoint > 0) {
                    underZeroPoint += prevPlusPoint;
                    prevPlusPoint = 0;
                }

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

    private int setPrevPlusPoints(List<MemberPointDetail> underZeroPointDetails) {
        Long earliestZeroPointId = underZeroPointDetails.get(NumberUtils.INTEGER_ZERO).getId();
        return memberPointDetailDao
                .findByParentId(earliestZeroPointId).stream().mapToInt(MemberPointDetail::getPoint).sum();
    }
}
