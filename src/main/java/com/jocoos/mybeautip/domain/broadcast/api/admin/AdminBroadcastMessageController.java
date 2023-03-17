package com.jocoos.mybeautip.domain.broadcast.api.admin;

import com.jocoos.mybeautip.domain.broadcast.service.BroadcastMessageService;
import com.jocoos.mybeautip.global.annotation.CurrentMember;
import com.jocoos.mybeautip.global.dto.IdAndBooleanResponse.CanChatResponse;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class AdminBroadcastMessageController {

    private final BroadcastMessageService service;

    @PatchMapping("/broadcast/{broadcastId}/message-room/status")
    public CanChatResponse changeMessageRoomStatus(@PathVariable Long broadcastId,
                                                   @CurrentMember MyBeautipUserDetails userDetails,
                                                   @RequestBody BooleanDto canChatRequest) {
        return service.sendChangeMessageRoomStatus(broadcastId, userDetails.getMember().getId(), canChatRequest.isBool());
    }
}
