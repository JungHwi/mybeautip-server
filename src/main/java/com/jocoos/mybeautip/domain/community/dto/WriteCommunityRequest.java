package com.jocoos.mybeautip.domain.community.dto;

import com.jocoos.mybeautip.global.dto.FileDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WriteCommunityRequest {

    private Long categoryId;

    private Long eventId;

    private String title;

    private String contents;

    private List<FileDto> files;
}
