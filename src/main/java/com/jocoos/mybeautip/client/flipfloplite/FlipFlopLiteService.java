package com.jocoos.mybeautip.client.flipfloplite;

import com.jocoos.mybeautip.client.flipfloplite.code.FFLErrorCode;
import com.jocoos.mybeautip.client.flipfloplite.converter.FlipFlopLiteConverter;
import com.jocoos.mybeautip.client.flipfloplite.dto.*;
import com.jocoos.mybeautip.client.flipfloplite.exception.FFLException;
import com.jocoos.mybeautip.domain.broadcast.dto.VisibleMessageRequest;
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
import static com.jocoos.mybeautip.global.constant.MybeautipConstant.MAX_RETRY_FFL;

@Service
@RequiredArgsConstructor
@Log4j2
public class FlipFlopLiteService {

    private final MemberDao memberDao;
    private final FlipFlopLiteClient client;
    private final FlipFlopLiteConverter converter;

    public ExternalBroadcastInfo createVideoRoom(FFLVideoRoomRequest request) {
        FFLVideoRoomResponse response = client.createVideoRoom(request);
        return converter.converts(response);
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

    public FFLStreamKeyResponse getStreamKey(long memberId) {
        return this.getStreamKey(memberId, 0);
    }

    public FFLStreamKeyResponse getStreamKey(long memberId, int count) {
        try {
            return client.getStreamKey(memberId);
        } catch (FFLException ex) {
            if (ex.getErrorCode() == FFLErrorCode.MEMBER_NOT_FOUND && count < MAX_RETRY_FFL) {
                this.login(memberId);
                return this.getStreamKey(memberId, ++count);
            } else {
                throw ex;
            }
        }
    }

    public Long getMemberIdFrom(long streamKeyId) {
        FFLStreamKeyResponse response = client.getStreamKeyById(streamKeyId);
        return Long.parseLong(response.member().appUserId());
    }

    public ChatTokenAndAppId getChatToken(long memberId) {
        FFLChatTokenResponse response = client.getChatToken(memberId);
        return converter.converts(response);
    }

    public ChatTokenAndAppId getGuestChatToken(String guestUsername) {
        FFLGuestChatTokenRequest request = FFLGuestChatTokenRequest.from(guestUsername);
        FFLChatTokenResponse response = client.getGuestChatToken(request);
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
        // FIXME 우선 500 명 한번에 불러옴. 시청자 수가 500 보다 많아 지기 전에 Loop 돌면서 모든 시청자 목록 불러오도록 수정.
        FFLCursorResponse<FFLChatMemberInfo> response = client.getChatMembers(videoRoomId, cursor, size);
        return converter.convertToViewerVo(response.content());
    }

    public EmptyResult visibleMessage(long videoRoomId, VisibleMessageRequest request) {
        if (request.isVisible()) {
            return client.visibleMessage(videoRoomId, request.messageId());
        } else {
            return client.invisibleMessage(videoRoomId, request.messageId());
        }
    }

    public void broadcastMessage(long videoRoomId, FFLBroadcastMessageRequest request) {
        try {
            client.broadcastMessage(videoRoomId, request);
        } catch (FFLException e) {
            log.warn("FFLException Broadcast Message Exception - [{}] {}, Video Room : {}, request : {}", e.getErrorCode(), e.getErrorMessage(), videoRoomId, request.toString());
        }
    }

    public void directMessage(long videoRoomId, FFLDirectMessageRequest request) {
        try {
            client.directMessage(videoRoomId, request);
        } catch (FFLException e) {
            log.warn("FFLException Direct Message Exception - [{}] {}, Video Room : {}, request : {}", e.getErrorCode(), e.getErrorMessage(), videoRoomId, request.toString());
        }
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
