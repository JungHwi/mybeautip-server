package com.jocoos.mybeautip.domain.notification.service;

import com.jocoos.mybeautip.domain.notification.code.NotificationStatus;
import com.jocoos.mybeautip.domain.notification.persistence.domain.NotificationCenterEntity;
import com.jocoos.mybeautip.domain.notification.persistence.repository.NotificationCenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CenterServiceImpl {

    private final NotificationCenterRepository repository;

    @Transactional(readOnly = true)
    public Page<NotificationCenterEntity> getUserCenterMessage(long userId, long id, Pageable pageable) {
        return repository.findByUserIdAndIdLessThan(userId, id, pageable);
    }

    @Transactional
    public int patchRead(long userId, long id) {
        return repository.patchStatus(userId, id, NotificationStatus.READ);
    }

    @Transactional
    public int patchReadAll(long userId) {
        return repository.patchStatus(userId, NotificationStatus.NOT_READ, NotificationStatus.READ);
    }
}
