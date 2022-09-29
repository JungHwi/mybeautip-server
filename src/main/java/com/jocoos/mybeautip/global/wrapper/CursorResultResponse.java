package com.jocoos.mybeautip.global.wrapper;


import io.jsonwebtoken.lang.Collections;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Getter
public class CursorResultResponse<T extends CursorInterface> {

    private String nextCursor;

    private int count;
    private List<T> content;

    public CursorResultResponse(List<T> content) {
        if (CollectionUtils.isEmpty(content)) {
            return;
        }

        this.nextCursor = content.get(content.size() - 1).getCursor();
        this.content = content;
    }

    public CursorResultResponse<T> withCount() {
        this.count = Collections.size(content);
        return this;
    }
}
