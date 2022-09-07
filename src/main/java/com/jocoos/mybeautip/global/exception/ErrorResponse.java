package com.jocoos.mybeautip.global.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

    private String error;
    private String errorDescription;
}
