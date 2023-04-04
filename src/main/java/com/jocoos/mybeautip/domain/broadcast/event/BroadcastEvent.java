package com.jocoos.mybeautip.domain.broadcast.event;

import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastEditResult;

import java.util.List;

public class BroadcastEvent {
    public record BroadcastEditNotificationEvent(BroadcastEditResult result) {}
    public record BroadcastBulkEditNotificationEvent(List<Long> ids) {}
    public record BroadcastForceFinishEvent(List<Long> inactiveInfluencerMemberIds) {}

}
