package com.jocoos.mybeautip.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class MyScrapResponse<T> implements CursorInterface {

    private Long scrapId;

    private ScrapType type;

    @JsonUnwrapped
    private T response;

    @Override
    @JsonIgnore
    public String getCursor() {
        return String.valueOf(scrapId);
    }
}
