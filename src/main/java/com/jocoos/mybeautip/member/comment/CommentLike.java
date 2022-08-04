package com.jocoos.mybeautip.member.comment;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.global.code.LikeStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "comment_likes")
public class CommentLike extends MemberAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column
    @Enumerated(EnumType.STRING)
    private LikeStatus status;

    public CommentLike(Comment comment) {
        this.comment = comment;
        this.status = LikeStatus.LIKE;
    }

    public void like() {
        this.status = LikeStatus.LIKE;
    }

    public void unlike() {
        this.status = LikeStatus.UNLIKE;
    }
}
