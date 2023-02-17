package com.jocoos.mybeautip.domain.broadcast.dto;

public record VodInput(long id,
                       String title,
                       Boolean isVisible,
                       String thumbnail) {
}
