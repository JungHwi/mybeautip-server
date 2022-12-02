package com.jocoos.mybeautip.domain.member.persistence.domain;

import com.jocoos.mybeautip.global.config.jpa.BaseEntity;
import com.jocoos.mybeautip.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MemberMemo extends BaseEntity {

    @Id
    private Long id;

    @Column
    private String memo;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Member member;

    public void update(String memo) {
        this.memo = memo;
    }

    public MemberMemo(String memo, Member member) {
        this.memo = memo;
        this.member = member;
    }
}
