package com.jocoos.mybeautip.godo;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import com.jocoos.mybeautip.goods.*;
import com.jocoos.mybeautip.recommendation.GoodsRecommendation;
import com.jocoos.mybeautip.recommendation.GoodsRecommendationRepository;
import com.jocoos.mybeautip.store.Store;
import com.jocoos.mybeautip.store.StoreRepository;

@Service
@Slf4j
public class GodoService {
  private final RestTemplate restTemplate;
  private final CategoryRepository categoryRepository;
  private final GoodsRepository goodsRepository;
  private final GoodsOptionRepository goodsOptionRepository;
  private final StoreRepository storeRepository;
  private final GoodsLikeRepository goodsLikeRepository;
  private final GoodsRecommendationRepository goodsRecommendationRepository;

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

  public GodoService(RestTemplate restTemplate,
                     CategoryRepository categoryRepository,
                     GoodsRepository goodsRepository,
                     GoodsOptionRepository goodsOptionRepository,
                     StoreRepository storeRepository,
                     GoodsLikeRepository goodsLikeRepository,
                     GoodsRecommendationRepository goodsRecommendationRepository) {
    this.restTemplate = restTemplate;
    this.categoryRepository = categoryRepository;
    this.goodsRepository = goodsRepository;
    this.goodsOptionRepository = goodsOptionRepository;
    this.storeRepository = storeRepository;
    this.goodsLikeRepository = goodsLikeRepository;
    this.goodsRecommendationRepository = goodsRecommendationRepository;
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
  public void gatheringGoodsFromGodomall() throws IOException {
    log.debug("GatheringGoodsFromGodomall task started...");
    getGoodsFromGodo();
    log.debug("GatheringGoodsFromGodomall task ended");
  }

  /**
   * Retrieve All categories using GodoMall Open API
   */
  private synchronized void getCategoriesFromGodo() {
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
    String requestUrl = baseUrl.concat(categorySearchUrl)
            .concat("partner_key=").concat(partnerKey)
            .concat("&key=").concat(key)
            .concat("&cateCd=").concat(code);

    ResponseEntity<GodoCategoryResponse> response
            = restTemplate.getForEntity(requestUrl, GodoCategoryResponse.class);

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
    final String CATEGORY_S3_PREFIX = "https://s3.ap-northeast-2.amazonaws.com/mybeautip/category/";
    final String CATEGORY_S3_SUFFIX = ".png";

    if (source.getCateCd().length() == 3) {  // top category,
      target.setGroup(GOODS_CATEGORY_TOP);
    } else if (source.getCateCd().length() == 6) {  // sub category,
      target.setGroup(source.getCateCd().substring(0, 3));
    } else {
      log.warn("Category code is invalid: " + source.getCateCd());
    }

    target.setCode(source.getCateCd());
    target.setName(source.getCateNm());
    target.setDisplayOnPc(source.getCateDisplayFl());
    target.setDisplayOnMobile(source.getCateDisplayMobileFl());
    target.setThumbnailUrl(CATEGORY_S3_PREFIX + source.getCateCd() + CATEGORY_S3_SUFFIX);

    return target;
  }

  /**
   * Retrieve All goods using GodoMall Open API
   */
  private synchronized void getGoodsFromGodo() throws IOException {
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

  private GodoGoodsResponse.Header getGoodsFromGodo(int nextPage) throws IOException {
    String requestUrl = baseUrl.concat(goodsSearchUrl)
      .concat("partner_key=").concat(partnerKey)
      .concat("&key=").concat(key)
      .concat("&page=").concat(String.valueOf(nextPage))
      .concat("&size=15");

    ResponseEntity<GodoGoodsResponse> response
      = restTemplate.getForEntity(requestUrl, GodoGoodsResponse.class);

    if (GODOMALL_RESPONSE_OK.equals(response.getBody().getHeader().getCode())) {
      List<GodoGoodsResponse.GoodsData> dataList = response.getBody().getBody();
      Goods goods;

      for (GodoGoodsResponse.GoodsData goodsData : dataList) {
        Optional<Goods> optional = goodsRepository.findById(goodsData.getGoodsNo());
        goods = optional.orElseGet(Goods::new);
        BeanUtils.copyProperties(goodsData, goods);
        goods.setFixedPrice(goodsData.getFixedPrice().intValue());
        goods.setGoodsPrice(goodsData.getGoodsPrice().intValue());
        goods.setGoodsDiscount(goodsData.getGoodsDiscount().intValue());

        if ("n".equalsIgnoreCase(goodsData.getGoodsDisplayFl())
          || "n".equalsIgnoreCase(goodsData.getGoodsSellFl())) {
          deleteGoods(goods);
          continue;
        }

        if (Strings.isNotEmpty(goodsData.getCateCd())) {
          goods.setAllCd(generateCategoryStr(goodsData.getCateCd()));
        }

        if (Strings.isNotEmpty(goodsData.getAllCateCd())) {
          goods.setAllCd(generateCategoryStr(goodsData.getAllCateCd()));
        }

        List<MustInfo> list = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for (GodoGoodsResponse.GoodsMustInfoData mustInfo : goodsData.getGoodsMustInfoData()) {
          list.add(new MustInfo(mustInfo.getStepData().getInfoTitle(), mustInfo.getStepData().getInfoValue()));
        }
        goods.setGoodsMustInfo(mapper.writeValueAsString(list));
        goodsRepository.save(goods);

        if (goodsData.getOptionData() != null) {
          Optional<GoodsOption> optionalGoodsOption;
          GoodsOption goodsOption;
          for (GodoGoodsResponse.OptionData optionData : goodsData.getOptionData()) {
            optionalGoodsOption = goodsOptionRepository.findById(optionData.getSno());
            goodsOption = optionalGoodsOption.orElseGet(GoodsOption::new);
            BeanUtils.copyProperties(optionData, goodsOption);
            goodsOption.setOptionPrice(optionData.getOptionPrice().intValue());
            goodsOption.setOptionCostPrice(optionData.getOptionCostPrice().intValue());
            goodsOption.setStockCnt(optionData.getStockCnt().intValue());

            if ("y".equalsIgnoreCase(optionData.getOptionViewFl())) {
              goodsOptionRepository.save(goodsOption);
            } else {
              goodsOptionRepository.delete(goodsOption);
            }
          }
        }
      }
      return response.getBody().getHeader();
    }
    return null;
  }

  /**
   * Retrieve All stores(scm) using GodoMall Open API
   */
  private synchronized void getStoresFromGodo() {
    long start = System.currentTimeMillis();
    int newCount = 0;
    int updatedCount = 0;

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));

    String requestUrl = baseUrl + commonCodeUrl + "partner_key=" + partnerKey + "&key=" + key;

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("code_type", "scm");

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

    ResponseEntity<GodoScmResponse> response = restTemplate.exchange(
            requestUrl, HttpMethod.POST, request, GodoScmResponse.class);

    if (GODOMALL_RESPONSE_OK.equals(response.getBody().getHeader().getCode())) {
      List<GodoScmResponse.CodeData> dataList = response.getBody().getBody();

      for (GodoScmResponse.CodeData scm : dataList) {
        Optional<Store> optional = storeRepository.findById(scm.getScmNo());
        Store store;
        if (optional.isPresent()) {
          store = optional.get();
          updatedCount++;
        } else {
          store = new Store(scm.getScmNo());
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

  private String generateCategoryStr(String src) {
    String result = "";
    String[] strArray = StringUtils.split(src, "|");
    for (String str : strArray) {
      result = result.concat("|").concat(str).concat("|");
    }
    return result;
  }

  @Transactional
  public void deleteGoods(Goods goods) {
    List<GoodsRecommendation> recommendations = goodsRecommendationRepository.findAllByGoodsGoodsNo(goods.getGoodsNo());
    goodsRecommendationRepository.deleteAll(recommendations);

    List<GoodsLike> goodsLikes = goodsLikeRepository.findAllByGoodsGoodsNo(goods.getGoodsNo());
    goodsLikeRepository.deleteAll(goodsLikes);

    goodsRepository.delete(goods);
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class MustInfo {
    String key;
    String value;
  }
}