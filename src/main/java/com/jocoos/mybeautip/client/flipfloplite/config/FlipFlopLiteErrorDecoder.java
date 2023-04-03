package com.jocoos.mybeautip.client.flipfloplite.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLErrorResponse;
import com.jocoos.mybeautip.client.flipfloplite.exception.FFLException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

@Log4j2
@NoArgsConstructor
public class FlipFlopLiteErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            if (response.request().body() != null) {
                String requestBody = new String(response.request().body(), UTF_8);
                log.debug("request body : {}", requestBody);
            }

            if (response.body() != null) {
                InputStream inputStream = response.body().asInputStream();
                ObjectMapper objectMapper = new ObjectMapper();
                FFLErrorResponse errorResponse = objectMapper.readValue(inputStream, FFLErrorResponse.class);

                return new FFLException(errorResponse.errorCode(), errorResponse.errorMessage());
            }
            return new IllegalStateException(format("%s 요청이 성공하지 못했습니다. Retry 합니다. - cause: %s, headers: %s and error occur reading body", methodKey, response.status(), response.headers()));
        } catch (IOException e) {
            return new IllegalStateException(format("%s 요청이 성공하지 못했습니다. Retry 합니다. - cause: %s, headers: %s and error occur reading body", methodKey, response.status(), response.headers()));
        }
    }
}
