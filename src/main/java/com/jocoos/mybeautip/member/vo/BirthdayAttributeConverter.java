package com.jocoos.mybeautip.member.vo;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

@Convert
public class BirthdayAttributeConverter implements AttributeConverter<Birthday, String> {

    @Override
    public String convertToDatabaseColumn(Birthday attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.toString();
    }

    @Override
    public Birthday convertToEntityAttribute(String dbData) {
        return new Birthday(dbData);
    }
}
