package com.jocoos.mybeautip.domain.community.persistence.domain;

import com.jocoos.mybeautip.global.config.jpa.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "community_like")
public class CommunityLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @Column
    private long memberId;

    @Column
    private long communityId;

    @Column
    private boolean isLike;

    public CommunityLike(long memberId, long communityId) {
        this.memberId = memberId;
        this.communityId = communityId;
        this.isLike = false;
    }
}