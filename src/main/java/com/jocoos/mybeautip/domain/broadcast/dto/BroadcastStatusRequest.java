package com.jocoos.mybeautip.domain.broadcast.dto;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastStatusRequest {
    private BroadcastStatus status;
}
