package com.jocoos.mybeautip.domain.notification.persistence.repository;

import com.jocoos.mybeautip.domain.notification.code.TemplateType;
import com.jocoos.mybeautip.domain.notification.persistence.domain.NotificationTemplateEntity;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationTemplateRepository extends DefaultJpaRepository<NotificationTemplateEntity, TemplateType> {

    List<NotificationTemplateEntity> findAllBy();

}
