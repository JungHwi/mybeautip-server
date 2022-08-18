package com.jocoos.mybeautip.domain.point.util;

import com.jocoos.mybeautip.member.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ValidObject {

    private final Object domain;

    private final Long domainId;

    private final Member member;

    @Builder
    private ValidObject(Object domain, Long domainId, Member member) {
        this.domain = domain;
        this.domainId = domainId;
        this.member = member;
    }

    public static ValidObject noDomain(Member member) {
        return ValidObject.builder()
                .member(member)
                .build();
    }

    public static ValidObject validDomainId(Long domainId, Member member) {
        return ValidObject.builder()
                .member(member)
                .domainId(domainId)
                .build();
    }

    public static ValidObject validDomain(Object domain, Member member) {
        return ValidObject.builder()
                .member(member)
                .domain(domain)
                .build();
    }
}
