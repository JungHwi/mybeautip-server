package com.jocoos.mybeautip.domain.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.event.code.EventStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
@Setter
@Builder
public class EditEventRequest {

    private Long id;

    @NotNull(message = "status must not be null")
    private EventStatus status;

    private Integer sorting;

    @NotNull(message = "visible must not be null")
    private Boolean isVisible;

    @NotNull(message = "title must not be null")
    private String title;

    private String description;

    @NotNull(message = "needPoint must not be null")
    private int needPoint;

    @NotNull(message = "startAt must not be null")
    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private ZonedDateTime startAt;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private ZonedDateTime endAt;

    @NotNull(message = "thumbnailImageUrl must not be null")
    private String thumbnailImageUrl;
    private String originalThumbnailImageFile;

    @NotNull(message = "detailImageUrl must not be null")
    private String detailImageUrl;
    private String originalDetailImageFile;

    @NotNull(message = "shareRectangleImageUrl must not be null")
    private String shareRectangleImageUrl;
    private String originalShareRectangleImageFile;

    @NotNull(message = "shareSquareImageUrl must not be null")
    private String shareSquareImageUrl;
    private String originalShareSquareImageFile;

    private String bannerImageUrl;
    private String originalBannerImageFile;

    private EventProductRequest product;
}