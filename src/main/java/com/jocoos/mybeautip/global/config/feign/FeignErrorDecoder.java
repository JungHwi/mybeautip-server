package com.jocoos.mybeautip.global.config.feign;

import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static java.time.temporal.ChronoField.INSTANT_SECONDS;

@Log4j2
@NoArgsConstructor
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {

        if (isRetry(response)) {
            OffsetDateTime offsetDateTime = OffsetDateTime.now().plusSeconds(5L);
            Date retryAfter = new Date(TimeUnit.SECONDS.toMillis(offsetDateTime.getLong(INSTANT_SECONDS)));
            return new RetryableException(response.status(), format("%s 요청이 성공하지 못했습니다. - status: %s, headers: %s", methodKey, response.status(), response.headers()), response.request().httpMethod(), retryAfter, response.request());
        }

        return new IllegalStateException(format("%s 요청이 성공하지 못했습니다. Retry 합니다. - cause: %s, headers: %s", methodKey, response.status(), response.headers()));
    }

    private boolean isRetry(Response response) {
//        if (response.request().httpMethod().name().equals(HttpMethod.GET.name())) {
//            return HttpStatusClass.SERVER_ERROR.contains(response.status());
//        }
        return false;
    }
}
