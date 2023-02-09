package com.jocoos.mybeautip.domain.community.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.vo.Files;
import com.jocoos.mybeautip.member.Member;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import java.util.List;

import static com.jocoos.mybeautip.global.code.FileOperationType.DELETE;
import static com.jocoos.mybeautip.global.code.FileOperationType.UPLOAD;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
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

    public Files fileDtoToFiles(String originalFileUrl) {
        if (isEmpty(files)) {
            return new Files();
        }

        // web front edit file request is active like PUT, overwrite file of original file
        if (isRequestFileIsOverwriteRequest()) {
            String requestUrl = files.get(0).getUrl();
            if (requestUrl.equals(originalFileUrl)) {
                return new Files();
            }
            files.get(0).setOperation(UPLOAD);
            if (originalFileUrl != null) {
                files.add(new FileDto(DELETE, originalFileUrl));
            }
        }

        return files.stream()
                .map(FileDto::toFile)
                .collect(collectingAndThen(toList(), Files::new));
    }

    @JsonIgnore
    private boolean isRequestFileIsOverwriteRequest() {
        return !isEmpty(files) && files.size() == 1 && files.get(0).getOperation() == null;
    }

}
