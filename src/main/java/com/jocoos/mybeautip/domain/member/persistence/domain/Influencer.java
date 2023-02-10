package com.jocoos.mybeautip.domain.member.persistence.domain;

import com.jocoos.mybeautip.domain.member.code.InfluencerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "influencer")
public class Influencer {

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private InfluencerStatus status;

    @Column
    private int broadcastCount;

    @Column
    private ZonedDateTime earnedAt;

    public void updateStatus(InfluencerStatus status) {
        if (status == InfluencerStatus.ACTIVE && this.status != status) {
            earnedAt = ZonedDateTime.now();
        }

        this.status = status;
    }

    public Influencer(long memberId) {
        this.id = memberId;
        this.status = InfluencerStatus.INACTIVE;
        this.broadcastCount = 0;
        this.earnedAt = null;
    }
}