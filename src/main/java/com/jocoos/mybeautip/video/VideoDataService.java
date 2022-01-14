package com.jocoos.mybeautip.video;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VideoDataService {

  private final ObjectMapper objectMapper;

  public VideoDataService(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public HashMap<String, Object> getData(String json) {
    try {
      return objectMapper.readValue(json, new TypeReference<HashMap<String, Object>>() {});
    } catch (IOException e) {
      log.error("{}", json);
      log.error("JSON Error", e);
      return null;
    }
  }

  public List<Integer> getCategory(String json) {
    Map<String, Object> map = getData(json);
    try {
      Integer[] categoryData = objectMapper.readValue(String.valueOf(map.get("video_category")), Integer[].class);
      List<Integer> category = Arrays.asList(categoryData);
      log.info("video category: {}", category);

      return category;
    } catch (IOException e) {
      log.error("{}", map.get("video_category"));
      log.error("JSON data Error", e);
      return null;
    }
  }

  public VideoExtraData getDataObject(String json) {
    try {
      return objectMapper.readValue(json, VideoExtraData.class);
    } catch (IOException e) {
      log.error("{}", json);
      log.error("JSON Error", e);
      return null;
    }
  }
}
