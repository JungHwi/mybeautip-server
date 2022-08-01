package com.jocoos.mybeautip.global.config.restdoc.util;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class ApiResponseDto<T> {

    private T data;

    private ApiResponseDto(T data){
        this.data=data;
    }

    public static <T> ApiResponseDto<T> of(T data) {
        return new ApiResponseDto<>(data);
    }
}