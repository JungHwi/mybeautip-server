package com.jocoos.mybeautip.security;

import com.jocoos.mybeautip.domain.member.persistence.domain.Jwt;
import com.jocoos.mybeautip.domain.member.service.dao.JwtDao;
import com.jocoos.mybeautip.member.Member;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

@Slf4j
@Service
public class JwtTokenProvider {
    private static final String TYPE_BEARER = "bearer";
    private static final String HEADER_TOKEN_ALG = "alg";
    private static final String HEADER_TOKEN_TYPE = "typ";
    private static final String PAYLOAD_USER_NAME = "user_name";
    private static final String PAYLOAD_SCOPE = "scope";
    private static final String PAYLOAD_JTI = "jti";
    private static final String PAYLOAD_ATI = "ati";
    private static final String PAYLOAD_CLIENT_ID = "client_id";
    @Value("${security.oauth2.private-key}")
    private String privateKey;
    @Value("${security.oauth2.public-key}")
    private String publicKey;
    @Value("${mybeautip.security.access-token-validity-seconds}")
    private int accessTokenValiditySeconds;
    @Value("${mybeautip.security.refresh-token-validity-seconds}")
    private int refreshTokenValiditySeconds;

    @Autowired
    private JwtDao jwtDao;

    public AccessTokenResponse auth(Member member) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(String.valueOf(member.getId()), null, null);
        return generateToken(authentication);
    }

    public AccessTokenResponse generateToken(Authentication authentication) {
        ArrayList<String> scope = new ArrayList<>(Arrays.asList("read", "write"));

        Map<String, Object> header = new HashMap<>();
        header.put(HEADER_TOKEN_ALG, "RS256");
        header.put(HEADER_TOKEN_TYPE, "JWT");


        long now = new Date().getTime();
        Date accessTokenExpiresIn = new Date(now + (accessTokenValiditySeconds * 1000L));
        Claims claims = Jwts.claims();
        String jti = UUID.randomUUID().toString();
        claims.put(PAYLOAD_USER_NAME, authentication.getPrincipal());
        claims.put(PAYLOAD_SCOPE, Arrays.asList("read", "write"));
        claims.put(PAYLOAD_JTI, jti);
        claims.put(PAYLOAD_CLIENT_ID, "mybeautip-mobile");

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;
        PrivateKey privateKey = getPrivateKey();

        String accessToken = Jwts.builder()
                .setHeader(header)
                .setClaims(claims)
                .setExpiration(accessTokenExpiresIn)
                .setSubject(String.valueOf(authentication.getPrincipal()))
                .signWith(signatureAlgorithm, privateKey)
                .compact();

        Date refreshTokenExpiration = new Date(now + (refreshTokenValiditySeconds * 1000L));
        claims.put(PAYLOAD_JTI, UUID.randomUUID());
        claims.put(PAYLOAD_ATI, jti);
        String refreshToken = Jwts.builder()
                .setHeader(header)
                .setClaims(claims)
                .setSubject(String.valueOf(authentication.getPrincipal()))
                .setExpiration(refreshTokenExpiration)
                .signWith(signatureAlgorithm, privateKey)
                .compact();

        String username = getMemberId(refreshToken);
        registerRefreshToken(username, refreshToken);

        return AccessTokenResponse.builder()
                .tokenType(TYPE_BEARER)
                .accessToken(accessToken)
                .expiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .jti(jti)
                .scope(String.join(" ", scope))
                .build();
    }

    @Transactional()
    public Jwt registerRefreshToken(String username, String refreshToken) {
        return jwtDao.registerRefreshToken(username, refreshToken, refreshTokenValiditySeconds);
    }

    @Transactional(readOnly = true)
    public boolean validRefreshToken(String username, String refreshToken) {
        Jwt jwt = jwtDao.get(username);
        return jwt.valid(refreshToken);
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
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.debug("", e);
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
