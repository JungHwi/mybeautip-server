package com.jocoos.mybeautip.post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {
  Optional<PostReport> findByPostIdAndCreatedById(Long postId, Long createdById);
}