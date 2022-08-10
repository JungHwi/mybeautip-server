package com.jocoos.mybeautip.global.config.web.formatter;

import com.jocoos.mybeautip.global.util.ZonedDateTimeUtil;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Locale;

public class StringToZonedDatedTime implements Formatter<ZonedDateTime> {


    @Override
    public ZonedDateTime parse(String text, Locale locale) throws ParseException {
        return ZonedDateTimeUtil.toZonedDateTime(text);
    }

    @Override
    public String print(ZonedDateTime object, Locale locale) {
        return ZonedDateTimeUtil.toString(object);
    }
}
