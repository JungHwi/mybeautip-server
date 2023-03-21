package com.jocoos.mybeautip.global.dto;

public class IdAndBooleanResponse {
    public record CanChatResponse(Long id, boolean canChat) {}
    public record NotificationResponse(Long id, Boolean isNotifyNeeded) {}
}
