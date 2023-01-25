package com.jocoos.mybeautip.domain.video.dto;

import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.util.FileUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WriteVideoCommentRequest {

    private final String contents;
    private final FileDto file;
    private final Long parentId;

    public String filename() {
        return file == null ? null : FileUtil.getFileName(file.getUrl());
    }
}
