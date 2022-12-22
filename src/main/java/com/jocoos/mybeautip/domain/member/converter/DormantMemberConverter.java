package com.jocoos.mybeautip.domain.member.converter;

import com.jocoos.mybeautip.domain.member.persistence.domain.DormantMember;
import com.jocoos.mybeautip.member.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class DormantMemberConverter {

    public abstract DormantMember convertForDormant(Member member);

    public Member merge(DormantMember dormantMember, Member member) {
        member.setTag(dormantMember.getTag());
        member.setStatus(dormantMember.getStatus());
        member.setVisible(dormantMember.isVisible());
        member.setUsername(dormantMember.getUsername());
        member.setBirthday(dormantMember.getBirthday());
        member.setAvatarFilename(dormantMember.getAvatarFilename());
        member.setEmail(dormantMember.getEmail());
        member.setPoint(dormantMember.getPoint());
        member.setIntro(dormantMember.getIntro());
        member.setLink(dormantMember.getLink());
        member.setPermission(dormantMember.getPermission());
        member.setFollowerCount(dormantMember.getFollowerCount());
        member.setFollowingCount(dormantMember.getFollowingCount());
        member.setReportCount(dormantMember.getReportCount());
        member.setPublicVideoCount(dormantMember.getPublicVideoCount());
        member.setTotalVideoCount(dormantMember.getTotalVideoCount());
        member.setRevenue(dormantMember.getRevenue());
        member.setRevenueModifiedAt(dormantMember.getRevenueModifiedAt());
        member.setPushable(dormantMember.getPushable());
        member.setCreatedAt(dormantMember.getCreatedAt());
        member.setModifiedAt(dormantMember.getModifiedAt());
        member.setLastLoggedAt(dormantMember.getLastLoggedAt());
        member.setDeletedAt(dormantMember.getDeletedAt());
        return member;
    }
}