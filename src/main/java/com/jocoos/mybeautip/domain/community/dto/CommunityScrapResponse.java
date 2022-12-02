package com.jocoos.mybeautip.domain.community.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommunityScrapResponse implements CursorInterface {

    private Long scrapId;

    private ScrapType type;

    @JsonUnwrapped
    private CommunityResponse communityResponse;

    @Override
    @JsonIgnore
    public String getCursor() {
        return String.valueOf(scrapId);
    }
}
