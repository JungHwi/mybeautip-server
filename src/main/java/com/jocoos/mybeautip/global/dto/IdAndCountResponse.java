package com.jocoos.mybeautip.global.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public class IdAndCountResponse {

    public record ReportCountResponse(long id, long reportCount) { }
    public record HeartCountResponse(long id, long heartCount) { }


    @JsonIgnore
    private final Map<String, Long> nameCountMap;

    private IdAndCountResponse(String name, long count) {
        this.nameCountMap = Map.of(name, count);
    }

    public static IdAndCountResponse nameReportCount(long count) {
        return new IdAndCountResponse("reportCount", count);
    }

    public static IdAndCountResponse nameHeartCount(long count) {
        return new IdAndCountResponse("heartCount", count);
    }

    @JsonAnyGetter
    public Map<String, Object> getCount() {
        return Map.copyOf(nameCountMap);
    }
}
