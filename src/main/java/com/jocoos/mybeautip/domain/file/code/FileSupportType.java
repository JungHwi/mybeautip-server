package com.jocoos.mybeautip.domain.file.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static org.apache.http.entity.ContentType.*;

@Getter
@RequiredArgsConstructor
public enum FileSupportType {
    JPEG(IMAGE_JPEG.getMimeType()),
    PNG(IMAGE_PNG.getMimeType()),
    GIF(IMAGE_GIF.getMimeType()),
    JPG("image/jpg"),
    MP4("video/mp4"),
    MOV("video/mov"),
    AVI("video/avi")
    ;

    private final String contentType;

    public static boolean isSupport(String contentType) {
        return Arrays.stream(values())
                .anyMatch(value -> value.contentType.equals(contentType));
    }
}
