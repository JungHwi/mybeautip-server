package com.jocoos.mybeautip.global.vo;

import com.jocoos.mybeautip.domain.file.code.FileType;
import com.jocoos.mybeautip.global.code.FileOperationType;
import lombok.Builder;

import static com.jocoos.mybeautip.global.util.FileUtil.getFileName;

@Builder
public record FileVo(FileOperationType operation,
                     FileType type,
                     String thumbnailUrl,
                     String url) {

    public String filename() {
        return getFileName(url);
    }
}
