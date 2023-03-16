package com.jocoos.mybeautip.domain.broadcast.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BroadcastReportType implements CodeValue {

    BROADCAST("방송 신고"),
    MESSAGE("메세지 신고");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
