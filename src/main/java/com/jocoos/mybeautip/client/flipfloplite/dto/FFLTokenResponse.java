package com.jocoos.mybeautip.client.flipfloplite.dto;

public record FFLTokenResponse(String accessToken,
                               String refreshToken,
                               String streamingToken) {
}
