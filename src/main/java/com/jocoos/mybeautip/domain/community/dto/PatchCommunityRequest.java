package com.jocoos.mybeautip.domain.community.dto;

import com.jocoos.mybeautip.global.dto.FileDto;
import lombok.Getter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

@Getter
@SuppressWarnings("FieldMayBeFinal")
public class PatchCommunityRequest {
    private JsonNullable<String> title = JsonNullable.undefined();
    private JsonNullable<String> contents = JsonNullable.undefined();
    private JsonNullable<List<FileDto>> files = JsonNullable.undefined();
}
