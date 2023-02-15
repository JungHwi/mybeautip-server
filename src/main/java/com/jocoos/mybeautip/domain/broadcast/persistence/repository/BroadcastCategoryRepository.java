package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BroadcastCategoryRepository extends JpaRepository<BroadcastCategory, Long> {

    List<BroadcastCategory> findAllByParentId(long parentId);
}
