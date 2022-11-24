package com.jocoos.mybeautip.domain.report.service.dao;

import com.jocoos.mybeautip.domain.member.dto.AdminMemberReportResponse;
import com.jocoos.mybeautip.domain.member.dto.MemberIdAndUsernameResponse;
import com.jocoos.mybeautip.domain.member.persistence.repository.NativeContentReportRepository;
import com.jocoos.mybeautip.global.util.date.ZonedDateTimeUtil;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ContentReportDao {

    private final NativeContentReportRepository repository;

    @Transactional(readOnly = true)
    public PageResponse<AdminMemberReportResponse> getAllAccusedBy(Long accusedId, Pageable pageable) {
        Page<Map<String, Object>> page = repository.unionAllContentReport(accusedId, pageable);

        List<AdminMemberReportResponse> response = page.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return new PageResponse<>(page.getTotalElements(), response);
    }

    @Transactional(readOnly = true)
    public Map<Long, Integer> getAllReportCountMap(List<Long> accusedIds) {
        List<Map<String, Object>> maps = repository.unionAllCountContentReport(accusedIds);
        Map<Long, Integer> idCountMap = new HashMap<>();
        maps.forEach(map -> idCountMap.put(getLongValue(map, "id"), getIntegerValue(map, "count")));
        return idCountMap;
    }

    private AdminMemberReportResponse toResponse(Map<String, Object> map) {
        return new AdminMemberReportResponse(
                getId(map),
                getAccuserResponse(map),
                getReason(map),
                getReportedAt(map));
    }

    private String getId(Map<String, Object> map) {
        return getStringValue(map, "type") + map.get("id");
    }

    private MemberIdAndUsernameResponse getAccuserResponse(Map<String, Object> map) {
        return new MemberIdAndUsernameResponse(getAccuserId(map), getStringValue(map, "accuserUsername"));
    }

    private long getAccuserId(Map<String, Object> map) {
        return Long.parseLong(getStringValue(map, "accuserId"));
    }

    private String getReason(Map<String, Object> map) {
        return getStringValue(map, "reason");
    }

    private ZonedDateTime getReportedAt(Map<String, Object> map) {
        return ZonedDateTimeUtil.toUTCZonedDateTimeFormat(getStringValue(map, "createdAt").split("\\.")[0]);
    }

    private Long getLongValue(Map<String, Object> map, String id) {
        return Long.valueOf(getStringValue(map, id));
    }

    private Integer getIntegerValue(Map<String, Object> map, String id) {
        return Integer.valueOf(getStringValue(map, id));
    }

    private String getStringValue(Map<String, Object> map, String key) {
        return String.valueOf(map.get(key));
    }
}
