package com.jocoos.mybeautip.domain.placard.dto;

import com.jocoos.mybeautip.domain.placard.vo.PlacardLink;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlacardResponse {
    private String imageUrl;

    private PlacardLink placardLink;
}
