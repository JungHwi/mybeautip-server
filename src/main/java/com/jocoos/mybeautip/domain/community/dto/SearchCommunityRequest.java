package com.jocoos.mybeautip.domain.community.dto;

import com.jocoos.mybeautip.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SearchCommunityRequest {

    private Member member;

    private Long categoryId;

    private Long eventId;

    private ZonedDateTime cursor;

    private boolean isFirstSearch;

}
