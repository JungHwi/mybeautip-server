package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLCallbackType;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record FFLCallbackRequest(String id, FFLCallbackType type, FFLCallbackData data) {
}
