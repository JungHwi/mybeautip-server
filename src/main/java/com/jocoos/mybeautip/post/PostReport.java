package com.jocoos.mybeautip.post;

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
@Table(name = "post_reports")
public class PostReport extends MemberAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column
    private Integer reasonCode;

    @Column(nullable = false)
    private String reason;

    public PostReport(Post post, Member member, int reasonCode, String reason) {
        this.post = post;
        this.createdBy = member;
        this.reasonCode = reasonCode;
        this.reason = reason;
    }
}
