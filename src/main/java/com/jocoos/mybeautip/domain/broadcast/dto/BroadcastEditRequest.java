package com.jocoos.mybeautip.domain.broadcast.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.global.dto.FileDto;
import io.jsonwebtoken.lang.Collections;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
@RequiredArgsConstructor
public class BroadcastEditRequest {

    @NotNull
    private final String title;

    private final List<FileDto> thumbnails;

    @NotNull
    private final Long categoryId;

    @NotNull
    private final Boolean isStartNow;

    private final String notice;

    @NotNull
    private final Boolean isSoundOn;

    @NotNull
    private final Boolean isScreenShow;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private final ZonedDateTime startedAt;

    @JsonIgnore
    @AssertTrue(message = "Broadcast request requires is_start_now is true or started_at is not null")
    public boolean isStartNowTrueOrStartedAtNotNull() {
        return Boolean.TRUE.equals(isStartNow) || startedAt != null;
    }

    @JsonIgnore
    @AssertTrue(message = "Thumbnail should keep single. so request must be empty or contain one upload, one delete operation each")
    public boolean isKeepSingleFile() {
        if (Collections.isEmpty(thumbnails)) return true;
        if (thumbnails.size() != 2) return false;
        return findUploadFileStream().count() == 1;
    }

    @JsonIgnore
    public String getUploadThumbnailUrl(String originalThumbnailUrl) {
        if (Collections.isEmpty(thumbnails)) {
            return originalThumbnailUrl;
        }
        return findUploadFileStream()
                .map(FileDto::getUrl)
                .findAny()
                .orElse(originalThumbnailUrl);
    }

    private Stream<FileDto> findUploadFileStream() {
        return thumbnails.stream().filter(FileDto::isUpload);
    }
}
