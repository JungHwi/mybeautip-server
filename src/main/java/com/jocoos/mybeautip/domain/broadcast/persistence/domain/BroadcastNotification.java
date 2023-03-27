package com.jocoos.mybeautip.domain.broadcast.persistence.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class BroadcastNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "broadcast_id")
    private Broadcast broadcast;

    private Boolean isNotifyNeeded;

    public BroadcastNotification(Long memberId, Broadcast broadcast) {
        this.memberId = memberId;
        this.broadcast = broadcast;
        this.isNotifyNeeded = true;
    }

    public void changeNotifyNeeded(Boolean isNotifyNeeded) {
        this.isNotifyNeeded = isNotifyNeeded;
    }

    public Long getBroadcastId() {
        return broadcast.getId();
    }
}
