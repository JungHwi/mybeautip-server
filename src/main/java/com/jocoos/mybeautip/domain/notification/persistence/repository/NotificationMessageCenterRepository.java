package com.jocoos.mybeautip.domain.notification.persistence.repository;

import com.jocoos.mybeautip.domain.notification.code.TemplateType;
import com.jocoos.mybeautip.domain.notification.persistence.domain.NotificationMessageCenterEntity;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;

import java.util.List;

public interface NotificationMessageCenterRepository extends DefaultJpaRepository<NotificationMessageCenterEntity, Long> {

    int countByTemplateIdAndIsLastVersionIsTrue(TemplateType templateType);
    List<NotificationMessageCenterEntity> findByTemplateIdAndIsLastVersionIsTrue(TemplateType templateType);
}
