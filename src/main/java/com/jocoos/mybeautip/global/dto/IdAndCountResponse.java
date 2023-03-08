package com.jocoos.mybeautip.global.dto;

public class IdAndCountResponse {

    public record ReportCountResponse(long id, long reportCount) { }
    public record HeartCountResponse(long id, long heartCount) { }
}
