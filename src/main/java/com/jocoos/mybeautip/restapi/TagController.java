package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.recommendation.KeywordRecommendation;
import com.jocoos.mybeautip.recommendation.KeywordRecommendationRepository;
import com.jocoos.mybeautip.tag.TagHistory;
import com.jocoos.mybeautip.tag.TagHistoryRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/tags", produces = MediaType.APPLICATION_JSON_VALUE)
public class TagController {
  
  private final MemberService memberService;
  private final TagHistoryRepository tagHistoryRepository;
  private final KeywordRecommendationRepository keywordRecommendationRepository;
  
  public TagController(MemberService memberService,
                       TagHistoryRepository tagHistoryRepository,
                       KeywordRecommendationRepository keywordRecommendationRepository) {
    this.memberService = memberService;
    this.tagHistoryRepository = tagHistoryRepository;
    this.keywordRecommendationRepository = keywordRecommendationRepository;
  }
  
  @GetMapping
  public List<TagInfo> getTags(@RequestParam(defaultValue = "20") int count,
                          @RequestParam(defaultValue = "me") String scope,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(defaultValue = "start") String method) {
  
    count = validateRequestAndGetValidCount(count, scope, keyword, method);
    PageRequest page = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "modifiedAt"));
    Member me = memberService.currentMember();
    
    switch (scope) {
      case "me":
        if (StringUtils.isBlank(keyword)) {
          return getMyTags(me, page);
        } else {
          return searchMyTags(me, keyword, method, page);
        }
      case "all":
        if (StringUtils.isBlank(keyword)) {
          return getAllTags(page);
        } else {
          return searchAllTags(keyword, method, page);
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
  
  private List<TagInfo> getMyTags(Member me, Pageable page) {
    List<TagHistory> list = tagHistoryRepository.findByCreatedBy(me, page);
    return getDistinctTagInfoList(list);
  }
  
  private List<TagInfo> searchMyTags(Member me, String keyword, String method, Pageable page) {
    List<TagHistory> list;
    if ("contain".equals(method)) {
      list = tagHistoryRepository.findByCreatedByAndTagNameContaining(me, keyword, page);
    } else {
      list = tagHistoryRepository.findByCreatedByAndTagNameStartingWith(me, keyword, page);
    }
    return getDistinctTagInfoList(list);
  }
  
  private List<TagInfo> getAllTags(Pageable page) {
    Page<TagHistory> list = tagHistoryRepository.findAll(page);
    return getDistinctTagInfoList(list.getContent());
  }
  
  private List<TagInfo> searchAllTags(String keyword, String method, Pageable page) {
    List<TagHistory> list;
    if ("contain".equals(method)) {
      list = tagHistoryRepository.findByTagNameContaining(keyword, page);
    } else {
      list = tagHistoryRepository.findByTagNameStartingWith(keyword, page);
    }
    return getDistinctTagInfoList(list);
  }
  
  private List<TagInfo> getDistinctTagInfoList(List<TagHistory> list) {
    Map<String, TagHistory> map = new HashMap<>();
    for (TagHistory history : list) {
      if (!map.containsKey(history.getTag().getName())) {
        map.put(history.getTag().getName(), history);
      }
    }
    List<TagInfo> info = new ArrayList<>();
    for (TagHistory item : map.values()) {
      info.add(new TagInfo(item));
    }
    return info;
  }
  
  private List<TagInfo> getRecommendedKeywords(Pageable page) {
    Page<KeywordRecommendation> list = keywordRecommendationRepository.findAll(page);
    List<TagInfo> info = new ArrayList<>();
    for (KeywordRecommendation item : list) {
      info.add(new TagInfo(item));
    }
    return info;
  }
  
  private List<TagInfo> searchRecommendedKeywords(String keyword, String method, Pageable page) {
    List<KeywordRecommendation> list;
    if ("contain".equals(method)) {
      list = keywordRecommendationRepository.findByTagNameContaining(keyword, page);
    } else {
      list = keywordRecommendationRepository.findByTagNameStartingWith(keyword, page);
    }
    List<TagInfo> info = new ArrayList<>();
    for (KeywordRecommendation item : list) {
      info.add(new TagInfo(item));
    }
    return info;
  }
  
  private int validateRequestAndGetValidCount(int count, String scope, String keyword, String method) {
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
  public static class TagInfo {
    private String name;
    private Integer refCount;
    private Date modifiedAt;
    
    public TagInfo(TagHistory history) {
      this.name = history.getTag().getName();
      this.refCount = history.getTag().getRefCount();
      this.modifiedAt = history.getModifiedAt();
    }
    
    public TagInfo(KeywordRecommendation recommendation) {
      this.name = recommendation.getTag().getName();
      this.refCount = recommendation.getTag().getRefCount();
      this.modifiedAt = recommendation.getTag().getModifiedAt();
    }
  }
}