package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.jocoos.mybeautip.client.flipfloplite.code.FFLEntityState;

public record FFLAppInfo(long id,
                         FFLEntityState state,
                         String name) {
}
