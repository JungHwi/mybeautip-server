package com.jocoos.mybeautip.global.dto;

import com.jocoos.mybeautip.global.code.FileOperationType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileDto {

    private FileOperationType operation;
    private String url;

    public String getUrl() {
        return this.url;
    }
}
