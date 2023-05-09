package com.jocoos.mybeautip.legacy.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LegacyStoreRepository extends JpaRepository<LegacyStore, Integer> {
    @Modifying
    @Query("update LegacyStore s set s.likeCount = s.likeCount + ?2, s.modifiedAt = now() where s.id = ?1")
    void updateLikeCount(Integer id, Integer count);
}