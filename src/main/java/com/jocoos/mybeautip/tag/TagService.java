package com.jocoos.mybeautip.tag;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;

@Slf4j
@Service
public class TagService {
  // Tag category
  public static final int TAG_MEMBER = 1;
  public static final int TAG_VIDEO = 2;
  public static final int TAG_POST = 3;
  public static final int TAG_BANNER = 4;
  public static final int TAG_COMMENT = 5;
  
  private static final String startSign = "#";
  private static final String regex = "^[\\p{L}\\p{N}_]+";  // letters, numbers, underscore(_)
  private static final int MAX_TAG_LENGTH = 25;
  
  private final TagRepository tagRepository;
  private final TagHistoryRepository tagHistoryRepository;
  private final ObjectMapper objectMapper;
  
  public TagService(TagRepository tagRepository,
                    TagHistoryRepository tagHistoryRepository,
                    ObjectMapper objectMapper) {
    this.tagRepository = tagRepository;
    this.tagHistoryRepository = tagHistoryRepository;
    this.objectMapper = objectMapper;
  }
  
  // Save tags without increasing refCount
  @Transactional
  public void touchRefCount(String text) {
    List<String> tags = parseHashTag(text);
    for (String name : tags) {
      Optional<Tag> optional = tagRepository.findByName(name);
      if (optional.isPresent()) {
        Tag tag = optional.get();
        tag.setModifiedAt(new Date());
        tagRepository.save(tag);
      } else {
        tagRepository.save(new Tag(name, 0));
      }
    }
  }
  
  @Transactional
  public void increaseRefCount(String text) {
    List<String> tags = parseHashTag(text);
    for (String name : tags) {
      Optional<Tag> optional = tagRepository.findByName(name);
      if (optional.isPresent()) {
        tagRepository.updateTagRefCount(optional.get().getId(), 1);
      } else {
        tagRepository.save(new Tag(name, 1));
      }
    }
  }
  
  @Transactional
  public void decreaseRefCount(String text) {
    List<String> tags = parseHashTag(text);
    for (String name : tags) {
      tagRepository.findByName(name)
          .ifPresent(tag -> tagRepository.updateTagRefCount(tag.getId(), -1));
    }
  }
  
  @Transactional
  public void updateRefCount(String oldText, String newText) {
    decreaseRefCount(oldText);
    increaseRefCount(newText);
  }
  
  @Transactional
  public void addHistory(String text, int category, long resourceId, Member me) {
    List<String> uniqueTagNames = parseHashTag(text);
    for (String name : uniqueTagNames) {
      Tag tag = tagRepository.findByName(name).orElse(new Tag(name, 1));
      
      tagHistoryRepository.findByTagAndCategoryAndResourceIdAndCreatedBy(tag, category, resourceId, me)
          .orElseGet(() -> tagHistoryRepository.save(new TagHistory(tag, category, resourceId, me)));
    }
  }
  
  @Transactional
  public void removeHistory(String text, int category, long resourceId, Member me) {
    List<String> uniqueTagNames = parseHashTag(text);
    for (String name : uniqueTagNames) {
      tagRepository.findByName(name)
          .ifPresent(tag -> tagHistoryRepository.findByTagAndCategoryAndResourceIdAndCreatedBy(tag, category, resourceId, me)
              .ifPresent(tagHistoryRepository::delete));
    }
  }
  
  @Transactional
  public void removeAllHistory(Member me) {
    tagHistoryRepository.findByCreatedBy(me).forEach(tagHistoryRepository::delete);
  }
  
  @Transactional
  public void updateHistory(String oldText, String newText, int category, long resourceId, Member me) {
    removeHistory(oldText, category, resourceId, me);
    addHistory(newText, category, resourceId, me);
  }
  
  private List<String> parseHashTag(String text) {
    List<String> tags = new ArrayList<>();
    String delimiters = " \n\r\t"; // space, new line, tab
    StringTokenizer tokenizer = new StringTokenizer(text, delimiters);
    
    int offset = 0;
    String temp = text;
    
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      if (!token.startsWith(startSign) || StringUtils.countMatches(token, startSign) > 1) {
        continue;
      }
      token = StringUtils.substringAfter(token, startSign);
      
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(token);
      
      if (matcher.find()) {
        String tag = matcher.group();
        if (tag.length() > MAX_TAG_LENGTH) {
          throw new BadRequestException("invalid_tag", "Valid tag length is between 1 to 25: " + tag);
        }
        
        if (StringUtils.removeAll(tag, "_").length() > 0) {
          offset = offset + temp.indexOf(startSign + tag) + token.length();
          temp = text.substring(offset);
          tags.add(tag);
        }
      }
    }
    
    List<String> uniqueTagNames = new ArrayList<>();
    for (String name : tags) {
      if (!uniqueTagNames.contains(name)) {
        uniqueTagNames.add(name);
      }
    }
    return uniqueTagNames;
  }
}