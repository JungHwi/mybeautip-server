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
@Table(name = "community_report")
public class CommunityReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @Column
    private long memberId;

    @Column
    private long communityId;

    @Column
    private boolean isReport;

    @Column
    private String description;

    public CommunityReport(long memberId, long communityId) {
        this.memberId = memberId;
        this.communityId = communityId;
        this.isReport = false;
        this.description = "";
    }
}