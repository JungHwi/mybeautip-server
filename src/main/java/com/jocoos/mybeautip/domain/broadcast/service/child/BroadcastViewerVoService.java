package com.jocoos.mybeautip.domain.broadcast.service.child;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastViewerDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastViewerVo;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.GUEST_TOKEN_PREFIX;
import static com.jocoos.mybeautip.global.constant.SignConstant.EMPTY_STRING;

@Service
@RequiredArgsConstructor
public class BroadcastViewerVoService {

    private final MemberDao memberDao;
    private final BroadcastDao broadcastDao;
    private final BroadcastViewerDao broadcastViewerDao;

    public BroadcastViewerVo of(Long broadcastId, String tokenUsername) {
        Long memberId = getMemberId(tokenUsername);
        String username = getUsername(tokenUsername);
        BroadcastViewerType type = getType(broadcastId, tokenUsername);

        return new BroadcastViewerVo(type, memberId, username);
    }

    private Long getMemberId(String username) {
        return Long.parseLong(username.replace(GUEST_TOKEN_PREFIX, EMPTY_STRING));
    }

    private String getUsername(String username) {
        if (username.startsWith(GUEST_TOKEN_PREFIX)) {
            return username;
        } else {
            return memberDao.getMember(Long.parseLong(username)).getUsername();
        }
    }

    private BroadcastViewerType getType(long broadcastId, String tokenUsername) {
        if (MemberUtil.isGuest(tokenUsername)) {
            return BroadcastViewerType.GUEST;
        } else if (isInfluencer(broadcastId, Long.parseLong(tokenUsername))) {
            return BroadcastViewerType.OWNER;
        } else if (isManager(broadcastId, Long.parseLong(tokenUsername))) {
            return BroadcastViewerType.MANAGER;
        } else if (memberDao.isAdmin(Long.parseLong(tokenUsername))) {
            return BroadcastViewerType.ADMIN;
        } else {
            return BroadcastViewerType.MEMBER;
        }
    }

    private boolean isInfluencer(long broadcastId, long memberId) {
        return broadcastDao.isCreator(broadcastId, memberId);
    }

    private boolean isManager(long broadcastId, long memberId) {
        return broadcastViewerDao.isManager(broadcastId, memberId);
    }
}
