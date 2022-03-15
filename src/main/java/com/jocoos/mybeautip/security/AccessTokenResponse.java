package com.jocoos.mybeautip.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenResponse {
  private String accessToken;
  private String tokenType;
  private String refreshToken;
  private Long expiresIn;
  private String scope;
  private String jti;
}
