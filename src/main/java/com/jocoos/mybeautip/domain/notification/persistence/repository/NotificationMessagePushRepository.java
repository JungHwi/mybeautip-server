package com.jocoos.mybeautip.domain.notification.persistence.repository;

import com.jocoos.mybeautip.domain.notification.code.TemplateType;
import com.jocoos.mybeautip.domain.notification.persistence.domain.NotificationMessagePushEntity;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;

import java.util.List;

public interface NotificationMessagePushRepository extends DefaultJpaRepository<NotificationMessagePushEntity, Long> {

    List<NotificationMessagePushEntity> findByTemplateIdAndIsLastVersionIsTrue(TemplateType templateType);

}
