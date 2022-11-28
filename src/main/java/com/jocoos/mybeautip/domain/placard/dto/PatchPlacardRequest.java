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
import static com.jocoos.mybeautip.global.util.JsonNullableUtils.changeIfPresent;

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


    public void edit(Placard placard) {
        changeIfPresent(status, placard::editStatus);
        changeIfPresent(title, placard::editTitle);
        changeIfPresent(linkType, placard::editLinkType);
        changeIfPresent(linkArgument, placard::editLinkArgument);
        changeIfPresent(description, placard::editDescription);
        changeIfPresent(color, placard::editColor);
        changeIfPresent(startedAt, placard::editStartedAt);
        changeIfPresent(endedAt, placard::editEndedAt);
        changeIfPresent(imageUrl, placard::editImageUrl);
    }
}
