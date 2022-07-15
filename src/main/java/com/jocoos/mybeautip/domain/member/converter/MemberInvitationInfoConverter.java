package com.jocoos.mybeautip.domain.member.converter;

import com.jocoos.mybeautip.domain.member.dto.MemberInvitationInfoResponse;
import com.jocoos.mybeautip.domain.member.persistence.domain.MemberInvitationInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberInvitationInfoConverter {
    MemberInvitationInfoResponse convertToResponse(MemberInvitationInfo info);
}
