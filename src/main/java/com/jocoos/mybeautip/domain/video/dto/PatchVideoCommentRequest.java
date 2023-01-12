package com.jocoos.mybeautip.domain.video.dto;

import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.vo.Files;
import lombok.Getter;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@Getter
public class PatchVideoCommentRequest {

    private JsonNullable<String> contents;
    private List<FileDto> files;

    public Files fileDtoToFiles() {
        if (CollectionUtils.isEmpty(files)) {
            return new Files();
        }
        return files.stream()
                .map(FileDto::toFile)
                .collect(collectingAndThen(toList(), Files::new));
    }
}
