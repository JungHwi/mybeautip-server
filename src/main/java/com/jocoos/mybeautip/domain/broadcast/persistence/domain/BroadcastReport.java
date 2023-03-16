package com.jocoos.mybeautip.domain.broadcast.persistence.domain;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastReportType;
import com.jocoos.mybeautip.global.config.jpa.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class BroadcastReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BroadcastReportType type;

    @Column
    private long broadcastId;

    @Column
    private long reportedId;

    @Column
    private long reporterId;

    @Column
    private String reason;

    @Column
    private String description;

    @Builder
    public BroadcastReport(BroadcastReportType type, long broadcastId, long reporterId, long reportedId, String reason, String description) {
        this.type = type;
        this.broadcastId = broadcastId;
        this.reportedId = reportedId;
        this.reporterId = reporterId;
        this.reason = reason;
        this.description = description;
    }
}
