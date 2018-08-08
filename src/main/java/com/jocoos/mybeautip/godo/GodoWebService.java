package com.jocoos.mybeautip.godo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GodoWebService {

  @Value("${godomall.goods-view-url}")
  private String goodsViewUrl;

  private final RestTemplate restTemplate;

  public GodoWebService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public String getGoodsViewPage(String goodsNo) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(goodsViewUrl);
    builder.queryParam("goodsNo", goodsNo);

    ResponseEntity<String> responseEntity = restTemplate.getForEntity(builder.toUriString(), String.class);

    log.debug("{}", responseEntity.getBody());
    return responseEntity.getBody();
  }

}
