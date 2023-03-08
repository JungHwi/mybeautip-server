package com.jocoos.mybeautip.client.flipfloplite.dto;

import java.util.List;

public record FFLCursorResponse<T>(long nextCursor,
                                int count,
                                List<T> content) {
}
