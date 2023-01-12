package com.jocoos.mybeautip.domain.video.dto;

import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.util.FileUtil;

public record WriteVideoCommentRequest(String contents,
                                       FileDto file,
                                       Long parentId) {

    public String getFilename() {
        return file == null ? null : FileUtil.getFileName(file.getUrl());
    }
}
