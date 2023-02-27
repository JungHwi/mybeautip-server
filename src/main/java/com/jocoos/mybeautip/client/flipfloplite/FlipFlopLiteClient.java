package com.jocoos.mybeautip.client.flipfloplite;

import com.jocoos.mybeautip.client.flipfloplite.config.FlipFlopLiteClientConfig;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLMemberInfo;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLTokenResponse;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLVideoRoomRequest;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLVideoRoomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "fflClient", url = "${ffl.domain}", configuration = {FlipFlopLiteClientConfig.class})
public interface FlipFlopLiteClient {

    @PostMapping("/v2/apps/me/members/login-as-guest")
    FFLTokenResponse loginGuest();

    @PostMapping("/v2/apps/me/members/login")
    FFLTokenResponse login(FFLMemberInfo memberInfo);

    @PostMapping("/v2/apps/me/video-rooms")
    FFLVideoRoomResponse createVideoRoom(FFLVideoRoomRequest request);

}
