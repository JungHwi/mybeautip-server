package com.jocoos.mybeautip.domain.notification.service.dao;

import com.jocoos.mybeautip.domain.notification.code.TemplateType;
import com.jocoos.mybeautip.domain.notification.persistence.domain.NotificationCenterEntity;
import com.jocoos.mybeautip.domain.notification.persistence.domain.NotificationMessageCenterEntity;
import com.jocoos.mybeautip.domain.notification.persistence.repository.NotificationCenterRepository;
import com.jocoos.mybeautip.domain.notification.persistence.repository.NotificationMessageCenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificationMessageCenterDao {

    private final NotificationMessageCenterRepository repository;
    private final NotificationCenterRepository notificationCenterRepository;

    @Transactional
    public NotificationCenterEntity saveNotificationSend(NotificationCenterEntity entity) {
        return notificationCenterRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public int getMessageRandomIndex(TemplateType templateType) {
        return repository.countByTemplateIdAndLastVersionIsTrue(templateType);
    }

    @Transactional(readOnly = true)
    public List<NotificationMessageCenterEntity> getLastVersion(TemplateType templateType) {
        return repository.findByTemplateIdAndLastVersionIsTrue(templateType);
    }
}
