package com.jocoos.mybeautip.domain.member.converter;

import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.dto.AdminMemberDetailResponse;
import com.jocoos.mybeautip.domain.member.dto.MemberStatusResponse;
import com.jocoos.mybeautip.domain.member.vo.MemberSearchResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface AdminMemberConverter {
    default List<MemberStatusResponse> convert(Map<MemberStatus, Long> statusCountMap) {
        return MemberStatusResponse.from(statusCountMap);
    }

    @Mapping(target = ".", source = "result")
    AdminMemberDetailResponse convert(MemberSearchResult result,
                                      Long communityCount,
                                      Long communityCommentCount,
                                      Long videoCommentCount,
                                      Long invitedFriendCount,
                                      int expiryPoint,
                                      boolean isAgreeMarketingTerm);
}
