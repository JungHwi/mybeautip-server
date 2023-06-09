package com.jocoos.mybeautip.domain.notice.dto;

import com.jocoos.mybeautip.global.dto.FileDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class WriteNoticeRequest {

    private Boolean isVisible;

    private String title;

    private String description;

    private List<FileDto> files;
}
