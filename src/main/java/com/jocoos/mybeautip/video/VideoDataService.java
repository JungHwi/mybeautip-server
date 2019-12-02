package com.jocoos.mybeautip.video;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VideoDataService {

  private final ObjectMapper objectMapper;

  public VideoDataService(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public Map<String, String> getData(String json) {
    try {
      return objectMapper.readValue(json, HashMap.class);
    } catch (IOException e) {
      log.error("{}", json);
      log.error("JSON Error", e);
      return null;
    }
  }

  public List<Integer> getCategory(String json) {
    Map<String, String> map = getData(json);
    try {
      Integer[] categoryData =  objectMapper.readValue(map.get("video_category"), Integer[].class);
      List<Integer> category = Arrays.asList(categoryData);
      log.info("video category: {}", category);

      return category;
    } catch (IOException e) {
      log.error("{}", map.get("video_category"));
      log.error("JSON data Error", e);
      return null;
    }
  }
}
