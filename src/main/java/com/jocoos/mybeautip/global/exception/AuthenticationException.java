package com.jocoos.mybeautip.global.exception;

import com.jocoos.mybeautip.domain.member.dto.ExceptionMemberResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;

@Getter
public class AuthenticationException extends ClientAuthenticationException {

    private final ErrorCode errorCode;
    private final ExceptionMemberResponse contents;

    public AuthenticationException(String msg) {
        super(msg);
        this.errorCode = null;
        this.contents = null;
    }

    public AuthenticationException(ErrorCode errorCode) {
        this(errorCode, null);
    }

    public AuthenticationException(ErrorCode errorCode, ExceptionMemberResponse contents) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.contents = contents;
    }

    public HttpStatus getHttpStatus() {
        return HttpStatus.valueOf(getHttpErrorCode());
    }

    @Override
    public int getHttpErrorCode() {
        return 401;
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "mybeautip authentication error";
    }
}
