package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLChangeStatusType;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record FFLBroadcastChangeStatusRequest(String id, FFLChangeStatusType type, FFLChangeStatusData data) {
}
