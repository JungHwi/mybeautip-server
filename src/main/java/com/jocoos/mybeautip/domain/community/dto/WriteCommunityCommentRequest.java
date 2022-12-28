package com.jocoos.mybeautip.domain.community.dto;

import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.util.FileUtil;
import com.jocoos.mybeautip.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WriteCommunityCommentRequest {

    private Long categoryId;

    private Long communityId;

    private Long parentId;

    private String contents;

    private Member member;

    private FileDto file;

    private String fileUrl;

    public String getFilename() {
        return file == null ? null : FileUtil.getFileName(file.getUrl());
    }
}
