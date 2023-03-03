package com.jocoos.mybeautip.client.flipfloplite;

import com.jocoos.mybeautip.client.flipfloplite.converter.FlipFlopLiteConverter;
import com.jocoos.mybeautip.client.flipfloplite.dto.*;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastCreateRequest;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
// FIXME 재훈님, 각 API Response 를 우선은 그냥 FFL 기준으로 작성 했습니다. 실제로 사용하기 전에 마이뷰팁에 맞게끔 여기서 convert 하도록 수정해 주세요.
public class FlipFlopLiteService {

    private final MemberDao memberDao;
    private final FlipFlopLiteClient client;
    private final FlipFlopLiteConverter converter;

    public ExternalBroadcastInfo createVideoRoom(BroadcastCreateRequest request, long memberId) {
        FFLVideoRoomRequest fflRequest = converter.converts(request, memberId);
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

    public FFLVideoRoomResponse startVideoRoom(long videoRoomId) {
        return client.startVideoRoom(videoRoomId);
    }

    public FFLVideoRoomResponse endVideoRoom(long videoRoomId) {
        return client.endVideoRoom(videoRoomId);
    }

    public FFLStreamKeyResponse getStreamKey(long memberId) {
        return client.getStreamKey(memberId);
    }

    public FFLChatTokenResponse getChatToken(long memberId) {
        return client.getChatToken(memberId);
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
