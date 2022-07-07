package com.jocoos.mybeautip;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.jocoos.mybeautip.config.ApplicationConfig;
import com.jocoos.mybeautip.restapi.VideoController;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ApplicationConfig.class})
public class GatlingGuestTest {

  private static final String host = "mybeautip-dev.jocoos.com";
  private static final int GUEST_COUNT = 100;
  private static final Long videoId = 14396L;
  private static final int RANDOM_BOUND = 5;
  private static ObjectMapper objectMapper;
  private static Random random;

  @BeforeClass
  public static void beforeClass() {
    objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

    random = new Random();
    random.setSeed(System.currentTimeMillis());
  }

  @Autowired
  private RestTemplate restTemplate;

  @Test
  public void gatlingWatchCount() throws InterruptedException {
    UriComponents watchUri = UriComponentsBuilder.newInstance()
        .scheme("https")
        .host(host)
        .path(String.format("/api/1/videos/%d/watches", videoId)).build(true);

    System.out.println(watchUri.toString());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

    for (int i = 0; i < GUEST_COUNT; i++) {
      int r = random.nextInt(RANDOM_BOUND);
      System.out.println(String.format("waiting for %d secs..", r));

      Thread.sleep(r * 1000);

      headers.set(HttpHeaders.AUTHORIZATION, getAeccessToken());

      LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

      ResponseEntity<String> response = restTemplate.postForEntity(watchUri.toString(), request, String.class);

      VideoController.VideoInfo video = getVideoInfo(response.getBody());
      System.out.println(String.format("video_id: %s, watch_count: %s", video.getId(), video.getWatchCount()));
    }
  }

  @Test
  public void gatlingViewCount() throws InterruptedException {
    UriComponents watchUri = UriComponentsBuilder.newInstance()
        .scheme("https")
        .host(host)
        .path(String.format("/api/1/videos/%d/view_count", videoId)).build(true);

    System.out.println(watchUri.toString());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

    for (int i = 0; i < GUEST_COUNT; i++) {
      int r = random.nextInt(RANDOM_BOUND);
      System.out.println(String.format("waiting for %d secs..", r));

      Thread.sleep(r * 1000);
      headers.set(HttpHeaders.AUTHORIZATION, getAeccessToken());

      LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

      ResponseEntity<String> response = restTemplate.postForEntity(watchUri.toString(), request, String.class);
//      System.out.println(response.getBody());

      VideoController.VideoInfo video = getVideoInfo(response.getBody());
      System.out.println(String.format("video_id: %s, view_count: %s", video.getId(), video.getViewCount()));
    }
  }

  private VideoController.VideoInfo getVideoInfo(String body) {
    try {
      return objectMapper.readValue(body, VideoController.VideoInfo.class);
    } catch (IOException e) {
      System.out.println("error " + e);
      return null;
    }
  }


  private String getAeccessToken() {

    UriComponents uri = UriComponentsBuilder.newInstance()
        .scheme("https")
        .host(host)
        .path("/api/1/token").build(true);

//    System.out.println(uri.toString());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

    headers.set(HttpHeaders.AUTHORIZATION, "Basic bXliZWF1dGlwLWlvczpha2RscWJ4bHFka2RsZGhkcHRt");

    LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "client");

    HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

    ResponseEntity<String> response = restTemplate.postForEntity(uri.toString(), request, String.class);

    try {
      AccessToken token = objectMapper.readValue(response.getBody(), AccessToken.class);
//      System.out.println(token.getAccessToken());
      return String.format("Bearer %s", token.accessToken);
    } catch (IOException e) {
      System.out.println("error " + e);
      return null;
    }
  }

  @NoArgsConstructor
  @Data
  static class AccessToken {
    private String accessToken;
  }

//  @NoArgsConstructor
//  @Data
//  static class VideoInfo {
//    private Long id;
//    private Long watchCount;
//    private Long viewCount;
//  }

}
