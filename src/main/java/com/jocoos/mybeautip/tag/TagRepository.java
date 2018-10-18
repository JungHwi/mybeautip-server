package com.jocoos.mybeautip.tag;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TagRepository extends JpaRepository<Tag, Long> {
  
  Optional<Tag> findByName(String name);
  
  @Modifying
  @Query("update Tag t set t.refCount = t.refCount + ?2, t.modifiedAt = now() where t.id = ?1")
  void updateTagRefCount(Long id, Integer count);
}