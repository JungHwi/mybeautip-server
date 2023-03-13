package com.jocoos.mybeautip.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InternalAuthenticationFilter extends OncePerRequestFilter {
  private static final String KEY_MEMBER_ID = "MEMBER-ID";

  @Autowired
  private InternalConfig internalConfig;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    if (internalConfig.isDebug()) {
      printHeaders(request, response);
    }

    String token = request.getHeader(HttpHeaders.AUTHORIZATION);
    String memberId = request.getHeader(KEY_MEMBER_ID);
    log.debug("token: {}, {}", token, memberId);

    if (StringUtils.hasText(memberId)) {
      setPrincipal(memberId);
    }

    if (!internalConfig.getAccessToken().equals(token)) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token is not valid.");
    } else {
      filterChain.doFilter(request, response);
    }
  }

  public void printHeaders(HttpServletRequest request, HttpServletResponse response) {
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
    MyBeautipUserDetails internalUser = new MyBeautipUserDetails(memberId, "ROLE_INTERNAL");
    UsernamePasswordAuthenticationToken internalToken = new UsernamePasswordAuthenticationToken(internalUser, null, null);
    SecurityContextHolder.getContext().setAuthentication(internalToken);
  }
}
