package com.jocoos.mybeautip.domain.broadcast.vo;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastViewer;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@NoArgsConstructor
@Embeddable
public class BroadcastViewerList {

    @OrderBy("sorted_username")
    @OneToMany(mappedBy = "broadcast", fetch = LAZY, cascade = ALL, orphanRemoval = true)
    private List<BroadcastViewer> viewerList = new ArrayList<>();

    @Transient
    private Map<Long, BroadcastViewer> viewerMap;

    public List<Long> getManagerId() {
        return viewerList.stream()
                .filter(viewer -> viewer.getType() == BroadcastViewerType.MANAGER)
                .map(BroadcastViewer::getMemberId)
                .toList();
    }

    public List<Long> outSync(List<BroadcastViewerVo> newViewers) {
        viewerMap = viewerList.stream()
                .collect(Collectors.toMap(BroadcastViewer::getMemberId, Function.identity()));
        List<Long> outManagerIds = new ArrayList<>();

        Map<Long, BroadcastViewerVo> voMap = newViewers.stream()
                .collect(Collectors.toMap(BroadcastViewerVo::memberId, Function.identity()));

        for (BroadcastViewer viewer : viewerList) {
            if (!voMap.containsKey(viewer.getMemberId())) {
                viewer.inactive();
                Long outManagerId = viewer.getType() == BroadcastViewerType.MANAGER ? viewer.getMemberId() : null;
                if (outManagerId != null) {
                    outManagerIds.add(outManagerId);
                }
            }
        }

        return outManagerIds;
    }
}
