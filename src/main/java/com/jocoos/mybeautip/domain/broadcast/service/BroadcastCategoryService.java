package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastCategoryResponse;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastCategoryDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BroadcastCategoryService {

    private final BroadcastCategoryDao categoryDao;

    @Transactional(readOnly = true)
    public List<BroadcastCategoryResponse> getAll(boolean withGroup) {
        return categoryDao.getAll()
                .stream()
                .filter(category -> withGroup || !category.isGroup())
                .map(BroadcastCategoryResponse::new)
                .toList();
    }
}
