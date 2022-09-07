package com.jocoos.mybeautip.domain.point.service;

import com.jocoos.mybeautip.domain.point.dao.MemberPointDetailDao;
import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.member.point.MemberPointDetail;
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
    public List<MemberPointDetail> earnPoints(MemberPoint memberPoint, int currentMemberPoint) {
        List<MemberPointDetail> details = memberPointDetailCalculateService.earnPoints(memberPoint, currentMemberPoint);
        details.forEach(slice -> slice.setCommonData(memberPoint));
        log.info("{}", memberPoint.getExpiryAt());
        return memberPointDetailDao.saveAll(details);
    }

    @Transactional
    public List<MemberPointDetail> usePoints(MemberPoint memberPoint) {
        List<MemberPointDetail> details = memberPointDetailCalculateService.usePoints(memberPoint);
        details.forEach(slice -> slice.setCommonData(memberPoint));
        return memberPointDetailDao.saveAll(details);
    }

    @Transactional
    public List<MemberPointDetail> retrievePoints(MemberPoint memberPoint, int currentMemberPoint) {
        List<MemberPointDetail> details = memberPointDetailCalculateService.retrievePoints(memberPoint, currentMemberPoint);
        details.forEach(slice -> slice.setCommonData(memberPoint));
        return memberPointDetailDao.saveAll(details);
    }
}
