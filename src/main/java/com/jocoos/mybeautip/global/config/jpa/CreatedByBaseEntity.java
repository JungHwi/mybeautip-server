package com.jocoos.mybeautip.global.config.jpa;

import com.jocoos.mybeautip.member.Member;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@Getter
@MappedSuperclass
public abstract class CreatedByBaseEntity extends BaseEntity {

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
