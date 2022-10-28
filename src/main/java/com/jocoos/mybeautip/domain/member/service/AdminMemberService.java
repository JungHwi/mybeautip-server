package com.jocoos.mybeautip.domain.member.service;

import com.jocoos.mybeautip.domain.member.dao.MemberDao;
import com.jocoos.mybeautip.domain.member.dto.MemberStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminMemberService {

    private final MemberDao memberDao;


    @Transactional(readOnly = true)
    public List<MemberStatusResponse> getStatusesWithCount() {
        return memberDao.getStatusesWithCount();
    }
}
