package com.jocoos.mybeautip.domain.notification.persistence.repository;

import com.jocoos.mybeautip.domain.notification.code.NotificationStatus;
import com.jocoos.mybeautip.domain.notification.persistence.domain.NotificationCenterEntity;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationCenterRepository extends DefaultJpaRepository<NotificationCenterEntity, Long> {

    Page<NotificationCenterEntity> findByUserIdAndIdLessThan(long userId, long id, Pageable pageable);

    @Modifying
    @Query("UPDATE NotificationCenterEntity SET status = :status WHERE userId = :userId AND id = :id")
    int patchStatus(@Param("userId") long userId, @Param("id") long id, @Param("status") NotificationStatus status);

    @Modifying
    @Query("UPDATE NotificationCenterEntity SET status = :afterStatus WHERE userId = :userId AND status = :beforeStatus")
    int patchStatus(@Param("userId") long userId, @Param("beforeStatus") NotificationStatus beforeStatus, @Param("afterStatus") NotificationStatus afterStatus);
}
