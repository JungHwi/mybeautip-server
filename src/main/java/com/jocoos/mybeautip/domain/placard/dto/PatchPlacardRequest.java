package com.jocoos.mybeautip.domain.placard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.placard.code.PlacardLinkType;
import com.jocoos.mybeautip.domain.placard.code.PlacardStatus;
import com.jocoos.mybeautip.domain.placard.persistence.domain.Placard;
import com.jocoos.mybeautip.global.dto.FileDto;
import lombok.Builder;
import lombok.Getter;
import org.openapitools.jackson.nullable.JsonNullable;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.global.code.FileOperationType.DELETE;
import static com.jocoos.mybeautip.global.code.FileOperationType.UPLOAD;
import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;
import static com.jocoos.mybeautip.global.util.JsonNullableUtils.getIfPresent;

@Getter
@Builder
@SuppressWarnings({"FieldMayBeFinal", ""})
public class PatchPlacardRequest {

    @NotNull
    private JsonNullable<PlacardStatus> status;

    @NotNull
    private JsonNullable<String> imageUrl;

    @NotNull
    private JsonNullable<String> title;

    @NotNull
    private JsonNullable<PlacardLinkType> linkType;
    private JsonNullable<String> linkArgument;
    private JsonNullable<String> description;

    @NotNull
    private JsonNullable<String> color;

    @NotNull
    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private JsonNullable<ZonedDateTime> startedAt;

    @NotNull
    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private JsonNullable<ZonedDateTime> endedAt;


    public Placard edit(Placard placard) {
        return Placard.editBuilder()
                .fromOriginal(placard)
                .status(getIfPresent(status, placard.getStatus()))
                .title(getIfPresent(title, placard.getTitle()))
                .linkType(getIfPresent(linkType, placard.getLinkType()))
                .linkArgument(getIfPresent(linkArgument, placard.getLinkArgument()))
                .description(getIfPresent(description, placard.getDescription()))
                .color(getIfPresent(color, placard.getColor()))
                .startedAt(getIfPresent(startedAt, placard.getStartedAt()))
                .endedAt(getIfPresent(endedAt, placard.getEndedAt()))
                .imageUrl(getIfPresent(imageUrl, placard.getImageUrl()))
                .build();
    }

    public List<FileDto> getFileDto(String originalImageUrl) {
        if (imageUrl.isPresent()) {
           return List.of(new FileDto(DELETE, originalImageUrl), new FileDto(UPLOAD, imageUrl.get()));
        }
        return List.of();
    }
}
