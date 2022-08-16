package com.jocoos.mybeautip.domain.community.persistence.domain;

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
@Table(name = "community_comment_like")
public class CommunityCommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @Column
    private long memberId;

    @Column
    private long commentId;

    @Column
    private boolean isLike;

    public CommunityCommentLike(long memberId, long commentId) {
        this.memberId = memberId;
        this.commentId = commentId;
        this.isLike = false;
    }
}