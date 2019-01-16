package com.jocoos.mybeautip.notification.event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PushMessageRepository extends JpaRepository<PushMessage, Long> {
}
