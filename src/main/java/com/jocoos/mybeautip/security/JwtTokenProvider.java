package com.jocoos.mybeautip.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import javax.xml.bind.DatatypeConverter;

import com.jocoos.mybeautip.member.Member;


import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtTokenProvider {
  @Value("${security.oauth2.private-key}")
  private String privateKey;

  @Value("${security.oauth2.public-key}")
  private String publicKey;

  @Value("${mybeautip.security.access-token-validity-seconds}")
  private int accessTokenValiditySeconds;

  @Value("${mybeautip.security.refresh-token-validity-seconds}")
  private int refreshTokenValiditySeconds;

  private static final String TYPE_BEARER = "bearer";
  private static final String HEADER_TOKEN_ALG = "alg";
  private static final String HEADER_TOKEN_TYPE = "typ";
  private static final String PAYLOAD_USER_NAME = "user_name";
  private static final String PAYLOAD_SCOPE = "scope";
  private static final String PAYLOAD_JTI = "jti";
  private static final String PAYLOAD_ATI = "ati";
  private static final String PAYLOAD_CLIENT_ID = "client_id";

  public AccessTokenResponse auth(Member member) throws UnsupportedEncodingException {
    Authentication authentication = new UsernamePasswordAuthenticationToken(String.valueOf(member.getId()), null, null);
    return generateToken(authentication);
  }

  public AccessTokenResponse generateToken(Authentication authentication) {
    ArrayList<String> scope = new ArrayList<>(Arrays.asList("read", "write"));

    Map<String, Object> header = new HashMap<>();
    header.put(HEADER_TOKEN_ALG, "RS256");
    header.put(HEADER_TOKEN_TYPE, "JWT");

    long now = new Date().getTime();
    Date accessTokenExpiresIn = new Date(now + (accessTokenValiditySeconds * 1000));
    Claims claims = Jwts.claims();
    String jti = UUID.randomUUID().toString();
    claims.put(PAYLOAD_USER_NAME, authentication.getPrincipal());
    claims.put(PAYLOAD_SCOPE, Arrays.asList("read", "write"));
    claims.put(PAYLOAD_JTI, jti);
    claims.put(PAYLOAD_CLIENT_ID, "mybeautip-web");

    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;
    PrivateKey privateKey = getPrivateKey();

    String accessToken = Jwts.builder()
        .setHeader(header)
        .setExpiration(accessTokenExpiresIn)
        .setSubject(String.valueOf(authentication.getPrincipal()))
        .setClaims(claims)
        .signWith(signatureAlgorithm, privateKey)
        .compact();

    Date refreshTokenExpiration = new Date(now + (refreshTokenValiditySeconds * 1000));
    claims.put(PAYLOAD_JTI, UUID.randomUUID());
    claims.put(PAYLOAD_ATI, jti);
    String refreshToken = Jwts.builder()
        .setHeader(header)
        .setSubject(String.valueOf(authentication.getPrincipal()))
        .setClaims(claims)
        .setExpiration(refreshTokenExpiration)
        .signWith(signatureAlgorithm, privateKey)
        .compact();

    return AccessTokenResponse.builder()
        .tokenType(TYPE_BEARER)
        .accessToken(accessToken)
        .expiresIn(accessTokenExpiresIn.getTime())
        .refreshToken(refreshToken)
        .jti(jti)
        .scope(String.join(" ", scope))
        .build();
  }

  private PrivateKey getPrivateKey() {
    String pkcs8Pem = privateKey;
    pkcs8Pem = pkcs8Pem.replace("-----BEGIN RSA PRIVATE KEY-----", "");
    pkcs8Pem = pkcs8Pem.replace("-----END RSA PRIVATE KEY-----", "");
    pkcs8Pem = pkcs8Pem.replaceAll("\\s+", "");
    // Base64 decode the result

    byte[] pkcs8EncodedBytes = Base64.getDecoder().decode(pkcs8Pem);
    // extract the private key
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
    KeyFactory kf = null;
    try {
      kf = KeyFactory.getInstance("RSA");
      PrivateKey privateKey = kf.generatePrivate(keySpec);
      return privateKey;
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (InvalidKeySpecException e) {
      e.printStackTrace();
    }

    return null;
  }

  public String getMemberId(String token) {
    return getClaimsValue(token, PAYLOAD_USER_NAME);
  }

  private String getClaimsValue(String token, String key) {
    Claims claims = getClaims(token);
    return claims.get(key, String.class);
  }

  public boolean validateToken(String token) {
    try {
      getClaims(token);
      return true;
    } catch (MalformedJwtException ex) {
      log.error("Invalid JWT token");
    } catch (ExpiredJwtException ex) {
      log.error("Expired JWT token");
    } catch (UnsupportedJwtException ex) {
      log.error("Unsupported JWT token");
    } catch (IllegalArgumentException ex) {
      log.error("JWT claims string is empty.");
    }
    return false;
  }

  private Claims getClaims(String token) {
    return Jwts.parser()
        .setSigningKey(getPrivateKey())
        .parseClaimsJws(token)
        .getBody();
  }
}
