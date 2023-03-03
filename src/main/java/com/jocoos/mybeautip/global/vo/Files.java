package com.jocoos.mybeautip.global.vo;

import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.jocoos.mybeautip.global.code.FileOperationType.UPLOAD;
import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
public class Files {

    private final List<FileVo> fileCollection;

    public Files() {
        this.fileCollection = List.of();
    }

    public String getUploadFilename(String originalFilename) {
        if (isEmpty(fileCollection)) {
            return originalFilename;
        }

        return fileCollection.stream()
                .filter(file -> UPLOAD.equals(file.operation()))
                .findFirst()
                .map(FileVo::filename)
                .orElse(null);
    }

    public boolean isSingleUpload() {
        return fileCollection.size() == 1 && countUploadFile() == 1;
    }

    private long countUploadFile() {
        return fileCollection.stream()
                .filter(file -> UPLOAD.equals(file.operation()))
                .count();
    }
}
