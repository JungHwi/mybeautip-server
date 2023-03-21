package com.jocoos.mybeautip.domain.broadcast.persistence.domain;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerStatus;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import com.jocoos.mybeautip.domain.broadcast.service.util.ViewerUsernameUtil;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastViewerVo;
import com.jocoos.mybeautip.global.exception.AccessDeniedException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType.MANAGER;

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

    public BroadcastViewer(Broadcast broadcast, BroadcastViewerType type, long memberId, String username) {
        this.broadcast = broadcast;
        this.type = type;
        this.memberId = memberId;
        this.sortedUsername = ViewerUsernameUtil.generateSortedUsername(username);
        this.status = BroadcastViewerStatus.ACTIVE;
        this.joinedAt = ZonedDateTime.now();
    }

    public BroadcastViewer(Broadcast broadcast, BroadcastViewerVo vo) {
        this.broadcast = broadcast;
        this.memberId = vo.memberId();
        this.type = vo.type();
        this.sortedUsername = ViewerUsernameUtil.generateSortedUsername(vo.username());
        this.status = BroadcastViewerStatus.ACTIVE;
        this.joinedAt = ZonedDateTime.now();
    }

    public String getUsername() {
        return ViewerUsernameUtil.generateUsername(this.sortedUsername);
    }

    public BroadcastViewer grantManager(boolean isManager) {
        if (isManager && type.isAvailableManager()) {
            type = MANAGER;
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

    public void reJoin(BroadcastViewerType type, String username) {
        if (this.status == BroadcastViewerStatus.EXILE) {
            throw new AccessDeniedException("You have been banned and will not be allowed to re-enter.");
        }
        this.type = type;
        this.status = BroadcastViewerStatus.ACTIVE;
        this.sortedUsername = ViewerUsernameUtil.generateSortedUsername(username);
        this.joinedAt = ZonedDateTime.now();
        this.isSuspended = false;
        this.suspendedAt = null;
    }

    public boolean isManager() {
        return type == MANAGER;
    }

}
