package com.jocoos.mybeautip.domain.notification.code.converter;

import com.jocoos.mybeautip.domain.notification.code.NotificationArgument;
import com.jocoos.mybeautip.global.converter.GenericEnumSetConverter;

import javax.persistence.AttributeConverter;
import java.util.Set;

public class NotificationArgumentSetConverter extends GenericEnumSetConverter<NotificationArgument> implements AttributeConverter<Set<NotificationArgument>, String> {

    public NotificationArgumentSetConverter() {
        super(NotificationArgument.class);
    }
}
