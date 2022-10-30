package com.jocoos.mybeautip.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class MemberStatusResponse {

    @JsonIgnore
    private static final String ALL = "전체";
    private final MemberStatus status;
    private final String statusName;
    private final long count;

    public MemberStatusResponse(String statusName, long count) {
        this.status = null;
        this.statusName = statusName;
        this.count = count;
    }

    public static List<MemberStatusResponse> from(Map<MemberStatus, Long> statusCountMap) {
        List<MemberStatusResponse> response = new ArrayList<>();
        long totalCount = statusCountMap.values().stream().mapToLong(Long::valueOf).sum();
        response.add(new MemberStatusResponse(ALL, totalCount));
        response.addAll(Arrays.stream(MemberStatus.values())
                .map(memberStatus -> new MemberStatusResponse(memberStatus, memberStatus.getName(), statusCountMap.getOrDefault(memberStatus, 0L)))
                .toList());
        return response;
    }
}
