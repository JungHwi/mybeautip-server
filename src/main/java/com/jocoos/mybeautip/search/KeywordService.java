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
    if (optionalSearchHistory.isPresent()) {
      history = optionalSearchHistory.get();
      history.setCategory(category.ordinal());
      history.setCount(history.getCount() + 1);
      searchHistoryRepository.save(history);
    } else {
      searchHistoryRepository.save(new SearchHistory(keyword, category.ordinal(), member));
    }
    
    Optional<Keyword> optionalKeyword = keywordRepository.findByKeyword(keyword);
    if (optionalKeyword.isPresent()) {
      keywordRepository.updateCount(optionalKeyword.get().getId(), 1);
    } else {
      keywordRepository.save(new Keyword(keyword));
    }
  }
}