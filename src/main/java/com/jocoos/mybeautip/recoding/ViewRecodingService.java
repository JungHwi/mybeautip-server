package com.jocoos.mybeautip.recoding;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;

@Slf4j
@Service
public class ViewRecodingService {

  private static final int DAY_IN_MS = 1000 * 60 * 60 * 24;
  private static final int MAX_COUNT = 200;

  private final ViewRecodingRepository viewRecodingRepository;

  public ViewRecodingService(ViewRecodingRepository viewRecodingRepository) {
    this.viewRecodingRepository = viewRecodingRepository;
  }

  public Slice<ViewRecoding> findByWeekAgo(Long memberId, int count, String cursor, Integer category) {
    if (count > MAX_COUNT) {
      throw new BadRequestException("The count must be less or equals to 200");
    }

    PageRequest page = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "modifiedAt"));

    Date now;
    if (!Strings.isNullOrEmpty(cursor)) {
      now = new Date(Long.parseLong(cursor));
    } else  {
      now = new Date();
    }

    Date weekAgo = new Date(now.getTime() - 7 * DAY_IN_MS);
    if (category == null) {
      return viewRecodingRepository.findByCreatedByIdAndModifiedAtBeforeAndModifiedAtAfter(memberId, now, weekAgo, page);
    } else {
      return viewRecodingRepository.findByCategoryAndCreatedByIdAndModifiedAtBeforeAndModifiedAtAfter(category, memberId, now, weekAgo, page);
    }
  }
  
  @Transactional
  public void insertOrUpdate(String itemId, int category) {
    viewRecodingRepository.findByItemIdAndCategory(itemId, category)
        .map(recoding -> {
          recoding.setViewCount(recoding.getViewCount() + 1);
          viewRecodingRepository.saveAndFlush(recoding);
          return Optional.empty();
        })
        .orElseGet(() -> {
          viewRecodingRepository.save(new ViewRecoding(itemId, category));
          return Optional.empty();
        });
  }
}
