package com.jocoos.mybeautip.domain.notification.persistence.repository;

import com.jocoos.mybeautip.domain.notification.code.TemplateType;
import com.jocoos.mybeautip.domain.notification.persistence.domain.NotificationMessageCenterEntity;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationMessageCenterRepository extends DefaultJpaRepository<NotificationMessageCenterEntity, Long> {

    int countByTemplateIdAndLastVersionIsTrue(TemplateType templateType);
    List<NotificationMessageCenterEntity> findByTemplateIdAndLastVersionIsTrue(TemplateType templateType);
}
