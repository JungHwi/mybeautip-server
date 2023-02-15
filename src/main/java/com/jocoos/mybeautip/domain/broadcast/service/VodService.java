package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.domain.broadcast.dto.VodResponse;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Vod;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastCategoryDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.VodDao;
import com.jocoos.mybeautip.domain.broadcast.vo.VodSearchCondition;
import com.jocoos.mybeautip.global.dto.IsVisibleResponse;
import com.jocoos.mybeautip.global.vo.CursorPaging;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastCategoryType.GROUP;

@RequiredArgsConstructor
@Service
public class VodService {

    private final VodDao vodDao;
    private final BroadcastCategoryDao categoryDao;

    @Transactional(readOnly = true)
    public List<VodResponse> getVodList(long categoryId, CursorPaging<Long> cursorPaging) {
        VodSearchCondition condition = VodSearchCondition.builder()
                .categoryIds(getCategories(categoryId))
                .cursorPaging(cursorPaging)
                .build();
        return vodDao.getList(condition);
    }

    @Transactional
    public IsVisibleResponse changeVodVisibility(Long id, boolean isVisible) {
        Vod vod = vodDao.get(id);
        vod.visible(isVisible);
        return new IsVisibleResponse(vod.getId(), vod.isVisible());
    }

    private List<Long> getCategories(long categoryId) {
        BroadcastCategory category = categoryDao.getBroadcastCategory(categoryId);
        if (category.isType(GROUP)) {
            return categoryDao.getChildCategories(categoryId).stream()
                    .map(BroadcastCategory::getId)
                    .toList();
        }
        return List.of(category.getId());
    }
}
