package com.jocoos.mybeautip.global.constant;

import java.time.format.DateTimeFormatter;

public class LocalDateTimeConstant {

    public static final String SIMPLE_LOCAL_DATE_FORMAT = "yyyyMMdd";
    public static final String LOCAL_DATE_FORMAT = "yyyy-MM-dd";
    public static final String SIMPLE_LOCAL_DATE_TIME_FORMAT = "yyyyMMddHHmmss";
    public static final String LOCAL_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String ZONE_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss Z";
    public static final String ZONE_DATE_TIME_MILLI_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS Z";
    public static final String FFL_ZONE_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static final DateTimeFormatter ZONE_DATE_TIME_MILLI_FORMATTER = DateTimeFormatter.ofPattern(ZONE_DATE_TIME_MILLI_FORMAT);
}
