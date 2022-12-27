package com.jocoos.mybeautip.domain.placard.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.jocoos.mybeautip.domain.placard.persistence.domain.Placard;
import lombok.Getter;

@Getter
public class AdminPlacardDetailResponse {

    @JsonUnwrapped
    private final AdminPlacardResponse response;
    private final String title;
    private final String linkArgument;
    private final String color;

    public AdminPlacardDetailResponse(Placard placard) {
        this.response = new AdminPlacardResponse(placard);
        this.title = placard.getTitle();
        this.linkArgument = placard.getLinkArgument();
        this.color = placard.getColor();
    }
}
