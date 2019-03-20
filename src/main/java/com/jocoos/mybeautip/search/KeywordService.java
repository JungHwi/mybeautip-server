package com.jocoos.mybeautip.search;

import javax.transaction.Transactional;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.member.Member;

@Slf4j
@Service
public class KeywordService {
  
  public enum KeywordCategory {MEMBER, VIDEO, GOODS, POST}
  
  private final SearchHistoryRepository searchHistoryRepository;
  private final KeywordRepository keywordRepository;
  
  public KeywordService(SearchHistoryRepository searchHistoryRepository,
                        KeywordRepository keywordRepository) {
    this.searchHistoryRepository = searchHistoryRepository;
    this.keywordRepository = keywordRepository;
  }
  
  @Transactional
  public synchronized void updateKeywordCount(String keyword) {
    Optional<Keyword> optional = keywordRepository.findByKeyword(keyword);
    if (optional.isPresent()) {
      keywordRepository.updateCount(optional.get().getId(), 1);
    } else {
      keywordRepository.save(new Keyword(keyword));
    }
  }
  
  public synchronized void logHistory(String keyword, KeywordCategory category, Member member) {
    Optional<SearchHistory> optional = searchHistoryRepository.findByKeywordAndCategoryAndCreatedBy(
        keyword, category.ordinal(), member);
    
    SearchHistory history;
    if (optional.isPresent()) {
      history = optional.get();
      history.setCount(history.getCount() + 1);
      history.setCategory(category.ordinal());
    } else {
      history = new SearchHistory(keyword, category.ordinal(), member);
    }
    searchHistoryRepository.save(history);
  }
}
