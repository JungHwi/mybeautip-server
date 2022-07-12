package com.jocoos.mybeautip.domain.placard.converter;

import com.jocoos.mybeautip.domain.placard.code.PlacardTabType;
import com.jocoos.mybeautip.domain.placard.dto.PlacardResponse;
import com.jocoos.mybeautip.domain.placard.persistence.domain.Placard;
import com.jocoos.mybeautip.domain.placard.persistence.domain.PlacardDetail;
import com.jocoos.mybeautip.domain.placard.vo.PlacardLink;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlacardConverter {

    @Mappings({
            @Mapping(target = "imageUrl", ignore = true),
            @Mapping(target = "placardLink", ignore = true)
    })
    PlacardResponse convertToResponse(Placard placard, @Context PlacardTabType tabType);

    @AfterMapping
    default void convertToResponse(@MappingTarget PlacardResponse placardResponse, Placard placard, @Context PlacardTabType tabType) {
        PlacardDetail detail = placard.getDetailList().stream()
                .filter(details -> details.getTabType() == tabType)
                .findFirst()
                .orElseThrow(() ->new BadRequestException("no_such_placard_detail"));

        placardResponse.setImageUrl(detail.getImageUrl());

        PlacardLink placardLink = PlacardLink.builder()
                .linkType(placard.getLinkType())
                .parameter(placard.getLinkArgument())
                .build();

        placardResponse.setPlacardLink(placardLink);
    }

    @Mappings({
            @Mapping(target = "imageUrl", ignore = true),
            @Mapping(target = "placardLink", ignore = true)
    })
    List<PlacardResponse> convertToResponse(List<Placard> placardList, @Context PlacardTabType tabType);
}
