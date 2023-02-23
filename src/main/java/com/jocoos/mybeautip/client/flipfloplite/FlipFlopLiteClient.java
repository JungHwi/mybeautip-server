package com.jocoos.mybeautip.client.flipfloplite;

import com.jocoos.mybeautip.client.flipfloplite.config.FlipFlopLiteClientConfig;
import com.jocoos.mybeautip.client.flipfloplite.dto.FflMemberInfo;
import com.jocoos.mybeautip.client.flipfloplite.dto.TokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "fflClient", url = "${ffl.domain}", configuration = {FlipFlopLiteClientConfig.class})
public interface FlipFlopLiteClient {

    @PostMapping("/v2/apps/me/members/login-as-guest")
    TokenResponse loginGuest();

    @PostMapping("/v2/apps/me/members/login")
    TokenResponse login(FflMemberInfo memberInfo);

}
