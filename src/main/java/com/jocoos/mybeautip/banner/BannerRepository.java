package com.jocoos.mybeautip.banner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BannerRepository extends JpaRepository<Banner, Long> {

  @Modifying
  @Query("update Banner b set b.viewCount = b.viewCount + ?2, b.modifiedAt = now() where b.id = ?1")
  void updateViewCount(Long id, Long count);

}
