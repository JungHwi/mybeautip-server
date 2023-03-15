package com.jocoos.mybeautip.domain.broadcast.api.front;


import com.jocoos.mybeautip.domain.broadcast.service.BroadcastChatService;
import com.jocoos.mybeautip.global.annotation.CurrentMember;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class BroadcastChatController {

    private final BroadcastChatService service;

    @PatchMapping("/1/broadcast/{broadcastId}/chat/status")
    public void changeChatStatus(@PathVariable Long broadcastId,
                                 @CurrentMember MyBeautipUserDetails userDetails,
                                 BooleanDto canChatRequest) {
        service.sendChangeChatStatus(broadcastId, userDetails.getMember().getId(), canChatRequest.isBool());
    }

    @PatchMapping("/1/broadcast/{broadcastId}/chat/{chatId}/pin")
    public void pinChat(@PathVariable Long broadcastId, @PathVariable Long chatId) {
        service.pinChat(broadcastId, chatId);
    }
}
