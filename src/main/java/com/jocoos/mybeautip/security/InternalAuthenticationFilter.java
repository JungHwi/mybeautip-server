package com.jocoos.mybeautip.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jocoos.mybeautip.config.InternalConfig;
import com.jocoos.mybeautip.global.exception.AuthenticationMemberNotFoundException;
import com.jocoos.mybeautip.global.exception.ErrorResponse;
import com.jocoos.mybeautip.global.util.StringConvertUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import static org.springframework.security.web.authentication.www.BasicAuthenticationConverter.AUTHENTICATION_SCHEME_BASIC;

@Slf4j
@Component
public class InternalAuthenticationFilter extends OncePerRequestFilter {
  private static final String KEY_MEMBER_ID = "MEMBER-ID";
  private static final String PATH_MEMBER_REGISTRATION = "/internal/1/member";

  @Autowired
  private InternalConfig internalConfig;

  @Autowired
  private MybeautipUserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    if (internalConfig.isDebug()) {
      printHeaders(request, response);
    }

    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    String memberId = request.getHeader(KEY_MEMBER_ID);

    if (!StringUtils.startsWithIgnoreCase(header, AUTHENTICATION_SCHEME_BASIC)) {
      responseBody(response, "Authentication scheme is required.");
      return;
    }

    String token = header.substring(6);
    log.debug("token: {}, {}", token, memberId);

    if (!internalConfig.getAccessToken().equals(token)) {
      responseBody(response, "The authentication token is not valid.");
      return;
    }

    log.debug("request uri: {} {}", request.getMethod(), request.getRequestURI());

    if (!PATH_MEMBER_REGISTRATION.equals(request.getRequestURI())) {
      if (!StringUtils.hasText(memberId)) {
        responseBody(response, "Member ID is required.");
        return;
      }

      try {
        setPrincipal(memberId);
      } catch (AuthenticationMemberNotFoundException e) {
        log.error("{}", e.getMessage());
        responseBody(response, "Member is not registered.");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  private void responseBody(HttpServletResponse response, String desc) throws IOException {
    response.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    response.setStatus(HttpStatus.SC_UNAUTHORIZED);

    ErrorResponse errorResponse = ErrorResponse.builder()
        .error("Unauthorized")
        .errorDescription(desc)
        .build();

    response.getOutputStream().print(StringConvertUtil.convertToJson(errorResponse));
    response.flushBuffer();
  }

  private void printHeaders(HttpServletRequest request, HttpServletResponse response) {
    Enumeration names = request.getHeaderNames();
    while (names.hasMoreElements()) {
      String headerName = (String) names.nextElement();
      String headerValue = request.getHeader(headerName);
      log.debug("{}: {}", headerName, headerValue);
    }

    String path = request.getRequestURI();
    String contentType = request.getContentType();
    log.info("Request URL path : {}, Request content type: {}", path, contentType);
  }

  private void setPrincipal(String memberId) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(memberId);
    UsernamePasswordAuthenticationToken internalToken = new UsernamePasswordAuthenticationToken(userDetails, null, null);
    SecurityContextHolder.getContext().setAuthentication(internalToken);
  }
}
