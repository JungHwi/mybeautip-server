package com.jocoos.mybeautip.domain.community.dto;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.member.Member;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WriteCommunityRequest {

    private Long categoryId;

    private Long eventId;

    private String title;

    private String contents;

    private List<FileDto> files;

    private Member member;

    private CommunityCategory category;
}
