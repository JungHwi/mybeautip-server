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
@Table(name = "community_report")
public class CommunityReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @Column
    private long memberId;

    @Column
    private long reportedId;

    @Column
    private long communityId;

    @Column
    private boolean isReport;

    @Column
    private String description;

    public CommunityReport(long memberId, long reportedId, long communityId) {
        this.memberId = memberId;
        this.reportedId = reportedId;
        this.communityId = communityId;
        this.isReport = false;
        this.description = "";
    }
}
