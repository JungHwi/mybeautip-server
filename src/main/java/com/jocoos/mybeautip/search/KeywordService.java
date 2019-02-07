package com.jocoos.mybeautip.search;

import com.jocoos.mybeautip.member.Member;
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
  
  @Transactional
  public void logHistoryAndUpdateStats(String keyword, KeywordCategory category, Member member) {
    Optional<SearchHistory> optionalSearchHistory = searchHistoryRepository.findByKeywordAndCategoryAndCreatedBy(
        keyword, category.ordinal(), member);
    SearchHistory history;
    if (optionalSearchHistory.isPresent()) {  // update exist item
      history = optionalSearchHistory.get();
      history.setCategory(category.ordinal());
      history.setCount(history.getCount() + 1);
    } else {
      history = new SearchHistory(keyword, category.ordinal(), member);
    }
    searchHistoryRepository.save(history);
  
    Optional<Keyword> optionalKeyword = keywordRepository.findByKeyword(keyword);
    Keyword item;
    if (optionalKeyword.isPresent()) {
      item = optionalKeyword.get();
      item.setCount(item.getCount() + 1);
    } else {
      item = new Keyword(keyword);
    }
    keywordRepository.save(item);
  }
}