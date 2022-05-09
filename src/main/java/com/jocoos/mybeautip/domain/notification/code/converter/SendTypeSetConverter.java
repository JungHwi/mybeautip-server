package com.jocoos.mybeautip.domain.notification.code.converter;

import com.jocoos.mybeautip.domain.notification.code.SendType;
import com.jocoos.mybeautip.global.converter.GenericEnumSetConverter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Set;

@Converter
public class SendTypeSetConverter extends GenericEnumSetConverter<SendType> implements AttributeConverter<Set<SendType>, String> {

    public SendTypeSetConverter() {
        super(SendType.class);
    }
}
