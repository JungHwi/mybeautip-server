package com.jocoos.mybeautip.global.config.restdoc.util;

import org.springframework.restdocs.snippet.Attributes;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;
import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_MILLI_FORMAT;
import static org.springframework.restdocs.snippet.Attributes.key;

public class DocumentAttributeGenerator {
    private static final String KEY_DEFAULT = "default";
    private static final String KEY_FORMAT = "format";

    public static Attributes.Attribute getDefault(Object defaultValue) {
        return key(KEY_DEFAULT).value(defaultValue);
    }

    public static Attributes.Attribute getDefault(boolean defaultValue) {
        return key(KEY_DEFAULT).value(defaultValue);
    }

    public static Attributes.Attribute getZonedDateFormat() {
        return key(KEY_FORMAT).value(ZONE_DATE_TIME_FORMAT);
    }

    public static Attributes.Attribute getZonedDateMilliFormat() {
        return key(KEY_FORMAT).value(ZONE_DATE_TIME_MILLI_FORMAT);
    }

    public static Attributes.Attribute getLocalDateFormat() {
        return key(KEY_FORMAT).value("yyyy-MM-dd");
    }
}
