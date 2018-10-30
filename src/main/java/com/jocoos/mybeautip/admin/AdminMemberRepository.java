package com.jocoos.mybeautip.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(excerptProjection = AdminMemberExcerpt.class)
public interface AdminMemberRepository extends JpaRepository<AdminMember, String> {

  Page<AdminMember> findByMemberLinkOrderByCreatedAtDesc(int link, Pageable pageable);

  boolean existsByEmail(String email);
}
