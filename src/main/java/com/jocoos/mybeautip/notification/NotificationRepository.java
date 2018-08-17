package com.jocoos.mybeautip.notification;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  Optional<Notification> findByIdAndTargetMemberId(Long id, Long targetMember);

  Slice<Notification> findByTargetMemberId(Long targetMember, Pageable pageable);
}
