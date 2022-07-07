package com.jocoos.mybeautip.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jocoos.mybeautip.client.apple.AppleClient;
import com.jocoos.mybeautip.client.apple.dto.ApplePublicKeyResponse;
import com.jocoos.mybeautip.client.apple.dto.AppleTokenRequest;
import com.jocoos.mybeautip.client.apple.dto.AppleTokenResponse;
import com.jocoos.mybeautip.client.apple.dto.RevokeRequest;
import com.jocoos.mybeautip.config.Oauth2Config;
import com.jocoos.mybeautip.global.exception.AuthenticationException;
import com.jocoos.mybeautip.member.AppleMember;
import com.jocoos.mybeautip.member.AppleMemberRepository;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleLoginService implements LoginService {
    public static final String PROVIDER_TYPE = "apple";

    private final AppleMemberRepository repository;
    private final AppleClient appleClient;
    private final Oauth2Config oauth2Config;

    @Override
    public SocialMemberRequest getMember(String authorizationCode, String identityToken) {
        AppleTokenRequest tokenRequest = AppleTokenRequest.builder()
                .code(authorizationCode)
                .client_id(oauth2Config.getApple().getAppBundleId())
                .grant_type(oauth2Config.getApple().getAuthorizationGrantType())
                .client_secret(makeClientSecret())
                .build();

        Claims Claims = getClaimsBy(identityToken);
        AppleTokenResponse tokenResponse = appleClient.getToken(tokenRequest);
        String refreshToken = tokenResponse.getRefreshToken();
        if (StringUtils.isBlank(refreshToken)) {
            throw new AuthenticationException("Refresh token required");
        }

        return toSocialMember(Claims, refreshToken);
    }

    public void revoke(long memberId) {
        AppleMember appleMember = repository.findByMemberId(memberId)
                .orElseGet(null);

        revoke(appleMember);
    }

    public void revoke(AppleMember appleMember) {
        if (appleMember == null) {
            return;
        }
        RevokeRequest revokeRequest = RevokeRequest.builder()
                .client_id(oauth2Config.getApple().getAppBundleId())
                .client_secret(makeClientSecret())
                .token(appleMember.getRefreshToken())
                .token_type_hint("refresh_token")
                .build();

        appleClient.revoke(revokeRequest);
        appleMember.revoke();
    }


    @SuppressWarnings("unchecked")
    private SocialMemberRequest toSocialMember(Claims claims, String refreshToken) {
        return SocialMemberRequest.builder()
                .id(claims.get("sub", String.class))
                .email(claims.get("email", String.class))
                .provider(PROVIDER_TYPE)
                .refreshToken(refreshToken)
                .build();
    }

    private Claims getClaimsBy(String identityToken) {
        try {
            ApplePublicKeyResponse response = appleClient.getAppleAuthPublicKey();

            String headerOfIdentityToken = identityToken.substring(0, identityToken.indexOf("."));
            Map<String, String> header = new ObjectMapper().readValue(new String(Base64.getDecoder().decode(headerOfIdentityToken), "UTF-8"), Map.class);
            ApplePublicKeyResponse.Key key = response.getMatchedKeyBy(header.get("kid"), header.get("alg"))
                    .orElseThrow(() -> new NullPointerException("Failed get public key from apple's id server."));

            byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
            byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());

            BigInteger n = new BigInteger(1, nBytes);
            BigInteger e = new BigInteger(1, eBytes);

            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance(key.getKty());
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(identityToken).getBody();

        } catch (NoSuchAlgorithmException e) {
            log.error("{}", e);
        } catch (InvalidKeySpecException e) {
            log.error("{}", e);
        } catch (MalformedJwtException e) {
            //토큰 서명 검증 or 구조 문제 (Invalid token)
            log.error("{}", e);
        } catch (ExpiredJwtException e) {
            //토큰이 만료됐기 때문에 클라이언트는 토큰을 refresh 해야함.
            log.error("{}", e);
        } catch (Exception e) {
            log.error("{}", e);
        }
        return null;
    }

    private String makeClientSecret() {
        Date expirationDate = Date.from(LocalDateTime.now().plusDays(180).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setHeaderParam("kid", oauth2Config.getApple().getClientId())
                .setHeaderParam("alg", "ES256")
                .setIssuer(oauth2Config.getApple().getTeamId())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationDate)
                .setAudience(oauth2Config.getApple().getTokenUri())
                .setSubject(oauth2Config.getApple().getAppBundleId())
                .signWith(SignatureAlgorithm.ES256, getPrivateKey())
                .compact();
    }

    private PrivateKey getPrivateKey() {
        try {
            String privateKey = oauth2Config.getApple().getClientSecret();
            Reader pemReader = new StringReader(privateKey);

            PEMParser pemParser = new PEMParser(pemReader);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

            PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
            return converter.getPrivateKey(object);
        } catch (IOException e) {
            return null;
        }
    }
}
