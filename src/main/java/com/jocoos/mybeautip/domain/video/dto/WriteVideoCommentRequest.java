package com.jocoos.mybeautip.domain.video.dto;

import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.util.FileUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
public class WriteVideoCommentRequest {

    @NotBlank
    private final String contents;
    private final FileDto file;
    private final Long parentId;

    public String getFilename() {
        return file == null ? null : FileUtil.getFileName(file.getUrl());
    }
}
