package com.jocoos.mybeautip.domain.community.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.util.FileUtil;
import com.jocoos.mybeautip.member.Member;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import java.util.List;

import static com.jocoos.mybeautip.global.code.FileOperationType.DELETE;
import static com.jocoos.mybeautip.global.code.FileOperationType.UPLOAD;
import static org.springframework.util.CollectionUtils.isEmpty;


@Data
@Builder
public class EditCommunityCommentRequest {

    private Long communityId;

    private Long commentId;

    private String contents;

    private List<FileDto> files;

    private Member member;

    @JsonIgnore
    public String getUploadFilename() {
        return files.stream()
                .filter(file -> UPLOAD.equals(file.getOperation()))
                .findFirst()
                .map(file -> FileUtil.getFileName(file.getUrl()))
                .orElse(null);
    }

    @JsonIgnore
    @AssertTrue(message = "files can only have one upload, one delete each")
    public boolean isFileSizeUnderLimit() {
        if (isEmpty(files)) {
            return true;
        }
        return files.size() <= 2 && countUploadFile() <= 1 && countDeleteFile() <= 1;
    }

    private long countUploadFile() {
        return files.stream()
                .filter(file -> UPLOAD.equals(file.getOperation()))
                .count();
    }

    private long countDeleteFile() {
        return files.stream()
                .filter(file -> DELETE.equals(file.getOperation()))
                .count();
    }

    @JsonIgnore
    public boolean isOnlyUpload() {
        if (isEmpty(files)) {
            return false;
        }
        return files.size() == 1 && countUploadFile() == 1;
    }
}
