package com.jocoos.mybeautip.domain.vod.service;

import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteService;
import com.jocoos.mybeautip.client.flipfloplite.dto.ChatTokenAndAppId;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastKey;
import com.jocoos.mybeautip.domain.vod.persistence.domain.Vod;
import com.jocoos.mybeautip.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VodFFLService {

    private final FlipFlopLiteService flipFlopLiteService;

    public BroadcastKey getVodKey(String tokenUsername, Vod vod) {
        ChatTokenAndAppId chatTokenAndAppId = getChatToken(tokenUsername);
        return BroadcastKey.forVod(vod.getChatChannelKey(), chatTokenAndAppId, vod.getChatStartedAt());
    }

    private ChatTokenAndAppId getChatToken(String tokenUsername) {
        if (MemberUtil.isGuest(tokenUsername)) {
            return flipFlopLiteService.getGuestChatToken(tokenUsername);
        }
        return flipFlopLiteService.getChatToken(Long.parseLong(tokenUsername));
    }
}
