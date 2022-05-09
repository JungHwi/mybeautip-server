package com.jocoos.mybeautip.domain.notification.persistence.domain;

import com.jocoos.mybeautip.domain.notification.code.NotificationArgument;
import com.jocoos.mybeautip.domain.notification.code.SendType;
import com.jocoos.mybeautip.domain.notification.code.TemplateType;
import com.jocoos.mybeautip.domain.notification.code.converter.NotificationArgumentSetConverter;
import com.jocoos.mybeautip.domain.notification.code.converter.SendTypeSetConverter;
import lombok.Getter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Entity
@Table(name = "notification_template")
public class NotificationTemplateEntity {

    @Id
    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private TemplateType id;

    @Column(length = 100)
    private String description;

    @Column
    @Convert(converter = SendTypeSetConverter.class)
    private Set<SendType> sendTypes;

    @Column
    @Convert(converter = NotificationArgumentSetConverter.class)
    private Set<NotificationArgument> availableArguments;
}
