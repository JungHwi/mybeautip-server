package com.jocoos.mybeautip.domain.community.dto;

import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.member.Member;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class EditCommunityRequest {
    private Long communityId;

    private String title;

    private String contents;

    private List<FileDto> files;

    private Member member;

    public boolean containTranscodeRequest() {
        return Optional.ofNullable(files)
                .orElse(List.of())
                .stream()
                .anyMatch(FileDto::isNeedTranscode);
    }
}
