package com.jocoos.mybeautip.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.jocoos.mybeautip.banner.Banner;

@RepositoryRestResource(collectionResourceRel = "headers", path = "headers")
public interface AdminBannerRepository extends JpaRepository<Banner, Long> {

  Page<Banner> findByDeletedAtIsNull(Pageable pageable);

  Page<Banner> findByDeletedAtIsNotNull(Pageable pageable);
}
