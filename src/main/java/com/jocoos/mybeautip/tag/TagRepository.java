package com.jocoos.mybeautip.tag;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);

    List<Tag> findByNameStartingWith(String keyword, Pageable pageable);

    @Modifying
    @Query("update Tag t set t.refCount = t.refCount + ?2, t.modifiedAt = now() where t.id = ?1")
    void updateTagRefCount(Long id, Integer count);
}