package com.jocoos.mybeautip.domain.broadcast.service.child;

import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastRelationInfo;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastNotification;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastNotificationDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import com.jocoos.mybeautip.global.util.EntityUtil;
import com.jocoos.mybeautip.global.util.MapUtil;
import com.jocoos.mybeautip.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class BroadcastRelationService {

    private final BroadcastNotificationDao notificationDao;

    @Transactional(readOnly = true)
    public Map<Long, BroadcastRelationInfo> getRelationInfoMap(String tokenUsername, List<BroadcastSearchResult> results) {
        Set<Long> ids = EntityUtil.extractLongSet(results, BroadcastSearchResult::getId);

        if (MemberUtil.isGuest(tokenUsername)) {
            return MapUtil.toIdentityMap(ids, id -> new BroadcastRelationInfo(false));
        }

        List<BroadcastNotification> notifications = notificationDao.getNotificationsIn(ids, Long.parseLong(tokenUsername));
        Set<Long> notifyBroadcastIds = EntityUtil.extractLongSet(notifications, BroadcastNotification::getBroadcastId);
        return MapUtil.toIdentityMap(ids, id -> new BroadcastRelationInfo(notifyBroadcastIds.contains(id)));
    }
}
