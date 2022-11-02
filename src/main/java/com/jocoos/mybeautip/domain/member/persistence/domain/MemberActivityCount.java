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
public class MemberActivityCount extends BaseEntity {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Member member;

    @Column
    private int communityCount;

    @Column
    private int communityCommentCount;

    @Column
    private int videoCommentCount;

    public MemberActivityCount(Member member) {
        this.member = member;
        this.communityCount = 0;
        this.communityCommentCount = 0;
        this.videoCommentCount = 0;
    }
}
