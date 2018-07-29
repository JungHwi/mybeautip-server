package com.jocoos.mybeautip.godo;

import com.jocoos.mybeautip.goods.Category;
import com.jocoos.mybeautip.goods.CategoryRepository;
import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.store.Store;
import com.jocoos.mybeautip.store.StoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
public class GodoService {
  private final RestTemplate restTemplate;
  private final CategoryRepository categoryRepository;
  private final GoodsRepository goodsRepository;
  private final StoreRepository storeRepository;

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

  @Value("${godomall.common-code-url}")
  private String commonCodeUrl;

  private static final String GODOMALL_RESPONSE_OK = "000";
  private static final String GOODS_CATEGORY_TOP = "0";

  private static int newCount;
  private static int updatedCount;

  public GodoService(CategoryRepository categoryRepository,
                     GoodsRepository goodsRepository,
                     RestTemplate restTemplate,
                     StoreRepository storeRepository) {
    this.categoryRepository = categoryRepository;
    this.goodsRepository = goodsRepository;
    this.restTemplate = restTemplate;
    this.storeRepository = storeRepository;
  }

  @Scheduled(initialDelay = 30000, fixedRate = 86400000)  // 30 sec, 1 day
  public void gatheringCategoriesFromGodomall() {
    log.debug("GatheringCategoriesFromGodomall task started...");
    getCategoriesFromGodo();
    log.debug("GatheringCategoriesFromGodomall task ended");
  }

  @Scheduled(initialDelay = 45000, fixedRate = 86400000) // 45 sec, 1 day
  public void gatheringScmDataFromGodomall() {
    log.debug("GatheringStoreFromGodomall task started...");
    getStoresFromGodo();
    log.debug("GatheringStoreFromGodomall task ended");
  }

  @Scheduled(initialDelay = 60000, fixedRate = 86400000) // 60 sec, 1 day
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

    log.debug(String.format("GatheringCategoriesFromGodomall elapsed: %d miliseconds",
            (System.currentTimeMillis() - start)));
  }

  private List<GodoCategoryResponse.CategoryData> getCategoriesWithCode(String code) {
    StringBuilder requestUrl = new StringBuilder();
    requestUrl.append(baseUrl).append(categorySearchUrl)
            .append("partner_key=").append(partnerKey)
            .append("&key=").append(key)
            .append("&cateCd=").append(code);

    ResponseEntity<GodoCategoryResponse> response
            = restTemplate.getForEntity(requestUrl.toString(), GodoCategoryResponse.class);

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

    log.debug(String.format("GatheringGoodsFromGodomall elapsed: %d miliseconds, " +
                    "new goods items: %d, updated goods items: %d",
            (System.currentTimeMillis() - start), newCount, updatedCount));
    newCount = updatedCount = 0;
  }

  private GodoGoodsResponse.Header getGoodsFromGodo(int nextPage) {
    StringBuilder requestUrl = new StringBuilder();
    requestUrl.append(baseUrl).append(goodsSearchUrl)
            .append("partner_key=").append(partnerKey)
            .append("&key=").append(key)
            .append("&page=").append(nextPage)
            .append("&size=15");

    ResponseEntity<GodoGoodsResponse> response
            = restTemplate.getForEntity(requestUrl.toString(), GodoGoodsResponse.class);

    if (GODOMALL_RESPONSE_OK.equals(response.getBody().getHeader().getCode())) {
      List<GodoGoodsResponse.GoodsData> dataList = response.getBody().getBody();
      Optional<Goods> optional;
      Goods goods;

      for (GodoGoodsResponse.GoodsData goodsData : dataList) {
        goods = new Goods();
        BeanUtils.copyProperties(goodsData, goods);
        optional = goodsRepository.findById(goods.getGoodsNo());
        if (optional.isPresent()) {  // Already exist
          updatedCount++;
          goods.setCreatedAt(optional.get().getCreatedAt());
          goods.setModifiedAt(new Date());
        } else {  // New item
          newCount++;
          goods.setCreatedAt(new Date());
          goods.setModifiedAt(new Date());
        }
        goodsRepository.save(goods);
      }
      return response.getBody().getHeader();
    }
    return null;
  }

  /**
   * Retrieve All stores(scm) using GodoMall Open API
   */
  public synchronized void getStoresFromGodo() {
    long start = System.currentTimeMillis();
    int newCount = 0;
    int updatedCount = 0;

    final String S3_STORE_PREFIX = "https://s3.ap-northeast-2.amazonaws.com/mybeautip/store/";
    final String S3_STORE_IMG_SUFFIX = ".png";
    final String S3_STORE_THUMBNAIL_SUFFIX = "-thumbnail.png";

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));

    String requestUrl = baseUrl + commonCodeUrl + "partner_key=" + partnerKey + "&key=" + key;

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("code_type", "scm");

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

    ResponseEntity<GodoScmResponse> response = restTemplate.exchange(
            requestUrl, HttpMethod.POST, request, GodoScmResponse.class);

    if (GODOMALL_RESPONSE_OK.equals(response.getBody().getHeader().getCode())) {
      List<GodoScmResponse.CodeData> dataList = response.getBody().getBody();

      for (GodoScmResponse.CodeData scm : dataList) {
        Optional<Store> optional = storeRepository.findById(Long.parseLong(scm.getScmNo()));
        Store store;
        if (optional.isPresent()) {
          store = optional.get();
          updatedCount++;
        } else {
          store = new Store(Long.parseLong(scm.getScmNo()));
          store.setImageUrl(S3_STORE_PREFIX + scm.getScmNo() + S3_STORE_IMG_SUFFIX);
          store.setThumbnailUrl(S3_STORE_PREFIX + scm.getScmNo() + S3_STORE_THUMBNAIL_SUFFIX);
          newCount++;
        }
        store.setName(scm.getCompanyNm());
        storeRepository.save(store);
      }
    }

    log.debug(String.format("GatheringStoreFromGodomall elapsed: %d miliseconds, " +
                    "new store items: %d, updated store items: %d",
            (System.currentTimeMillis() - start), newCount, updatedCount));
  }
}