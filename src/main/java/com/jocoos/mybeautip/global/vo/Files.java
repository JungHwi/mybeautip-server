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

        // for comment file PUT request
        if (isSingleFile() && fileCollection.get(0).operation() == null) {
            FileVo singleFile = fileCollection.get(0);
            if (singleFile.filename().equals(originalFilename)) {
                return originalFilename;
            }
            return singleFile.filename();
        }

        return fileCollection.stream()
                .filter(file -> UPLOAD.equals(file.operation()))
                .findFirst()
                .map(FileVo::filename)
                .orElse(null);
    }

    public boolean isSingleUpload() {
        return isSingleFile() && countUploadFile() == 1;
    }

    private boolean isSingleFile() {
        return fileCollection.size() == 1;
    }

    private long countUploadFile() {
        return fileCollection.stream()
                .filter(file -> UPLOAD.equals(file.operation()))
                .count();
    }
}
