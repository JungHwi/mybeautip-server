package com.jocoos.mybeautip.admin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;

@Slf4j
public class Dates {
  private static final SimpleDateFormat RECOMMENDED_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ");

  public static Date getRecommendedDate(String date) {
    try {
      return RECOMMENDED_DATE_FORMAT.parse(date);
    } catch (ParseException e) {
      log.error("invalid recommended date format", e);
      throw new BadRequestException("invalid date format", e.getMessage() + " - " + date);
    }
  }
}
