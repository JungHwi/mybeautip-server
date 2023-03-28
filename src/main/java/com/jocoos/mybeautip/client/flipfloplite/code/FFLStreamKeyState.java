package com.jocoos.mybeautip.client.flipfloplite.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FFLStreamKeyState implements CodeValue {
    INACTIVE("방송 송출 전 RTMP Ingest 또는 CMAF Publish 중단 시점, 또는 방장에 의해 특정 VideoRoom 방송 송출 종료 시점"),
    ACTIVE_PREP("방송 송출 전 첫 RTMP Ingest 수신 시점"),
    ACTIVE("방송 송출 전 첫 CMAF Publish 시점"),
    ACTIVE_LIVE_PREP("방송 송출 중 중단되었던 RTMP Ingest 재수신 시점"),
    ACTIVE_LIVE("방송 송출 중 중단되었던 CMAF Publish 재수신 시점, 또는 방장에 의해 특정 VideoRoom 방송 송출 시작 시점"),
    INACTIVE_LIVE("방송 송출 중 RTMP Ingest 또는 CMAF Publish 중단 시점");


    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
