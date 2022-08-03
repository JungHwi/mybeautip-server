package com.jocoos.mybeautip.domain.community.dto;

import com.jocoos.mybeautip.global.dto.FileDto;
import lombok.Data;

import java.util.List;

@Data
public class EditCommunityRequest {

    private String title;

    private String contents;

    private List<FileDto> files;
}
