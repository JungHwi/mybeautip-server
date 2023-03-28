package com.jocoos.mybeautip.domain.broadcast.dto;

import com.jocoos.mybeautip.client.flipfloplite.code.FFLStreamKeyState;

public record StreamKeyResponse(String streamKey,
                                FFLStreamKeyState status,
                                Long broadcastId) {

}
