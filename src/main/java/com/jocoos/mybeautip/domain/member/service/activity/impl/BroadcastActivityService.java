package com.jocoos.mybeautip.domain.member.service.activity.impl;

import com.jocoos.mybeautip.domain.broadcast.converter.BroadcastConverter;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastListResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastPermission;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastRelationInfo;
import com.jocoos.mybeautip.domain.broadcast.service.child.BroadcastRelationService;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastPermissionDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import com.jocoos.mybeautip.domain.member.code.MemberActivityType;
import com.jocoos.mybeautip.domain.member.dto.MemberActivityRequest;
import com.jocoos.mybeautip.domain.member.service.activity.MyActivityService;
import com.jocoos.mybeautip.global.exception.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastSortField.SORTED_STATUS;
import static org.springframework.data.domain.Sort.Direction.ASC;

@RequiredArgsConstructor
@Service
public class BroadcastActivityService implements MyActivityService<BroadcastListResponse> {

    private final BroadcastDao broadcastDao;
    private final BroadcastRelationService relationService;
    private final BroadcastPermissionDao permissionDao;
    private final BroadcastConverter converter;

    @Override
    public MemberActivityType getType() {
        return MemberActivityType.BROADCAST;
    }

    @Override
    public List<BroadcastListResponse> getMyActivity(MemberActivityRequest request) {
        Long memberId = request.member().getId();
        validCanView(memberId);
        BroadcastSearchCondition condition = BroadcastSearchCondition.builder()
                .memberId(memberId)
                .pageable(PageRequest.of(0, request.size(), SORTED_STATUS.getSort(ASC)))
                .cursor(request.idCursor())
                .build();
        List<BroadcastSearchResult> results = broadcastDao.getList(condition);
        Map<Long, BroadcastRelationInfo> relationInfoMap = relationService.getRelationInfoMapForMember(request.member().getId(), results);
        return converter.toListResponse(results, relationInfoMap);
    }

    public void validCanView(Long memberId) {
        BroadcastPermission permission = permissionDao.getBroadcastPermission(memberId);
        if (!permission.availableBroadcast()) {
            throw new AccessDeniedException("Request Member Not Viewable Broadcast Activity. Member Id : " + memberId);
        }
    }
}
