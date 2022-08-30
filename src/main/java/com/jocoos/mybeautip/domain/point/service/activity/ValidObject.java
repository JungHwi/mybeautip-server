package com.jocoos.mybeautip.domain.point.service.activity;

import com.jocoos.mybeautip.member.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ValidObject {

    private final Object domain;
    private final Long domainId;
    private final Member receiveMember;

    @Builder
    private ValidObject(Object domain, Long domainId, Member receiveMember) {
        this.domain = domain;
        this.domainId = domainId;
        this.receiveMember = receiveMember;
    }

    public static ValidObject validReceiver(Member member) {
        return ValidObject.builder()
                .receiveMember(member)
                .build();
    }

    public static ValidObject validDomainIdAndReceiver(Long domainId, Member member) {
        return ValidObject.builder()
                .receiveMember(member)
                .domainId(domainId)
                .build();
    }

    public static ValidObject validDomainAndReceiver(Object domain, Long domainId, Member member) {
        return ValidObject.builder()
                .receiveMember(member)
                .domain(domain)
                .domainId(domainId)
                .build();
    }
}
