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

    public MemberStatusResponse(MemberStatus status, long count) {
        this.status = status;
        this.statusName = status.getDescription();
        this.count = count;
    }

    public MemberStatusResponse(String statusName, long count) {
        this.status = null;
        this.statusName = statusName;
        this.count = count;
    }

    public static List<MemberStatusResponse> from(Map<MemberStatus, Long> statusCountMap) {
        List<MemberStatusResponse> response = new ArrayList<>();
        long totalCount = getTotalCount(statusCountMap);
        response.add(getTotalStatus(totalCount));
        response.addAll(getIndividualStatus(statusCountMap));
        return response;
    }

    private static long getTotalCount(Map<MemberStatus, Long> joinCountMap) {
        return joinCountMap.values().stream()
                .mapToLong(Long::valueOf)
                .sum();
    }

    private static MemberStatusResponse getTotalStatus(long totalCount) {
        return new MemberStatusResponse(ALL, totalCount);
    }

    private static List<MemberStatusResponse> getIndividualStatus(final Map<MemberStatus, Long> joinCountMap) {
        return Arrays.stream(MemberStatus.values())
                .map(memberStatus -> toResponse(joinCountMap, memberStatus))
                .toList();
    }

    private static MemberStatusResponse toResponse(Map<MemberStatus, Long> joinCountMap, MemberStatus memberStatus) {
        return new MemberStatusResponse(memberStatus, getCount(joinCountMap, memberStatus));
    }

    private static Long getCount(Map<MemberStatus, Long> joinCountMap, MemberStatus memberStatus) {
        return joinCountMap.getOrDefault(memberStatus, 0L);
    }
}
