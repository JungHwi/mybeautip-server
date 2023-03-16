package com.jocoos.mybeautip.domain.broadcast.api.front;


import com.jocoos.mybeautip.domain.broadcast.service.BroadcastMessageService;
import com.jocoos.mybeautip.global.annotation.CurrentMember;
import com.jocoos.mybeautip.global.dto.IdAndBooleanResponse.CanChatResponse;
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
public class BroadcastMessageController {

    private final BroadcastMessageService service;

    @PatchMapping("/1/broadcast/{broadcastId}/message-room/status")
    public CanChatResponse changeMessageRoomStatus(@PathVariable Long broadcastId,
                                                   @CurrentMember MyBeautipUserDetails userDetails,
                                                   BooleanDto canChatRequest) {
        return service.sendChangeMessageRoomStatus(broadcastId, userDetails.getMember().getId(), canChatRequest.isBool());
    }

    @PatchMapping("/1/broadcast/{broadcastId}/chat/{chatId}/pin")
    public void pinChat(@PathVariable Long broadcastId, @PathVariable Long chatId) {
        service.pinChat(broadcastId, chatId);
    }
}
