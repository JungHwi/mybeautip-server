package com.jocoos.mybeautip.client.flipfloplite.dto;

public record TokenResponse(String accessToken,
                            String refreshToken,
                            String streamingToken) {
}
