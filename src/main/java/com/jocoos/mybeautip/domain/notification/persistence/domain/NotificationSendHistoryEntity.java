package com.jocoos.mybeautip.domain.notification.persistence.domain;

import com.jocoos.mybeautip.domain.notification.code.NotificationPlatform;
import com.jocoos.mybeautip.global.config.jpa.CreatedAtBaseEntity;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "notification_send_history")
public class NotificationSendHistoryEntity extends CreatedAtBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private long id;

    @Column
    private long userId;

    @Column
    @Enumerated(EnumType.STRING)
    private NotificationPlatform platform;

    @Column
    private String target;

    @Column
    private Long messageId;

    @Column
    private String emailFile;

    @Column
    private String arguments;
}
