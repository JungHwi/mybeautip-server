package com.jocoos.mybeautip.client.flipfloplite;

import com.jocoos.mybeautip.client.flipfloplite.config.FlipFlopLiteClientConfig;
import com.jocoos.mybeautip.client.flipfloplite.dto.*;
import com.jocoos.mybeautip.global.vo.EmptyResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "fflClient", url = "${ffl.domain}", configuration = {FlipFlopLiteClientConfig.class})
public interface FlipFlopLiteClient {

    // Member
    @PostMapping("/v2/apps/me/members/login-as-guest")
    FFLTokenResponse loginGuest();

    @PostMapping("/v2/apps/me/members/login")
    FFLTokenResponse login(FFLMemberInfo memberInfo);

    @GetMapping("/v2/apps/me/members/{appUserId}/stream-key")
    FFLStreamKeyResponse getStreamKey(@PathVariable long appUserId);

    @PostMapping("/v2/apps/me/members/{appUserId}/chat-tokens")
    FFLChatTokenResponse getChatToken(@PathVariable long appUserId);

    // Video Room
    @PostMapping("/v2/apps/me/video-rooms")
    FFLVideoRoomResponse createVideoRoom(FFLVideoRoomRequest request);

    @PostMapping("/v2/apps/me/video-rooms/{videoRoomId}/rtmp-broadcast/state/live")
    FFLVideoRoomResponse startVideoRoom(@PathVariable long videoRoomId);

    @PostMapping("/v2/apps/me/video-rooms/{videoRoomId}/rtmp-broadcast/state/ended")
    FFLVideoRoomResponse endVideoRoom(@PathVariable long videoRoomId);

    // Chat Room
    @PostMapping("/v2/apps/me/video-rooms/{videoRoomId}/chat-room")
    FFLChatRoomResponse createChatRoom(@PathVariable long videoRoomId);

    @PostMapping("/v2/apps/me/video-rooms/{videoRoomId}/chat-room/close")
    FFLChatRoomResponse closeChatRoom(@PathVariable long videoRoomId);

    @GetMapping("/v2/apps/me/video-rooms/{videoRoomId}/chat-room/members")
    FFLCursorResponse<FFLChatMemberInfo> getChatMembers(@PathVariable long videoRoomId);

    // Chat Message
    @PostMapping("/v2/apps/me/video-rooms/{videoRoomId}/chat-room/messages/{messageId}/hide")
    EmptyResult hideMessage(@PathVariable long videoRoomId, @PathVariable long messageId);

    @PostMapping("/v2/apps/me/video-rooms/{videoRoomId}/chat-room/messages/{messageId}/unhide")
    EmptyResult unhideMessage(@PathVariable long videoRoomId, @PathVariable long messageId);

    @PostMapping("/v2/apps/me/video-rooms/{videoRoomId}/chat-room/admin/messages/broadcast")
    EmptyResult broadcastMessage(@PathVariable long videoRoomId, FFLBroadcastMessageRequest request);

    @PostMapping("/v2/apps/me/video-rooms/{videoRoomId}/chat-room/admin/messages/direct")
    EmptyResult directMessage(@PathVariable long videoRoomId, FFLDirectMessageRequest request);

}
