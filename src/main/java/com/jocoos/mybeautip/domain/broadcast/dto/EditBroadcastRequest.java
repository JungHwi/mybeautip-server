package com.jocoos.mybeautip.domain.broadcast.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@RequiredArgsConstructor
public class EditBroadcastRequest {

    private final JsonNullable<String> title;
    private final JsonNullable<String> notice;
    private final JsonNullable<String> thumbnailUrl;

}
