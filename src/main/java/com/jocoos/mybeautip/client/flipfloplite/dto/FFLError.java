package com.jocoos.mybeautip.client.flipfloplite.dto;

import java.time.ZonedDateTime;

public record FFLError(String code,
                       String message,
                       ZonedDateTime occurredAt) {
}
