package com.jocoos.mybeautip.domain.broadcast.persistence.domain;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerStatus;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import com.jocoos.mybeautip.domain.broadcast.vo.ViewerCountResult;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class BroadcastStatistics {
    @Id
    private long id;

    @Column(nullable = false)
    private int totalViewerCount;

    @Column(nullable = false)
    private int maxViewerCount;

    @Column(nullable = false)
    private int viewerCount;

    @Column(nullable = false)
    private int memberViewerCount;

    @Column(nullable = false)
    private int guestViewerCount;

    @Column(nullable = false)
    private int reportCount;

    @Column(nullable = false)
    private int heartCount;

    public BroadcastStatistics(long id) {
        this.id = id;
        this.totalViewerCount = 0;
        this.maxViewerCount = 0;
        this.viewerCount = 0;
        this.memberViewerCount = 0;
        this.guestViewerCount = 0;
        this.reportCount = 0;
        this.heartCount = 0;
    }

    public void refresh(List<ViewerCountResult> statistics) {
        int memberViewerCount = statistics.stream()
                .filter(viewer -> (viewer.type() == BroadcastViewerType.MANAGER || viewer.type() == BroadcastViewerType.MEMBER) && viewer.status() == BroadcastViewerStatus.ACTIVE)
                .mapToInt(ViewerCountResult::count)
                .sum();

        int guestViewerCount = statistics.stream()
                .filter(viewer -> viewer.type() == BroadcastViewerType.GUEST && viewer.status() == BroadcastViewerStatus.ACTIVE)
                .mapToInt(ViewerCountResult::count)
                .sum();


        this.totalViewerCount = statistics.stream()
                .mapToInt(ViewerCountResult::count)
                .sum();
        this.viewerCount = memberViewerCount + guestViewerCount;
        this.memberViewerCount = memberViewerCount;
        this.guestViewerCount = guestViewerCount;
        this.maxViewerCount = memberViewerCount + guestViewerCount > this.maxViewerCount ? memberViewerCount + guestViewerCount : this.maxViewerCount;
    }
}
