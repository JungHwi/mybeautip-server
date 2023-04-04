package com.jocoos.mybeautip.domain.member.service.activity.impl;

import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastPermission;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastPermissionDao;
import com.jocoos.mybeautip.domain.member.code.MemberActivityType;
import com.jocoos.mybeautip.domain.member.dto.MemberActivityRequest;
import com.jocoos.mybeautip.domain.member.service.activity.MyActivityService;
import com.jocoos.mybeautip.domain.vod.code.VodSortField;
import com.jocoos.mybeautip.domain.vod.dto.VodListResponse;
import com.jocoos.mybeautip.domain.vod.service.dao.VodDao;
import com.jocoos.mybeautip.domain.vod.vo.VodSearchCondition;
import com.jocoos.mybeautip.global.exception.AccessDeniedException;
import com.jocoos.mybeautip.global.vo.CursorPaging;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.jocoos.mybeautip.domain.vod.code.VodStatus.AVAILABLE;
import static org.springframework.data.domain.Sort.Direction.DESC;

@RequiredArgsConstructor
@Service
public class VodActivityService implements MyActivityService<VodListResponse> {

    private final VodDao vodDao;
    private final BroadcastPermissionDao permissionDao;

    @Override
    public MemberActivityType getType() {
        return MemberActivityType.VOD;
    }

    @Override
    public List<VodListResponse> getMyActivity(MemberActivityRequest request) {
        Long memberId = request.member().getId();
        validCanView(memberId);

        VodSortField defaultSort = VodSortField.CREATED_AT;
        CursorPaging<Long> cursorPaging = CursorPaging.idCursorWithNonUniqueSortField(request.idCursor(), defaultSort.getSortField());
        Pageable pageable = PageRequest.of(0, request.size(), defaultSort.getSort(DESC));
        VodSearchCondition condition = VodSearchCondition.builder()
                .isVisible(true)
                .status(AVAILABLE)
                .memberId(memberId)
                .cursorPaging(cursorPaging)
                .pageable(pageable)
                .build();
        return vodDao.getListWithMember(condition);
    }

    public void validCanView(Long memberId) {
        BroadcastPermission permission = permissionDao.getBroadcastPermission(memberId);
        if (!permission.availableBroadcast()) {
            throw new AccessDeniedException("Request Member Not Viewable Broadcast Activity. Member Id : " + memberId);
        }
    }
}
