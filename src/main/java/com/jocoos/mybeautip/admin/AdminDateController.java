package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class AdminDateController {
    protected static final SimpleDateFormat RECOMMENDED_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ");

    protected Date getUTCDate(String date) {
        try {
            return RECOMMENDED_DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            log.error("invalid recommended date format", e);
            throw new BadRequestException("invalid date format", e.getMessage() + " - " + date);
        }
    }
}
