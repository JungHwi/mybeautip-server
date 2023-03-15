package com.jocoos.mybeautip.client.flipfloplite.filter;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Slf4j
@Component
public class FFLCallbackFilter extends OncePerRequestFilter {

    private final String fflBasicAuthorizationHeader;

    public FFLCallbackFilter(@Value("${ffl.callback.basic-auth}") String fflBasicAuthorizationHeader) {
        this.fflBasicAuthorizationHeader = fflBasicAuthorizationHeader;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        log.debug("FFLCallbackFilter : {}", request.getRequestURI());
        String requestHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!fflBasicAuthorizationHeader.equals(requestHeader)) {
            response.sendError(SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
