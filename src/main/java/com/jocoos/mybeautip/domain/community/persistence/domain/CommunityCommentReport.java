
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
@Table(name = "community_comment_report")
public class CommunityCommentReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @Column
    private long memberId;

    @Column
    private long reportedId;

    @Column
    private long commentId;

    @Column
    private boolean isReport;

    @Column
    private String description;

    public CommunityCommentReport(long memberId, long reportedId, long commentId) {
        this.memberId = memberId;
        this.reportedId = reportedId;
        this.commentId = commentId;
        this.isReport = false;
        this.description = "";
    }
}
