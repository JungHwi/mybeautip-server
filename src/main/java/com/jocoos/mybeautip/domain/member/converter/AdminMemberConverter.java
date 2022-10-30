package com.jocoos.mybeautip.domain.member.converter;

import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.dto.MemberStatusResponse;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface AdminMemberConverter {
    default List<MemberStatusResponse> convert(Map<MemberStatus, Long> statusCountMap) {
        return MemberStatusResponse.from(statusCountMap);
    }
}
