package com.jocoos.mybeautip.domain.member.converter;

import com.jocoos.mybeautip.domain.member.dto.MemberEntireInfo;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MemberConverter {

    @Mappings({
            @Mapping(target = "member", source = "member"),
            @Mapping(target = "token", ignore = true),
    })
    MemberEntireInfo convert(Member member);

    @Mappings({
            @Mapping(target = "videoCount", source = "publicVideoCount"),
            @Mapping(target = "followingId", ignore = true),
            @Mapping(target = "reportedId", ignore = true),
            @Mapping(target = "blockedId", ignore = true),
            @Mapping(target = "videos", ignore = true),
            @Mapping(target = "permission", ignore = true),
            @Mapping(target = "memberDetail", ignore = true)
    })
    MemberInfo convertToInfo(Member member);

    @AfterMapping
    default void convertToInfo(@TargetType MemberInfo memberInfo, Member member) {
        memberInfo.setPermission(new MemberInfo.PermissionInfo(member.getPermission()));
    }
}