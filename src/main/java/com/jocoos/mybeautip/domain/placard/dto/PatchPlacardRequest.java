package com.jocoos.mybeautip.domain.placard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.placard.code.PlacardLinkType;
import com.jocoos.mybeautip.domain.placard.code.PlacardStatus;
import com.jocoos.mybeautip.domain.placard.persistence.domain.Placard;
import lombok.Builder;
import lombok.Getter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;
import static com.jocoos.mybeautip.global.util.JsonNullableUtils.getIfPresent;

@Getter
@Builder
@SuppressWarnings({"FieldMayBeFinal", ""})
public class PatchPlacardRequest {

    private JsonNullable<PlacardStatus> status = JsonNullable.undefined();
    private JsonNullable<String> imageUrl = JsonNullable.undefined();
    private JsonNullable<String> title = JsonNullable.undefined();
    private JsonNullable<PlacardLinkType> linkType = JsonNullable.undefined();
    private JsonNullable<String> linkArgument = JsonNullable.undefined();
    private JsonNullable<String> description = JsonNullable.undefined();
    private JsonNullable<String> color = JsonNullable.undefined();

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private JsonNullable<ZonedDateTime> startedAt = JsonNullable.undefined();

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private JsonNullable<ZonedDateTime> endedAt = JsonNullable.undefined();


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
}
