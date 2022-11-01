package com.jocoos.mybeautip.global.wrapper;

import java.util.List;

public record PageResponse<T>(Long total,
                              List<T> content) {
}
