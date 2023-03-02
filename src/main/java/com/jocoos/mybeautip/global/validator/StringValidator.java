package com.jocoos.mybeautip.global.validator;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.util.StringUtils.trimAllWhitespace;

@NoArgsConstructor(access = PRIVATE)
public class StringValidator {

    public static void validateMaxLengthWithoutWhiteSpace(String validField, int maxLength, String fieldName) {

        if (validField == null) {
            return;
        }

        if (trimAllWhitespace(validField).length() > maxLength) {
            throw new BadRequestException(fieldName + " length must less than " + maxLength);
        }
    }
}
