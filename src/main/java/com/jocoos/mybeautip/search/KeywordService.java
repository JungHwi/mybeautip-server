package com.jocoos.mybeautip.search;

import com.jocoos.mybeautip.member.Member;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

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
  
  @Synchronized
  @Transactional
  public void updateKeywordCount(String keyword) {
    Optional<Keyword> optional = keywordRepository.findByKeyword(keyword);
    if (optional.isPresent()) {
      keywordRepository.updateCount(optional.get().getId(), 1);
    } else {
      keywordRepository.save(new Keyword(keyword));
    }
  }
  
  @Synchronized
  @Transactional
  public void logHistory(String keyword, KeywordCategory category, Member member) {
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