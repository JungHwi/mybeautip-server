package com.jocoos.mybeautip.client.flipfloplite;

import com.jocoos.mybeautip.client.flipfloplite.converter.FlipFlopLiteConverter;
import com.jocoos.mybeautip.client.flipfloplite.dto.*;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastViewerVo;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.global.vo.EmptyResult;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.client.flipfloplite.code.FFLVideoRoomState.*;

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

    public ZonedDateTime cancelVideoRoom(long videoRoomId) {
        FFLVideoRoomResponse response = client.cancelVideoRoom(videoRoomId);
        if (response.videoRoomState() != CANCELLED) {

        }
        return response.lastModifiedAt();
    }

    public String getStreamKey(long memberId) {
        FFLStreamKeyResponse response = client.getStreamKey(memberId);
        return response.streamKey();
    }

    public ChatTokenAndAppId getChatToken(long memberId) {
        FFLChatTokenResponse response = client.getChatToken(memberId);
        return converter.converts(response);
    }

    public FFLChatRoomResponse createChatRoom(long videoRoomId) {
        return client.createChatRoom(videoRoomId);
    }

    public FFLChatRoomResponse closeChatRoom(long videoRoomId) {
        return client.closeChatRoom(videoRoomId);
    }

    public List<BroadcastViewerVo> getAllChatMembers(long videoRoomId) {
        long cursor = 0;
        long size = 500;
        // FIXME 우선 500 명 한번에 불러옴. 시청자 수가 900 보다 많아 지기 전에 Loop 돌면서 모든 시청자 목록 불러오도록 수정.

        FFLCursorResponse<FFLChatMemberInfo> response = client.getChatMembers(videoRoomId, cursor, size);

        FFLChatMemberInfo memberInfo1 = new FFLChatMemberInfo("8", "8회원", null, ZonedDateTime.now());
        FFLChatMemberInfo memberInfo2 = new FFLChatMemberInfo("9", "9회원", null, ZonedDateTime.now());
        FFLChatMemberInfo memberInfo3 = new FFLChatMemberInfo("3", "A회원", null, ZonedDateTime.now());
        FFLChatMemberInfo memberInfo4 = new FFLChatMemberInfo("4", "0회원", null, ZonedDateTime.now());
        FFLChatMemberInfo memberInfo5 = new FFLChatMemberInfo("5", "_회원", null, ZonedDateTime.now());
        FFLChatMemberInfo memberInfo6 = new FFLChatMemberInfo("6", "C회원", null, ZonedDateTime.now());
        FFLChatMemberInfo memberInfo7 = new FFLChatMemberInfo("7", "회원", null, ZonedDateTime.now());
        FFLChatMemberInfo memberInfoGuest = new FFLChatMemberInfo("guest:123", "Guest", null, ZonedDateTime.now());

        List<FFLChatMemberInfo> list = List.of(memberInfo1, memberInfo2, memberInfo3, memberInfo4, memberInfo5, memberInfo6, memberInfo7, memberInfoGuest);

//        return converter.convertToViewerVo(response.content());
        return converter.convertToViewerVo(list);
    }

    public EmptyResult hideMessage(long videoRoomId, long messageId) {
        return client.hideMessage(videoRoomId, messageId);
    }

    public EmptyResult unhideMessage(long videoRoomId, long messageId) {
        return client.unhideMessage(videoRoomId, messageId);
    }

    public Long broadcastMessage(long videoRoomId, FFLBroadcastMessageRequest request) {
        FFLMessageInfo messageInfo = client.broadcastMessage(videoRoomId, request);
        return messageInfo.messageId();
    }

    public Long directMessage(long videoRoomId, FFLDirectMessageRequest request) {
        FFLMessageInfo messageInfo =  client.directMessage(videoRoomId, request);
        return messageInfo.messageId();
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
