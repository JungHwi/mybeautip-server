package com.jocoos.mybeautip.domain.notice.dto;

import com.jocoos.mybeautip.global.dto.FileDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class NoticeResponse {

    private long id;

    private String title;

    private String description;

    private List<FileDto> files;

}
