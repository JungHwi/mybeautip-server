package com.jocoos.mybeautip.member.comment;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.member.Member;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "comment_reports")
public class CommentReport extends MemberAuditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long reportedId;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column
    private Integer reasonCode;

    @Column(nullable = false)
    private String reason;

    public CommentReport(Comment comment, Member member, int reasonCode, String reason) {
        this.comment = comment;
        this.reportedId = comment.getCreatedBy().getId();
        this.createdBy = member;
        this.reasonCode = reasonCode;
        this.reason = reason;
    }
}
