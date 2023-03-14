package com.jocoos.mybeautip.domain.broadcast.persistence.domain;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerStatus;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import com.jocoos.mybeautip.domain.broadcast.service.util.ViewerUsernameUtil;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastViewerVo;
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

    @Column
    private ZonedDateTime suspendedAt;

    @Column(nullable = false)
    private ZonedDateTime joinedAt;

    public BroadcastViewer(Broadcast broadcast, BroadcastViewerVo vo) {
        this.broadcast = broadcast;
        this.memberId = vo.memberId();
        this.type = vo.type();
        this.sortedUsername = ViewerUsernameUtil.generateSortedUsername(vo.username());
        this.status = BroadcastViewerStatus.ACTIVE;
        this.joinedAt = vo.joinedAt();
    }

    public String getUsername() {
        return ViewerUsernameUtil.generateUsername(this.sortedUsername);
    }

    public BroadcastViewer grantManager(boolean isManager) {
        if (isManager && type.isAvailableManager()) {
            type = BroadcastViewerType.MANAGER;
        } else if(!isManager) {
            type = BroadcastViewerType.MEMBER;
        }

        return this;
    }

    public void inactive() {
        this.status = BroadcastViewerStatus.INACTIVE;
    }

    public BroadcastViewer suspend(boolean isSuspended) {
        this.isSuspended = isSuspended;

        if (isSuspended) {
            this.suspendedAt = ZonedDateTime.now();
        } else {
            this.suspendedAt = null;
        }
        return this;
    }

    public BroadcastViewer exile() {
        this.status = BroadcastViewerStatus.EXILE;

        return this;
    }

    public void reJoin(BroadcastViewerVo vo) {
        this.type = vo.type();
        this.joinedAt = vo.joinedAt();
        this.isSuspended = false;
    }
}
