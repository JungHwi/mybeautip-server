package com.jocoos.mybeautip.member.detail.converter;

import com.jocoos.mybeautip.member.detail.MemberDetail;
import com.jocoos.mybeautip.member.detail.MemberDetailRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberDetailConverter {

    MemberDetail convert(MemberDetailRequest request);
}
