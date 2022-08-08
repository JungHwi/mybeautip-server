package com.jocoos.mybeautip.domain.member.converter;

import com.jocoos.mybeautip.domain.community.dto.CommunityMemberResponse;
import com.jocoos.mybeautip.domain.member.dto.MemberEntireInfo;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.restapi.dto.SignupRequest;
import org.mapstruct.*;

import static com.jocoos.mybeautip.global.code.UrlDirectory.AVATAR;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;

@Mapper(componentModel = "spring")
public interface MemberConverter {

    @Mappings({
            @Mapping(target = "member", source = "member"),
            @Mapping(target = "token", ignore = true),
    })
    MemberEntireInfo convert(Member member);

    @Mappings({
            @Mapping(target = "status", constant = "ACTIVE"),
            @Mapping(target = "visible", constant = "false"),
            @Mapping(target = "pushable", constant = "true"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "tag", ignore = true),
            @Mapping(target = "link", ignore = true),
            @Mapping(target = "birthday", ignore = true),
            @Mapping(target = "phoneNumber", ignore = true),
            @Mapping(target = "point", ignore = true),
            @Mapping(target = "intro", ignore = true),
            @Mapping(target = "permission", ignore = true),
            @Mapping(target = "followerCount", ignore = true),
            @Mapping(target = "followingCount", ignore = true),
            @Mapping(target = "reportCount", ignore = true),
            @Mapping(target = "publicVideoCount", ignore = true),
            @Mapping(target = "totalVideoCount", ignore = true),
            @Mapping(target = "revenue", ignore = true),
            @Mapping(target = "revenueModifiedAt", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "modifiedAt", ignore = true),
            @Mapping(target = "lastLoggedAt", ignore = true),
            @Mapping(target = "deletedAt", ignore = true),
    })
    Member convertToMember(SignupRequest signupRequest);

    @AfterMapping
    default void convertToMember(@MappingTarget Member member, SignupRequest signupRequest) {
        member.setLink(signupRequest.getGrantType());
    }


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
    default void convertToInfo(@MappingTarget MemberInfo memberInfo, Member member) {
        memberInfo.setPermission(new MemberInfo.PermissionInfo(member.getPermission()));
    }


    CommunityMemberResponse convertToCommunityMember(Member member);

    @AfterMapping
    default void convertToCommunityMember(@MappingTarget CommunityMemberResponse response, Member member) {
        response.setAvatarUrl(toUrl(member.getAvatarUrl(), AVATAR));
    }
}