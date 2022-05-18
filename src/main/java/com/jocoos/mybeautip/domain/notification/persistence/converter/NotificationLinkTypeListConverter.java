package com.jocoos.mybeautip.domain.notification.persistence.converter;

import com.jocoos.mybeautip.domain.notification.code.NotificationLinkType;
import com.jocoos.mybeautip.global.converter.GenericEnumListConverter;

import javax.persistence.AttributeConverter;
import java.util.List;

public class NotificationLinkTypeListConverter extends GenericEnumListConverter<NotificationLinkType> implements AttributeConverter<List<NotificationLinkType>, String> {

    public NotificationLinkTypeListConverter() {
        super(NotificationLinkType.class);
    }
}
