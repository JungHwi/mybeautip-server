package com.jocoos.mybeautip.domain.placard.converter;

import com.jocoos.mybeautip.domain.placard.dto.PlacardResponse;
import com.jocoos.mybeautip.domain.placard.persistence.domain.Placard;
import com.jocoos.mybeautip.domain.placard.vo.PlacardLink;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlacardConverter {

    @Mapping(target = "placardLink", ignore = true)
    PlacardResponse convertToResponse(Placard placard);

    List<PlacardResponse> convertToResponse(List<Placard> placard);

    @AfterMapping
    default void convertToResponse(@MappingTarget PlacardResponse placardResponse, Placard placard) {
        PlacardLink placardLink = PlacardLink.builder()
                .linkType(placard.getLinkType())
                .parameter(placard.getLinkArgument())
                .build();

        placardResponse.setPlacardLink(placardLink);
    }
}
