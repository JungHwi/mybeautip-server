package com.jocoos.mybeautip.domain.broadcast.persistence.domain;

import com.jocoos.mybeautip.global.config.jpa.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class VodReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column
    private long vodId;

    @Column
    private long reportedId;

    @Column
    private long reporterId;

    @Column
    private String description;

    @Builder
    public VodReport(Vod vod, long reporterId, String description) {
        this.vodId = vod.getId();
        this.reportedId = vod.getMemberId();
        this.reporterId = reporterId;
        this.description = description;
    }
}
