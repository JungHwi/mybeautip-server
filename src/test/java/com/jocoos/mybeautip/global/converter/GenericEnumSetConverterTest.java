package com.jocoos.mybeautip.global.converter;

import com.jocoos.mybeautip.domain.notification.code.SendType;
import com.jocoos.mybeautip.domain.notification.persistence.converter.SendTypeSetConverter;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GenericEnumSetConverterTest {
    SendTypeSetConverter converter = new SendTypeSetConverter() ;

    @Test
    void testConvertToDatabaseColumn() {
        Set<SendType> sendTypes = Sets.newSet(SendType.CENTER, SendType.APP_PUSH);
        String actual = converter.convertToDatabaseColumn(sendTypes);
        assertEquals("CENTER,APP_PUSH", actual);
    }

    @Test
    void convertToEntityAttribute() {
        String attribute = "CENTER,APP_PUSH";
        Set<SendType> actual = converter.convertToEntityAttribute(attribute);
        Set<SendType> expected = Sets.newSet(SendType.CENTER, SendType.APP_PUSH);

        assertEquals(expected, actual);
    }
}