package com.jocoos.mybeautip.domain.broadcast.event;

import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastEditResult;

import java.util.List;

public class BroadcastNotificationEvent {
    public record BroadcastEditNotificationEvent(BroadcastEditResult result) {}
    public record BroadcastBulkEditNotificationEvent(List<Long> ids) {}

}
