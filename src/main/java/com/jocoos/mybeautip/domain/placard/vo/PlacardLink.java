package com.jocoos.mybeautip.domain.placard.vo;

import com.jocoos.mybeautip.domain.placard.code.PlacardLinkType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlacardLink {

    private PlacardLinkType linkType;

    private String parameter;
}
