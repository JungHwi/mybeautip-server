package com.jocoos.mybeautip.domain.notice.dto;

import com.jocoos.mybeautip.global.dto.FileDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EditNoticeRequest {

    private Long id;

    private String title;

    private String description;

    private List<FileDto> files;
}
