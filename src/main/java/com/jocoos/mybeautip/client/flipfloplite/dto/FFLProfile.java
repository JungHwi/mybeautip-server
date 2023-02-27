package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.jocoos.mybeautip.client.flipfloplite.code.FFLEntityState;

public record FFLProfile(long id,
                         FFLEntityState state,
                         FFLVideoTranscodingProfileType type,
                         String name) {
}
