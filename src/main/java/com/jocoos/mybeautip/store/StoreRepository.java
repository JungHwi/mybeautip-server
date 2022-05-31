package com.jocoos.mybeautip.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface StoreRepository extends JpaRepository<Store, Integer> {
    @Modifying
    @Query("update Store s set s.likeCount = s.likeCount + ?2, s.modifiedAt = now() where s.id = ?1")
    void updateLikeCount(Integer id, Integer count);
}