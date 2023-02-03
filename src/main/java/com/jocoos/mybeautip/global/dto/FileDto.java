package com.jocoos.mybeautip.global.dto;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityFile;
import com.jocoos.mybeautip.domain.file.code.FileType;
import com.jocoos.mybeautip.global.code.FileOperationType;
import com.jocoos.mybeautip.global.vo.FileVo;
import lombok.*;

import static com.jocoos.mybeautip.domain.file.code.FileType.IMAGE;
import static com.jocoos.mybeautip.domain.file.code.FileType.VIDEO;
import static com.jocoos.mybeautip.global.util.FileUtil.getFileName;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileDto {

    private FileOperationType operation;
    private FileType type = IMAGE;
    private String thumbnailUrl;
    private String url;
    private boolean needTranscode = false;

    public FileDto(FileOperationType operation, String url) {
        this.operation = operation;
        this.url = url;
    }

    public FileDto(FileType type, String url) {
        this.type = type;
        this.url = url;
        if (VIDEO.equals(type)) {
            this.thumbnailUrl = thumbnailUrl();
        }
    }

    public static FileDto from(CommunityFile file) {
        return new FileDto(file.getType(), file.getFileUrl());
    }

    public FileVo toFile() {
        return FileVo.builder()
                .operation(operation)
                .type(type)
                .thumbnailUrl(thumbnailUrl)
                .url(url)
                .build();
    }

    public boolean containThumbnail() {
        return thumbnailUrl != null;
    }

    public String filename() {
        return getFileName(url);
    }

    public String requestThumbnailFilename() {
        return getFileName(thumbnailUrl);
    }

    public String thumbnailFilename() {
        return getFileName(thumbnailUrl());
    }

    private String thumbnailUrl() {
        return url + "_thumbnail.jpg";
    }

}
