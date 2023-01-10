package com.jocoos.mybeautip.domain.notice.dto;

import com.jocoos.mybeautip.global.dto.FileDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditNoticeRequest {

    private Long id;

    private Boolean isVisible;

    private Boolean isImportant;

    private String title;

    private String description;

    private List<FileDto> files;
}
