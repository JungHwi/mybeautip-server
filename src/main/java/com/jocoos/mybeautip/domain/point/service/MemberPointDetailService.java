package com.jocoos.mybeautip.domain.point.service;

import com.jocoos.mybeautip.domain.point.code.ActivityPointType;
import com.jocoos.mybeautip.domain.point.dao.MemberPointDetailDao;
import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.member.point.MemberPointDetail;
import com.jocoos.mybeautip.member.point.UsePointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberPointDetailService {

    private final MemberPointDetailDao memberPointDetailDao;
    private final MemberPointDetailCalculateService memberPointDetailCalculateService;

    @Transactional
    public void earnPoints(MemberPoint memberPoint, int currentMemberPoint, UsePointService service, long serviceId) {
        List<MemberPointDetail> details = memberPointDetailCalculateService.earnPoint(memberPoint, currentMemberPoint);
        details.forEach(slice -> slice.setCommonData(memberPoint, service, serviceId));
        memberPointDetailDao.saveAll(details);
    }

    @Transactional
    public void usePoints(MemberPoint memberPoint, UsePointService service, long serviceId) {
        List<MemberPointDetail> details = memberPointDetailCalculateService.usePoints(memberPoint);
        details.forEach(slice -> slice.setCommonData(memberPoint, service, serviceId));
        memberPointDetailDao.saveAll(details);
    }

    @Transactional
    public void retrievePoints(MemberPoint memberPoint, int currentMemberPoint, ActivityPointType type) {
        List<MemberPointDetail> details = memberPointDetailCalculateService.retrievePoints(memberPoint, currentMemberPoint);
        details.forEach(slice -> slice.setCommonData(memberPoint, type));
        memberPointDetailDao.saveAll(details);
    }
}
