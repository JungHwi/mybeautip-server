package com.jocoos.mybeautip.domain.member.service;

import com.jocoos.mybeautip.domain.member.converter.MemberInvitationInfoConverter;
import com.jocoos.mybeautip.domain.member.dto.MemberInvitationInfoResponse;
import com.jocoos.mybeautip.domain.member.persistence.domain.MemberInvitationInfo;
import com.jocoos.mybeautip.domain.member.persistence.repository.MemberInvitationInfoRepository;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberInvitationInfoService {

    private final MemberInvitationInfoRepository memberInvitationInfoRepository;
    private final MemberInvitationInfoConverter memberInvitationInfoConverter;

    @Transactional(readOnly = true)
    public MemberInvitationInfoResponse getMemberInvitationInfo() {
        final List<MemberInvitationInfo> infos = memberInvitationInfoRepository.findAll();
        return memberInvitationInfoConverter.convertToResponse(getFirstWithValid(infos));
    }

    private MemberInvitationInfo getFirstWithValid(List<MemberInvitationInfo> infos) {
        return infos.stream().findFirst()
                .orElseThrow(() -> new NotFoundException("friend invite info not found"));
    }
}
