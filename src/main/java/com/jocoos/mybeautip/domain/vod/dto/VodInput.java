package com.jocoos.mybeautip.domain.vod.dto;

public record VodInput(long id,
                       String title,
                       Boolean isVisible,
                       String thumbnail) {
}
