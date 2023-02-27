package com.jocoos.mybeautip.client.apple;

import com.jocoos.mybeautip.client.apple.dto.ApplePublicKeyResponse;
import com.jocoos.mybeautip.client.apple.dto.AppleTokenRequest;
import com.jocoos.mybeautip.client.apple.dto.AppleTokenResponse;
import com.jocoos.mybeautip.client.apple.dto.RevokeRequest;
import com.jocoos.mybeautip.global.config.feign.CommonFeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "appleClient", url = "https://appleid.apple.com", configuration = CommonFeignClientConfig.class)
public interface AppleClient {

    @GetMapping(value = "/auth/keys")
    ApplePublicKeyResponse getAppleAuthPublicKey();

    @PostMapping(value = "/auth/token", consumes = "application/x-www-form-urlencoded")
    AppleTokenResponse getToken(AppleTokenRequest request);

    @PostMapping(value = "/auth/revoke", consumes = "application/x-www-form-urlencoded")
    ResponseEntity revoke(RevokeRequest request);
}
