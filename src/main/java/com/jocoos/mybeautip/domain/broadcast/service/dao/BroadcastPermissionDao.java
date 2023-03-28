package com.jocoos.mybeautip.domain.broadcast.service.dao;

import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastPermission;
import com.jocoos.mybeautip.domain.member.service.dao.InfluencerDao;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.domain.system.service.dao.SystemOptionDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jocoos.mybeautip.domain.system.code.SystemOptionType.FREE_LIVE_PERMISSION;

@RequiredArgsConstructor
@Service
public class BroadcastPermissionDao {

    private final BroadcastDao broadcastDao;
    private final InfluencerDao influencerDao;
    private final SystemOptionDao systemOptionDao;
    private final BroadcastViewerDao viewerDao;
    private final MemberDao memberDao;

    @Transactional(readOnly = true)
    public boolean canBroadcast(Long memberId) {
        return systemOptionDao.getSystemOption(FREE_LIVE_PERMISSION)
                || influencerDao.isInfluencer(memberId);
    }

    @Transactional(readOnly = true)
    public BroadcastPermission getBroadcastPermission(Long memberId) {
        boolean availableBroadcast = canBroadcast(memberId);
        boolean viewableLiveMenu = availableBroadcast || broadcastDao.countByMemberId(memberId) > 0;
        return new BroadcastPermission(availableBroadcast, viewableLiveMenu);
    }

    @Transactional(readOnly = true)
    public boolean isManagerOrAdmin(Long broadcastId, Long requestMemberId) {
        return viewerDao.isManager(broadcastId, requestMemberId)
                || memberDao.isAdmin(requestMemberId);
    }
}
