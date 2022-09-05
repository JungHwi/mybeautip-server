package com.jocoos.mybeautip.domain.point.service;

import com.jocoos.mybeautip.domain.point.dao.MemberPointDetailDao;
import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.member.point.MemberPointDetail;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.member.point.MemberPoint.*;

@RequiredArgsConstructor
@Service
public class MemberPointDetailCalculateService {

    private final MemberPointDetailDao memberPointDetailDao;
    private final EntityManager entityManager;

    @Transactional
    public List<MemberPointDetail> earnPoint(MemberPoint memberPoint, int currentMemberPoint) {
        if (currentMemberPoint < 0) {
            return getAllSliceWithUnderZero(memberPoint);
        } else {
            return Collections.singletonList(MemberPointDetail.slice(memberPoint.getId(), memberPoint.getPoint(), STATE_EARNED_POINT));
        }
    }

    @Transactional(readOnly = true)
    public List<MemberPointDetail> usePoints(MemberPoint memberPoint) {
        List<MemberPointDetail> details = new ArrayList<>();
        ListIterator<MemberPointDetail> iterator = getAvailablePointsIterator(memberPoint.getMember().getId());

        int toUsePoint = memberPoint.getPoint();
        int inputPoint = 0;

        while ((toUsePoint -= inputPoint) > 0) {
            if (iterator.hasNext()) {
                MemberPointDetail availablePoint = iterator.next();
                inputPoint = Math.min(toUsePoint, availablePoint.getPoint());
                details.add(MemberPointDetail.slice(availablePoint.getMemberPointId(), -inputPoint, STATE_USE_POINT));
            } else {
                details.add(MemberPointDetail.slice(-toUsePoint, STATE_UNDER_ZERO_POINT));
                break;
            }
        }
        return details;
    }

    @Transactional
    public List<MemberPointDetail> retrievePoints(MemberPoint memberPoint, int currentMemberPoint) {
        if (currentMemberPoint > 0) {
            return usePoints(memberPoint);
        } else {
            return Collections.singletonList(MemberPointDetail.slice(memberPoint.getPoint(), STATE_UNDER_ZERO_POINT));
        }
    }

    private List<MemberPointDetail> getAllSliceWithUnderZero(MemberPoint memberPoint) {
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

                inputPoint = getInputPoint(pointMap, earnPoint, underZeroDetail.getId());
                changeParentIdIfAllAddedUp(underZeroDetail, inputPoint, earnPoint);

                details.add(MemberPointDetail.slice(underZeroDetail.getMemberPointId(), inputPoint, STATE_UNDER_ZERO_POINT));
            } else {
                details.add(MemberPointDetail.slice(memberPoint.getId(), earnPoint, STATE_EARNED_POINT));
                break;
            }
        }
        return details;
    }

    private void changeParentIdIfAllAddedUp(MemberPointDetail underZeroDetail, int inputPoint, int earnPoint) {
        if (inputPoint <= earnPoint) {
            underZeroDetail.underZeroPointAllAddedUp();
        }
    }

    private int getInputPoint(Map<Long, Integer> pointMap, int earnPoint, long underZeroPointDetailId) {
        int underZeroPoint = pointMap.get(underZeroPointDetailId);
        return Math.min(Math.abs(underZeroPoint), earnPoint);
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
        pointMap.put(firstElement.getId(), firstElement.getPoint() + prevPlusPoints);
    }

    private int calPrevPlusPoints(List<MemberPointDetail> underZeroPointDetails) {
        Long earliestZeroPointId = underZeroPointDetails.get(NumberUtils.INTEGER_ZERO).getMemberPointId();
        return memberPointDetailDao
                .findByParentId(earliestZeroPointId).stream().mapToInt(MemberPointDetail::getPoint).sum();
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
}
