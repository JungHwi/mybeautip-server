package com.jocoos.mybeautip.domain.community.dto;

import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.util.FileUtil;
import com.jocoos.mybeautip.member.Member;
import lombok.Builder;
import lombok.Data;

import java.util.List;

import static com.jocoos.mybeautip.global.code.FileOperationType.DELETE;
import static com.jocoos.mybeautip.global.code.FileOperationType.UPLOAD;


@Data
@Builder
public class EditCommunityCommentRequest {

    private Long communityId;

    private Long commentId;

    private String contents;

    private List<FileDto> files;

    private Member member;

    public String getUploadFilename() {

        if (files.size() == 1 && files.get(0).getOperation().equals(DELETE)) {
            return null;
        }

        FileDto uploadFile = files.stream()
                .filter(file -> UPLOAD.equals(file.getOperation()))
                .findFirst()
                .orElseThrow();

        return FileUtil.getFileName(uploadFile.getUrl());
    }
}
