package com.jocoos.mybeautip.domain.scrap.dto;

import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScrapResponse implements CursorInterface {

    private long id;

    private ScrapType type;

    private long relationId;

    public String getCursor() {
        return String.valueOf(id);
    }
}
