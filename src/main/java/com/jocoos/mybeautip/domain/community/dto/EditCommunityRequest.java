package com.jocoos.mybeautip.domain.community.dto;

import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.member.Member;
import lombok.Data;

import java.util.List;

@Data
public class EditCommunityRequest {
    private Long communityId;

    private String title;

    private String contents;

    private List<FileDto> files;

    private Member member;
}
