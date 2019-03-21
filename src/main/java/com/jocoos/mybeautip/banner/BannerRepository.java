package com.jocoos.mybeautip.banner;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "headers", path = "headers")
public interface BannerRepository extends JpaRepository<Banner, Long> {

  @Modifying
  @Query("update Banner b set b.viewCount = b.viewCount + ?2, b.modifiedAt = now() where b.id = ?1")
  void updateViewCount(Long id, Long count);

  Slice<Banner> findByStartedAtBeforeAndEndedAtAfterAndDeletedAtIsNull(Date statedAt, Date endedAt, Pageable pageable);

  Page<Banner> findByDeletedAtIsNull(Pageable pageable);

  Page<Banner> findByDeletedAtIsNotNull(Pageable pageable);

}
