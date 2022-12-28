package com.jocoos.mybeautip.global.dto;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityFile;
import com.jocoos.mybeautip.domain.file.code.FileType;
import com.jocoos.mybeautip.global.code.FileOperationType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileDto {
    private FileOperationType operation;
    private FileType type;
    private String url;

    public FileDto(FileOperationType operation, String url) {
        this.operation = operation;
        this.url = url;
    }

    public static FileDto from(CommunityFile file) {
        return FileDto.builder()
                .type(file.getType())
                .url(file.getFileUrl())
                .build();
    }
}
