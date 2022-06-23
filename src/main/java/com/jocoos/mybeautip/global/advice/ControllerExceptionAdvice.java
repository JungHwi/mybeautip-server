package com.jocoos.mybeautip.global.advice;

import com.jocoos.mybeautip.exception.*;
import com.jocoos.mybeautip.global.exception.MybeautipException;
import com.jocoos.mybeautip.global.exception.UnauthorizedException;
import com.jocoos.mybeautip.global.wrapper.ResultResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.ServletException;
import javax.validation.ValidationException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.global.constant.ErrorCodeConstant.DEFAULT_ERROR_CODE;

@Log4j2
@RestControllerAdvice
public class ControllerExceptionAdvice {

    private Environment environment;

    public ControllerExceptionAdvice(Environment environment){
        this.environment = environment;
    }

    @ExceptionHandler({UnauthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResultResponse handleException(UnauthorizedException e) {
        return ResultResponse.builder()
                .code(DEFAULT_ERROR_CODE)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResultResponse handleException(AccessDeniedException e) {
        return ResultResponse.builder()
                .code(DEFAULT_ERROR_CODE)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(MybeautipException.class)
    public ResponseEntity<ResultResponse> handleException(MybeautipException e) {
        ResultResponse resultResponse = ResultResponse.builder()
                .code(e.getCode())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(resultResponse, e.getHttpStatus());
    }

    @ExceptionHandler({ValidationException.class, TypeMismatchException.class,
            ServletException.class, HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultResponse handleValidateException(Exception e) {
        return ResultResponse.builder()
                .code(40099)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errorMessages = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> String.format("%s : %s", fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());

        return ResultResponse.builder()
                .code(DEFAULT_ERROR_CODE)
                .message(StringUtils.join(errorMessages,  ", "))
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultResponse handleException(Exception e) {
        for (StackTraceElement stack : e.getStackTrace()) {
            log.warn("#### {}", stack);
        }

        return ResultResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(isProduction() ? "Server Error" : e.getMessage())
                .build();
    }

    // legacy
    @ExceptionHandler(ConflictException.class)
    public final ResponseEntity<ErrorResponse> handleConflictException(ConflictException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), e.getDescription());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({com.jocoos.mybeautip.exception.NotFoundException.class})
    public final ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), e.getDescription());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), e.getDescription());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public final ResponseEntity<ErrorResponse> handleMemberNotFoundException(MemberNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), e.getDescription());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Object> handleNumberFormatException(NumberFormatException e) {
        log.debug("handleNumberFormatException called");
        ErrorResponse errorResponse = new ErrorResponse("bad_request",
                e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private boolean isProduction() {
        if (environment == null) {
            return false;
        }
        return Arrays.asList(environment.getActiveProfiles()).contains("production");
    }
}
