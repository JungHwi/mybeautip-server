package com.jocoos.mybeautip.godo;

import com.jocoos.mybeautip.goods.Category;
import com.jocoos.mybeautip.goods.CategoryRepository;
import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class GodoService {
  
  @Autowired
  private RestTemplate restTemplate;
  
  @Autowired
  private CategoryRepository categoryRepository;
  
  @Autowired
  private GoodsRepository goodsRepository;
  
  @Value("${godomall.base-url}")
  private String baseUrl;
  
  @Value("${godomall.partner-key}")
  private String partnerKey;
  
  @Value("${godomall.key}")
  private String key;
  
  @Value("${godomall.category-search-url}")
  private String categorySearchUrl;
  
  @Value("${godomall.goods-search-url}")
  private String goodsSearchUrl;
  
  private static final String GODOMALL_RESPONSE_OK = "000";
  private static final String GOODS_CATEGORY_TOP = "0";
  
  private static int newCount;
  private static int updatedCount;
  
  @Scheduled(initialDelay = 30000, fixedRate = 86400000)  // 30 sec, 1 day
  public void gatheringCategoriesFromGodomall() {
    log.debug("GatheringCategoriesFromGodomall task started...");
    getCategoriesFromGodo();
    log.debug("GatheringCategoriesFromGodomall task ended");
  }
  
  @Scheduled(initialDelay = 60000, fixedRate = 3600000) // 60 sec, 1 hour
  public void gatheringGoodsFromGodomall() {
    log.debug("GatheringGoodsFromGodomall task started...");
    getGoodsFromGodo();
    log.debug("GatheringGoodsFromGodomall task ended");
  }
  
  /**
   * Retrieve All categories using GodoMall Open API
   */
  public synchronized void getCategoriesFromGodo() {
    long start = System.currentTimeMillis();
    
    List<GodoCategoryResponse.CategoryData> categories = getCategoriesWithCode("");
    
    // Retrieve Sub categories
    if (categories != null) {
      for (GodoCategoryResponse.CategoryData category : categories) {
        getCategoriesWithCode(category.getCateCd());
      }
    }
    
    log.debug(String.format("Elapsed: %d miliseconds", (System.currentTimeMillis() - start)));
  }
  
  private List<GodoCategoryResponse.CategoryData> getCategoriesWithCode(String code) {
    StringBuilder requestUrl = new StringBuilder();
    requestUrl.append(baseUrl).append(categorySearchUrl)
        .append("partner_key=").append(partnerKey)
        .append("&key=").append(key)
        .append("&cateCd=").append(code);
    
    HttpHeaders headers = new HttpHeaders();
    HttpEntity<String> request = new HttpEntity<>(headers);
    ResponseEntity<GodoCategoryResponse> response = restTemplate.exchange(requestUrl.toString(),
        HttpMethod.GET, request, GodoCategoryResponse.class);
    
    if (GODOMALL_RESPONSE_OK.equals(response.getBody().getHeader().getCode())) {
      List<GodoCategoryResponse.CategoryData> dataList = response.getBody().getBody();
      
      for (GodoCategoryResponse.CategoryData categoryData : dataList) {
        Category category = copyPropertiesFrom(categoryData);
        categoryRepository.save(category);
      }
      return dataList;
    }
    return null;
  }
  
  private Category copyPropertiesFrom(GodoCategoryResponse.CategoryData source) {
    Category target = new Category();
    
    if (source.getCateCd().length() == 3) {  // top category, FIXME: Use contant value
      target.setGroup(GOODS_CATEGORY_TOP);
    } else if (source.getCateCd().length() == 6) {  // sub category, FIXME: Use contant value
      target.setGroup(source.getCateCd().substring(0, 3));
    } else {
      log.warn("Category code is invalid: " + source.getCateCd());
    }
    
    target.setCode(source.getCateCd());
    target.setName(source.getCateNm());
    target.setDisplayOnPc(source.getCateDisplayFl());
    target.setDisplayOnMobile(source.getCateDisplayMobileFl());
    
    return target;
  }
  
  /**
   * Retrieve All goods using GodoMall Open API
   */
  public synchronized void getGoodsFromGodo() {
    long start = System.currentTimeMillis();
    int nextPage = 1;
    int maxPage;
    int nowPage;
    
    GodoGoodsResponse.Header header;
    
    do {
      header = getGoodsFromGodo(nextPage);
      maxPage = header.getMax_page();
      nowPage = header.getNow_page();
      nextPage = nowPage + 1;
    } while (maxPage > nowPage);
    
    log.debug(String.format("Elapsed: %d miliseconds, new goods items: %d, updated goods items: %d",
        (System.currentTimeMillis() - start), newCount, updatedCount));
    newCount = updatedCount = 0;
  }
  
  private GodoGoodsResponse.Header getGoodsFromGodo(int nextPage) {
    StringBuilder requestUrl = new StringBuilder();
    requestUrl.append(baseUrl).append(goodsSearchUrl)
        .append("partner_key=").append(partnerKey)
        .append("&key=").append(key)
        .append("&page=").append(nextPage)
        .append("&size=25");
    
    HttpHeaders headers = new HttpHeaders();
    HttpEntity<String> request = new HttpEntity<>(headers);
    ResponseEntity<GodoGoodsResponse> response = restTemplate.exchange(requestUrl.toString(),
        HttpMethod.GET, request, GodoGoodsResponse.class);
    
    if (GODOMALL_RESPONSE_OK.equals(response.getBody().getHeader().getCode())) {
      List<GodoGoodsResponse.GoodsData> dataList = response.getBody().getBody();
      Optional<Goods> optional;
      Goods goods;
      
      for (GodoGoodsResponse.GoodsData goodsData : dataList) {
        goods = new Goods();
        BeanUtils.copyProperties(goodsData, goods);
        optional = goodsRepository.findById(goods.getGoodsNo());
        if (optional.isPresent()) {  // Already exist
          goods.setUpdatedAt(System.currentTimeMillis());
          updatedCount++;
        } else {  // New item
          goods.setCreatedAt(System.currentTimeMillis());
          goods.setUpdatedAt(System.currentTimeMillis());
          newCount++;
        }
        goodsRepository.save(goods);
      }
      return response.getBody().getHeader();
    }
    return null;
  }
}