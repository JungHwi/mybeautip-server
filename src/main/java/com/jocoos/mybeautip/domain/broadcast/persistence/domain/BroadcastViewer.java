package com.jocoos.mybeautip.domain.broadcast.persistence.domain;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerStatus;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BroadcastViewer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private long broadcastId;

    @Column(nullable = false)
    private long memberId;

    @Enumerated(EnumType.STRING)
    private BroadcastViewerType type;

    @Enumerated(EnumType.STRING)
    private BroadcastViewerStatus status;

    @Column(nullable = false)
    private boolean isSuspended = false;

}
