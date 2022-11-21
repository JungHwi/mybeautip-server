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

    @Column
    private int allCommunityCount;

    @Column
    private int allCommunityCommentCount;

    @Column
    private int allVideoCommentCount;

    public MemberActivityCount(Member member) {
        this.member = member;
        this.communityCount = 0;
        this.communityCommentCount = 0;
        this.videoCommentCount = 0;
        this.allCommunityCount = 0;
        this.allCommunityCommentCount = 0;
        this.allVideoCommentCount = 0;
    }

    public int getTotalNormalCommentCount() {
        return communityCommentCount + videoCommentCount;
    }
}
