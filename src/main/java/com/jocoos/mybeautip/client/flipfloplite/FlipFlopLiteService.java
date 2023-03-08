package com.jocoos.mybeautip.client.flipfloplite;

import com.jocoos.mybeautip.client.flipfloplite.converter.FlipFlopLiteConverter;
import com.jocoos.mybeautip.client.flipfloplite.dto.*;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.global.vo.EmptyResult;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.client.flipfloplite.code.FFLVideoRoomState.ENDED;
import static com.jocoos.mybeautip.client.flipfloplite.code.FFLVideoRoomState.LIVE;

@Service
@RequiredArgsConstructor
@Log4j2
public class FlipFlopLiteService {

    private final MemberDao memberDao;
    private final FlipFlopLiteClient client;
    private final FlipFlopLiteConverter converter;

    public ExternalBroadcastInfo createVideoRoom(Broadcast broadcast) {
        FFLVideoRoomRequest fflRequest = converter.converts(broadcast);
        FFLVideoRoomResponse response = client.createVideoRoom(fflRequest);
        return converter.converts(response);
    }

    public FFLTokenResponse loginGuest() {
        FFLTokenResponse response = client.loginGuest();
        return response;
    }

    public FFLTokenResponse login(Member member) {
        FFLMemberInfo fflMemberInfo = converter.converts(member);
        return client.login(fflMemberInfo);
    }

    public FFLTokenResponse login(long memberId) {
        Member member = memberDao.getMember(memberId);
        return this.login(member);
    }

    public ExternalBroadcastInfo startVideoRoom(long videoRoomId) {
        FFLVideoRoomResponse response = client.startVideoRoom(videoRoomId);
        if (response.videoRoomState() != LIVE) {

        }
        return converter.converts(response);
    }

    public ZonedDateTime endVideoRoom(long videoRoomId) {
        FFLVideoRoomResponse response = client.endVideoRoom(videoRoomId);
        if (response.videoRoomState() != ENDED) {

        }
        return response.lastModifiedAt();
    }

    public String getStreamKey(long memberId) {
        FFLStreamKeyResponse response = client.getStreamKey(memberId);
        return response.streamKey();
    }

    public FFLChatTokenResponse getChatToken(long memberId) {
        return client.getChatToken(memberId);
    }

    public FFLChatRoomResponse createChatRoom(long videoRoomId) {
        return client.createChatRoom(videoRoomId);
    }

    public FFLChatRoomResponse closeChatRoom(long videoRoomId) {
        return client.closeChatRoom(videoRoomId);
    }

    public FFLCursorResponse<FFLChatMemberInfo> getChatMembers(long videoRoomId) {
        return client.getChatMembers(videoRoomId);
    }

    public EmptyResult hideMessage(long videoRoomId, long messageId) {
        return client.hideMessage(videoRoomId, messageId);
    }

    public EmptyResult unhideMessage(long videoRoomId, long messageId) {
        return client.unhideMessage(videoRoomId, messageId);
    }

    public EmptyResult broadcastMessage(long videoRoomId, FFLBroadcastMessageRequest request) {
        return client.broadcastMessage(videoRoomId, request);
    }

    public EmptyResult directMessage(long videoRoomId, FFLDirectMessageRequest request) {
        return client.directMessage(videoRoomId, request);
    }

    public int migration() {
        int count = 0;
        List<Member> memberList = memberDao.getAll();
        for (Member member : memberList) {
            this.login(member);
            count++;
        }
        return count;
    }
}
