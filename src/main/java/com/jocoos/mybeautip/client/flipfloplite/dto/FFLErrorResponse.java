package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.jocoos.mybeautip.client.flipfloplite.code.FFLErrorCode;

public record FFLErrorResponse(FFLErrorCode errorCode,
                               String errorMessage) {
}
