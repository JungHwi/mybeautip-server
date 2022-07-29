package com.jocoos.mybeautip.domain.term.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberTermRequest {

    private long termId;

    public static List<Long> getTermIds(List<MemberTermRequest> requests) {
        return requests.stream()
                .map(MemberTermRequest::getTermId)
                .collect(Collectors.toList());
    }
}
