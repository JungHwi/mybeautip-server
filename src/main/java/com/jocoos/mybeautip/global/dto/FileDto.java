package com.jocoos.mybeautip.global.dto;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityFile;
import com.jocoos.mybeautip.domain.file.code.FileType;
import com.jocoos.mybeautip.global.code.FileOperationType;
import com.jocoos.mybeautip.global.vo.FileVo;
import lombok.*;
import org.springframework.util.StringUtils;

import java.util.Objects;

import static com.jocoos.mybeautip.domain.file.code.FileType.IMAGE;
import static com.jocoos.mybeautip.domain.file.code.FileType.VIDEO;
import static com.jocoos.mybeautip.global.util.FileUtil.getFileName;
import static org.springframework.util.StringUtils.hasText;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileDto {

    private FileOperationType operation;
    private FileType type = IMAGE;
    private String thumbnailUrl;
    private Integer duration;
    private String url;
    private boolean needTranscode = false;

    public FileDto(FileOperationType operation, String url) {
        this.operation = operation;
        this.url = url;
    }

    public FileDto(FileType type, String url, Integer duration) {
        this.type = type;
        this.url = url;
        if (VIDEO.equals(type)) {
            this.thumbnailUrl = thumbnailUrl();
            this.duration = duration;
        }
    }

    public static FileDto from(CommunityFile file) {
        return new FileDto(file.getType(), file.getFileUrl(), file.getDuration());
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
        return hasText(thumbnailUrl);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileDto fileDto = (FileDto) o;
        return needTranscode == fileDto.needTranscode &&
                operation == fileDto.operation &&
                type == fileDto.type &&
                Objects.equals(thumbnailUrl, fileDto.thumbnailUrl) &&
                Objects.equals(url, fileDto.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operation, type, thumbnailUrl, url, needTranscode);
    }
}
