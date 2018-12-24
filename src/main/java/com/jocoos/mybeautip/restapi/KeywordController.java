package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.recommendation.KeywordRecommendation;
import com.jocoos.mybeautip.recommendation.KeywordRecommendationRepository;
import com.jocoos.mybeautip.search.SearchHistory;
import com.jocoos.mybeautip.search.SearchHistoryRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/keywords", produces = MediaType.APPLICATION_JSON_VALUE)
public class KeywordController {
  
  private final MemberService memberService;
  private final SearchHistoryRepository searchHistoryRepository;
  private final KeywordRecommendationRepository keywordRecommendationRepository;
  
  public KeywordController(MemberService memberService,
                           SearchHistoryRepository searchHistoryRepository,
                           KeywordRecommendationRepository keywordRecommendationRepository) {
    this.memberService = memberService;
    this.searchHistoryRepository = searchHistoryRepository;
    this.keywordRecommendationRepository = keywordRecommendationRepository;
  }
  
  @GetMapping
  public List<KeywordInfo> getKeywords(@RequestParam(defaultValue = "20") int count,
                          @RequestParam(defaultValue = "me") String scope,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(defaultValue = "start") String method) {
  
    count = validateRequestAndGetValidCount(count, scope, keyword, method);
    PageRequest page = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "modifiedAt"));
    
    switch (scope) {
      case "me":
        if (StringUtils.isBlank(keyword)) {
          return getMyKeywords(page);
        } else {
          return searchMyKeywords(keyword, method, page);
        }
      case "all":
        if (StringUtils.isBlank(keyword)) {
          return getAllKeywords(page);
        } else {
          return searchAllKeywords(keyword, method, page);
        }
      case "recommended":
        if (StringUtils.isBlank(keyword)) {
          return getRecommendedKeywords(page);
        } else {
          return searchRecommendedKeywords(keyword, method, page);
        }
      default:
        break;
    }
    
    return new ArrayList<>();
  }
  
  private List<KeywordInfo> getMyKeywords(Pageable page) {
    List<SearchHistory> list = searchHistoryRepository.findByCreatedBy(memberService.currentMember(), page);
    List<KeywordInfo> info = new ArrayList<>();
    for (SearchHistory history : list) {
      info.add(new KeywordInfo(history));
    }
    return info;
  }
  
  private List<KeywordInfo> searchMyKeywords(String keyword, String method, Pageable page) {
    List<SearchHistory> list;
    if ("contain".equals(method)) {
      list = searchHistoryRepository.findByCreatedByAndKeywordContaining(memberService.currentMember(), keyword, page);
    } else {
      list = searchHistoryRepository.findByCreatedByAndKeywordStartingWith(memberService.currentMember(), keyword, page);
    }
    List<KeywordInfo> info = new ArrayList<>();
    for (SearchHistory item : list) {
      info.add(new KeywordInfo(item));
    }
    return info;
  }
  
  private List<KeywordInfo> getAllKeywords(Pageable page) {
    Page<SearchHistory> list = searchHistoryRepository.findAll(page);
    List<KeywordInfo> info = new ArrayList<>();
    for (SearchHistory item : list) {
      info.add(new KeywordInfo(item));
    }
    return info;
  }
  
  private List<KeywordInfo> searchAllKeywords(String keyword, String method, Pageable page) {
    List<SearchHistory> list;
    if ("contain".equals(method)) {
      list = searchHistoryRepository.findByKeywordContaining(keyword, page);
    } else {
      list = searchHistoryRepository.findByKeywordStartingWith(keyword, page);
    }
    List<KeywordInfo> info = new ArrayList<>();
    for (SearchHistory item : list) {
      info.add(new KeywordInfo(item));
    }
    return info;
  }
  
  private List<KeywordInfo> getRecommendedKeywords(Pageable page) {
    Page<KeywordRecommendation> list = keywordRecommendationRepository.findAll(page);
    List<KeywordInfo> info = new ArrayList<>();
    for (KeywordRecommendation item : list) {
      info.add(new KeywordInfo(item));
    }
    return info;
  }
  
  private List<KeywordInfo> searchRecommendedKeywords(String keyword, String method, Pageable page) {
    List<KeywordRecommendation> list;
    if ("contain".equals(method)) {
      list = keywordRecommendationRepository.findByTagNameContaining(keyword, page);
    } else {
      list = keywordRecommendationRepository.findByTagNameStartingWith(keyword, page);
    }
    List<KeywordInfo> info = new ArrayList<>();
    for (KeywordRecommendation item : list) {
      info.add(new KeywordInfo(item));
    }
    return info;
  }
  
  public int validateRequestAndGetValidCount(int count, String scope, String keyword, String method) {
    String[] validScopes = {"me", "all", "recommended"};
    String[] validMethods = {"start", "contain"};

    if (!StringUtils.containsAny(scope, validScopes)) {
      throw new BadRequestException("invalid_scope", "Valid scope is 'me', 'all', or 'recommended'");
    }
  
    if (!StringUtils.containsAny(method, validMethods)) {
      throw new BadRequestException("invalid_method", "Valid method is 'start_with' or 'contain'");
    }
    
    if (keyword != null && keyword.length() > 255) {
      throw new BadRequestException("invalid_keyword", "Valid keyword size is between 1 to 255");
    }
  
    if (count < 0) {
      count = 20;
    }
    if (count > 100) {
      count = 100;
    }
    
    return count;
  }
  
  @Data
  public class KeywordInfo {
    private String name;
    private Integer refCount;
    private Date modifiedAt;
    
    public KeywordInfo(SearchHistory history) {
      this.name = history.getKeyword();
      this.refCount = history.getCount();
      this.modifiedAt = history.getModifiedAt();
    }
    
    public KeywordInfo(KeywordRecommendation recommendation) {
      this.name = recommendation.getTag().getName();
      this.refCount = recommendation.getTag().getRefCount();
      this.modifiedAt = recommendation.getTag().getModifiedAt();
    }
  }
}