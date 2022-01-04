package com.jocoos.mybeautip.member.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
  Optional<CommentReport> findByCommentIdAndCreatedById(Long commentId, Long createdById);
}