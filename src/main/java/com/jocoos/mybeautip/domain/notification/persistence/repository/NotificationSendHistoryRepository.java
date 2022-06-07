package com.jocoos.mybeautip.domain.notification.persistence.repository;

import com.jocoos.mybeautip.domain.notification.persistence.domain.NotificationSendHistoryEntity;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationSendHistoryRepository extends DefaultJpaRepository<NotificationSendHistoryEntity, Long> {
}
