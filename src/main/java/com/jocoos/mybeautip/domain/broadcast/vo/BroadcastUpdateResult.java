package com.jocoos.mybeautip.domain.broadcast.vo;

import java.util.List;

public record BroadcastUpdateResult(long count, List<Long> videoKeys) {
}
