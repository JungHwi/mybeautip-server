package com.jocoos.mybeautip.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.util.FileUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

import static com.jocoos.mybeautip.global.code.FileOperationType.DELETE;
import static com.jocoos.mybeautip.global.code.FileOperationType.UPLOAD;
import static org.springframework.util.CollectionUtils.isEmpty;

@Data
@AllArgsConstructor
public class UpdateCommentRequest {
    @NotNull
    @Size(max = 500)
    private String comment;

    private List<FileDto> files;

    @JsonIgnore
    public String getUploadFilename(String originalFileUrl) {
        if (files.size() == 1 && files.get(0).getOperation().equals(DELETE)) {
            return null;
        }

        // web front edit file request is active like PUT, overwrite file of original file
        if (isRequestFileIsOverwriteRequest()) {
            String requestUrl = files.get(0).getUrl();
            if (requestUrl.equals(originalFileUrl)) {
                return originalFileUrl;
            }
            files.get(0).setOperation(UPLOAD);
            if (originalFileUrl != null) {
                files.add(new FileDto(DELETE, originalFileUrl));
            }
        }

        FileDto uploadFile = files.stream()
                .filter(file -> UPLOAD.equals(file.getOperation()))
                .findFirst()
                .orElseThrow();

        return FileUtil.getFileName(uploadFile.getUrl());
    }

    @JsonIgnore
    private boolean isRequestFileIsOverwriteRequest() {
        return !isEmpty(files) && files.size() == 1 && files.get(0).getOperation() == null;
    }
}
