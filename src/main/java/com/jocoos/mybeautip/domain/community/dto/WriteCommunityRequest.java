package com.jocoos.mybeautip.domain.community.dto;

import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.member.Member;
import lombok.Builder;
import lombok.Data;

import java.util.List;

import static com.jocoos.mybeautip.global.code.FileOperationType.UPLOAD;
import static org.springframework.util.CollectionUtils.isEmpty;

@Data
@Builder
public class WriteCommunityRequest {

    private CommunityStatus status;

    private Long categoryId;

    private Long eventId;

    private String title;

    private String contents;
    private List<FileDto> files;
    private Member member;

    private CommunityCategory category;

    public void setFileOperationToUpload() {
        if (!isEmpty(files)) {
            files.forEach(file -> file.setOperation(UPLOAD));
        }
    }

}
