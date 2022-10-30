package com.jocoos.mybeautip.domain.member.service;

import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.converter.AdminMemberConverter;
import com.jocoos.mybeautip.domain.member.dao.MemberDao;
import com.jocoos.mybeautip.domain.member.dto.MemberStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AdminMemberService {

    private final MemberDao memberDao;
    private final AdminMemberConverter converter;


    @Transactional(readOnly = true)
    public List<MemberStatusResponse> getStatusesWithCount() {
        Map<MemberStatus, Long> statusCountMap = memberDao.getStatusesWithCount();
        return converter.convert(statusCountMap);
    }
}
