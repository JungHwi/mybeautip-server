package com.jocoos.mybeautip.domain.broadcast.vo;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;

public record BroadcastEditResult(Broadcast broadcast, String originalThumbnailUrl) {}
