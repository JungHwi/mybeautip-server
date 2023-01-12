package com.jocoos.mybeautip.global.vo;

import com.jocoos.mybeautip.domain.file.code.FileType;
import com.jocoos.mybeautip.global.code.FileOperationType;
import lombok.Builder;

@Builder
public record FileVo(FileOperationType operation,
                     FileType type,
                     String thumbnailUrl,
                     String url) {

}
