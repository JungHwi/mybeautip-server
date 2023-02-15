package com.jocoos.mybeautip.domain.broadcast.vo;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@Builder
public class ViewerSearchCondition {
    private Long broadcastId;
    private BroadcastViewerType type;
    private ViewerCursorCondition cursorCondition;
    private Pageable pageable;
    private Long cursor;
}