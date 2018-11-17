package com.jocoos.mybeautip.tag;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.BadRequestException;

@Slf4j
@Service
public class TagService {
  
  private static final String startSign = "#";
  private static final String regex = "^[\\p{L}\\p{N}_]+";  // letters, numbers, underscore(_)
  private static final int MAX_TAG_LENGTH = 25;
  
  private final TagRepository tagRepository;
  private final ObjectMapper objectMapper;
  
  public TagService(TagRepository tagRepository, ObjectMapper objectMapper) {
    this.tagRepository = tagRepository;
    this.objectMapper = objectMapper;
  }
  
  /**
   * Parse TagInfo from the text, and save tags without increasing refCount
   */
  public void parseHashTagsAndToucheRefCount(String text) {
    List<String> tags = getHashTags(text);
    touchRefCount(getUniqueTagNames(tags));
  }
  
  /**
   * Parse TagInfo from the text, and save tags and increase refCount
   */
  @Transactional
  public List<String> getHashTagsAndIncreaseRefCount(String text) {
    List<String> tags = getHashTags(text);
    increaseRefCount(getUniqueTagNames(tags));
    return tags;
  }
  
  @Transactional
  public List<String> getHashTagsAndUpdateRefCount(String oldTagInfo, String text) {
    List<String> oldTags = new ArrayList<>();
    if (oldTagInfo != null) {
      try {
        oldTags = Arrays.asList(objectMapper.readValue(oldTagInfo, String[].class));
      } catch (IOException e) {
        log.warn("cannot read tag info, tags:", oldTagInfo);
      }
    }
    
    List<String> newTags = getHashTags(text);
    
    List<String> oldTagNames = getUniqueTagNames(oldTags);
    List<String> newTagNames = getUniqueTagNames(newTags);
    
    List<String> remove = (List<String>) CollectionUtils.removeAll(oldTagNames, newTagNames);
    List<String> add = (List<String>) CollectionUtils.removeAll(newTagNames, oldTagNames);
    
    decreaseRefCount(remove);
    increaseRefCount(add);
    
    return newTags;
  }
  
  @Transactional
  public void decreaseRefCount(String tagsInfo) {
    if (tagsInfo == null) {
      return;
    }
    List<String> tags = new ArrayList<>();
    try {
      tags = Arrays.asList(objectMapper.readValue(tagsInfo, String[].class));
    } catch (IOException e) {
      log.warn("cannot read tag info, tags:", tagsInfo);
    }
    decreaseRefCount(getUniqueTagNames(tags));
  }
  
  private List<String> getHashTags(String text) {
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
    return tags;
  }
  
  private List<String> getUniqueTagNames(List<String> tags) {
    List<String> uniqueTagNames = new ArrayList<>();
    for (String name : tags) {
      if (!uniqueTagNames.contains(name)) {
        uniqueTagNames.add(name);
      }
    }
    return uniqueTagNames;
  }
  
  private void touchRefCount(List<String> uniqueTagNames) {
    for (String name : uniqueTagNames) {
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
  
  private void increaseRefCount(List<String> uniqueTagNames) {
    for (String name : uniqueTagNames) {
      Optional<Tag> optional = tagRepository.findByName(name);
      if (optional.isPresent()) {
        tagRepository.updateTagRefCount(optional.get().getId(), 1);
      } else {
        tagRepository.save(new Tag(name, 1));
      }
    }
  }
  
  private void decreaseRefCount(List<String> uniqueTagNames) {
    for (String name : uniqueTagNames) {
      tagRepository.findByName(name)
          .ifPresent(tag -> tagRepository.updateTagRefCount(tag.getId(), -1));
    }
  }
}