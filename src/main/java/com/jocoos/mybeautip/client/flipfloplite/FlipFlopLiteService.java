package com.jocoos.mybeautip.client.flipfloplite;

import com.jocoos.mybeautip.client.flipfloplite.converter.FlipFlopLiteConverter;
import com.jocoos.mybeautip.client.flipfloplite.dto.*;
import com.jocoos.mybeautip.domain.broadcast.dto.CreateBroadcastRequest;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class FlipFlopLiteService {

    private final MemberDao memberDao;
    private final FlipFlopLiteClient client;
    private final FlipFlopLiteConverter converter;

    public FFLVideoRoomResponse createVideoRoom(CreateBroadcastRequest request) {
        FFLVideoRoomRequest fflRequest = converter.converts(request);
        return client.createVideoRoom(fflRequest);
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
        return client.getStreamKey(memberId);
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
