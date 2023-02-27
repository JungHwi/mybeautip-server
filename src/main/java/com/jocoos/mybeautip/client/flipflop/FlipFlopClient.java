package com.jocoos.mybeautip.client.flipflop;

import com.jocoos.mybeautip.global.config.feign.CommonFeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@FeignClient(name = "flipFlopClient", url = "${flipflop.url}", configuration = CommonFeignClientConfig.class)
public interface FlipFlopClient {

    @PostMapping(value = "/admin/2/applications/${flipflop.app-key}/videos", consumes = MULTIPART_FORM_DATA_VALUE)
    void uploadVideo(@RequestHeader(AUTHORIZATION) String accessToken,
                     @RequestPart("url") String url,
                     @RequestPart("data") String data);
}
