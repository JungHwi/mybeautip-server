package com.jocoos.mybeautip.global.config.web.formatter;

import com.jocoos.mybeautip.global.util.date.ZonedDateTimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Locale;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_MILLI_FORMAT;
import static com.jocoos.mybeautip.global.util.date.ZonedDateTimeUtil.isZonedDateTime;

public class StringToZonedDatedTime implements Formatter<ZonedDateTime> {


    @Override
    public ZonedDateTime parse(String text, Locale locale) throws ParseException {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        if (isZonedDateTime(text)) {
            return ZonedDateTimeUtil.toZonedDateTime(text);
        } else if (isZonedDateTime(text, ZONE_DATE_TIME_MILLI_FORMAT)) {
            return ZonedDateTimeUtil.toZonedDateTime(text, ZONE_DATE_TIME_MILLI_FORMAT);
        }

        return null;
    }

    @Override
    public String print(ZonedDateTime object, Locale locale) {
        return ZonedDateTimeUtil.toString(object);
    }
}
