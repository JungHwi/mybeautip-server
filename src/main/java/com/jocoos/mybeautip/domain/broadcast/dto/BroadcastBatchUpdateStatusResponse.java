package com.jocoos.mybeautip.domain.broadcast.dto;

public record BroadcastBatchUpdateStatusResponse(long toReadyCount,
                                                 long toCancelCount,
                                                 long toEndCount) {

}
