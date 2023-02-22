package com.jocoos.mybeautip.domain.broadcast.service.dao;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory;
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastCategoryRepository;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastCategoryType.GROUP;

@RequiredArgsConstructor
@Service
public class BroadcastCategoryDao {
    private final BroadcastCategoryRepository repository;

    @Transactional(readOnly = true)
    public List<BroadcastCategory> getCategories(long categoryId) {
        BroadcastCategory category = getBroadcastCategory(categoryId);
        if (category.isType(GROUP)) {
            return getChildCategories(categoryId);
        }
        return List.of(category);
    }

    private BroadcastCategory getBroadcastCategory(long categoryId) {
        return repository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(""));
    }

    private List<BroadcastCategory> getChildCategories(long parentId) {
        return repository.findAllByParentId(parentId);
    }
}
