package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.jocoos.mybeautip.client.flipfloplite.code.FFLChangeStatusType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FFLBroadcastChangeStatusRequest {

    private final String id;
    private final FFLChangeStatusType type;
    private final FFLChangeStatusData data;

}
