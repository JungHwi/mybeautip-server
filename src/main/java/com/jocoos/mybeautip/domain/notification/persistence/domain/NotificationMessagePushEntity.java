package com.jocoos.mybeautip.domain.notification.persistence.domain;

import com.jocoos.mybeautip.domain.notification.code.MessageType;
import com.jocoos.mybeautip.domain.notification.code.TemplateType;
import com.jocoos.mybeautip.global.code.Language;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "notification_message_push")
public class NotificationMessagePushEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TemplateType templateId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 6)
    private Language lang;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageType messageType;

    @Column(nullable = false)
    private boolean isLastVersion;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    @Column(length = 200)
    private String deepLink;
}
