package com.jocoos.mybeautip.domain.broadcast.persistence.domain;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerStatus;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class BroadcastViewer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "broadcast_id")
    private Broadcast broadcast;

    @Column
    private Long memberId;

    @Column(name = "sorted_username", nullable = false)
    private String sortedUsername;

    @Enumerated(EnumType.STRING)
    private BroadcastViewerType type;

    @Enumerated(EnumType.STRING)
    private BroadcastViewerStatus status;

    @Column(nullable = false)
    private boolean isSuspended = false;

    @Column(nullable = false)
    private ZonedDateTime joinedAt;

    public BroadcastViewer grantManager(boolean isManager) {
        if (isManager && type.isAvailableManager()) {
            type = BroadcastViewerType.MANAGER;
        } else if(!isManager) {
            type = BroadcastViewerType.MEMBER;
        }

        return this;
    }

    public BroadcastViewer suspend(boolean isSuspended) {
        this.isSuspended = isSuspended;

        return this;
    }

    public BroadcastViewer exile() {
        this.status = BroadcastViewerStatus.EXILE;

        return this;
    }

}
