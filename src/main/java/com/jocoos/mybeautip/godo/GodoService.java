package com.jocoos.mybeautip.godo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jocoos.mybeautip.goods.*;
import com.jocoos.mybeautip.recommendation.GoodsRecommendationRepository;
import com.jocoos.mybeautip.store.Store;
import com.jocoos.mybeautip.store.StoreRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
  private final DeliveryChargeRepository deliveryChargeRepository;
  private final DeliveryChargeDetailRepository deliveryChargeDetailRepository;
  private final ObjectMapper mapper;

  @Value("${godomall.scheme}")
  private String scheme;

  @Value("${godomall.base-url}")
  private String godoBaseUrl;

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

  @Value("${mybeautip.category.image-path.prefix}")
  private String categoryImagePrefix;

  @Value("${mybeautip.category.image-path.suffix}")
  private String categoryImageSuffix;

  @Value("${mybeautip.store.image-path.prefix}")
  private String storeImagePrefix;

  @Value("${mybeautip.store.image-path.cover-suffix}")
  private String storeImageSuffix;

  @Value("${mybeautip.store.image-path.thumbnail-suffix}")
  private String storeImageThumbnailSuffix;

  @Value("${mybeautip.store.image-path.refund-suffix}")
  private String storeImageRefundSuffix;

  @Value("${mybeautip.store.image-path.as-suffix}")
  private String storeImageAsSuffix;

  private static final String GODOMALL_RESPONSE_OK = "000";
  private static final String GOODS_CATEGORY_TOP = "0";

  private static int refreshCount;
  private static int deletedCount;

  public GodoService(RestTemplate restTemplate,
                     CategoryRepository categoryRepository,
                     GoodsRepository goodsRepository,
                     GoodsOptionRepository goodsOptionRepository,
                     StoreRepository storeRepository,
                     GoodsLikeRepository goodsLikeRepository,
                     GoodsRecommendationRepository goodsRecommendationRepository,
                     DeliveryChargeRepository deliveryChargeRepository,
                     DeliveryChargeDetailRepository deliveryChargeDetailRepository,
                     ObjectMapper mapper) {
    this.restTemplate = restTemplate;
    this.categoryRepository = categoryRepository;
    this.goodsRepository = goodsRepository;
    this.goodsOptionRepository = goodsOptionRepository;
    this.storeRepository = storeRepository;
    this.goodsLikeRepository = goodsLikeRepository;
    this.goodsRecommendationRepository = goodsRecommendationRepository;
    this.deliveryChargeRepository = deliveryChargeRepository;
    this.deliveryChargeDetailRepository = deliveryChargeDetailRepository;
    this.mapper = mapper;
  }

  @Scheduled(initialDelay = 5000, fixedRate = 86400000) // 5 sec, 1 day
  public void gatheringDeliveryInfoFromGodomall() throws IOException {
    log.debug("GatheringDeliveryFromGodomall task started...");
    getDeliveryInfoFromGodo();
    log.debug("GatheringDeliveryFromGodomall task ended");
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
    UriComponents uriComponents = UriComponentsBuilder.newInstance()
      .scheme(scheme).host(godoBaseUrl).path(categorySearchUrl)
      .query("partner_key={value}").query("key={value}").query("cateCd={value}")
      .buildAndExpand(partnerKey, key, code);

    ResponseEntity<GodoCategoryResponse> response
            = restTemplate.getForEntity(uriComponents.toString(), GodoCategoryResponse.class);

    if (GODOMALL_RESPONSE_OK.equals(response.getBody().getHeader().getCode())) {
      List<GodoCategoryResponse.CategoryData> dataList = response.getBody().getBody();

      for (GodoCategoryResponse.CategoryData data : dataList) {
        boolean hidden = "n".equals(data.getCateDisplayFl()) || "n".equals(data.getCateDisplayMobileFl());
        Optional<Category> optional = categoryRepository.findById(data.getCateCd());
        Category category;
        if (optional.isPresent()) {
          if (hidden) { // delete
            categoryRepository.delete(optional.get());
          } else {  // update
            category = copyPropertiesFrom(data);
            category.setCode(optional.get().getCode());
            category.setThumbnailUrl(optional.get().getThumbnailUrl());
            categoryRepository.save(category);
          }
        } else {  // insert
          if (!hidden) {
            category = copyPropertiesFrom(data);
            category.setThumbnailUrl(String.format("%s%s%s", categoryImagePrefix, data.getCateCd(), categoryImageSuffix));
            categoryRepository.save(category);
          }
        }
      }
      return dataList;
    }
    return null;
  }

  private Category copyPropertiesFrom(GodoCategoryResponse.CategoryData source) {
    Category target = new Category();

    if (source.getCateCd().length() == 3) {  // top category,
      target.setGroup(GOODS_CATEGORY_TOP);
    } else if (source.getCateCd().length() == 6) {  // sub category,
      target.setGroup(source.getCateCd().substring(0, 3));
    } else {
      log.warn("Category code is invalid: " + source.getCateCd());
    }

    target.setCode(source.getCateCd());
    target.setName(source.getCateNm());

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
        "refresh goods items: %d", (System.currentTimeMillis() - start), refreshCount));
    refreshCount = 0;
  }

  private GodoGoodsResponse.Header getGoodsFromGodo(int nextPage) throws IOException {
    UriComponents uriComponents = UriComponentsBuilder.newInstance()
      .scheme(scheme).host(godoBaseUrl).path(goodsSearchUrl)
      .query("partner_key={value}").query("key={value}").query("page={value}").query("size={value}")
      .buildAndExpand(partnerKey, key, nextPage, 15);

    ResponseEntity<GodoGoodsResponse> response
      = restTemplate.getForEntity(uriComponents.toString(), GodoGoodsResponse.class);

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
          // FIXME: delete goods
          log.debug("Goods not available: " + goods.getGoodsNo());
          continue;
        }

        if (Strings.isNotEmpty(goodsData.getCateCd())) {
          goods.setAllCd(generateCategoryStr(goodsData.getCateCd()));
        }

        if (Strings.isNotEmpty(goodsData.getAllCateCd())) {
          goods.setAllCd(generateCategoryStr(goodsData.getAllCateCd()));
        }
        
        Optional<DeliveryCharge> optionalDeliveryCharge = deliveryChargeRepository.findById(goodsData.getDeliverySno());
        if (optionalDeliveryCharge.isPresent()) {
          goods.setDeliveryFixFl(optionalDeliveryCharge.get().getFixFl());
          goods.setDeliveryMethod(optionalDeliveryCharge.get().getMethod());
        } else {
          log.warn("DeliveryCharge is null, delivery_sno:" + goodsData.getDeliverySno());
          goods.setDeliveryFixFl("");
          goods.setDeliveryMethod("");
        }

        List<MustInfo> list = new ArrayList<>();

        if (goodsData.getGoodsMustInfoData() != null) {
          for (GodoGoodsResponse.GoodsMustInfoData mustInfo : goodsData.getGoodsMustInfoData()) {
            list.add(new MustInfo(mustInfo.getStepData().getInfoTitle(), mustInfo.getStepData().getInfoValue()));
          }
          goods.setGoodsMustInfo(mapper.writeValueAsString(list));
        }
        goodsRepository.save(goods);
        refreshCount++;

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

    UriComponents uriComponents = UriComponentsBuilder.newInstance()
      .scheme(scheme).host(godoBaseUrl).path(commonCodeUrl)
      .query("partner_key={value}").query("key={value}")
      .buildAndExpand(partnerKey, key);

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("code_type", "scm");

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

    ResponseEntity<GodoScmResponse> response = restTemplate.exchange(
            uriComponents.toString(), HttpMethod.POST, request, GodoScmResponse.class);

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
          store.setImageUrl(String.format("%s%d%s", storeImagePrefix, store.getId(), storeImageSuffix));
          store.setThumbnailUrl(String.format("%s%d%s", storeImagePrefix, store.getId(), storeImageThumbnailSuffix));
          store.setRefundUrl(String.format("%s%d%s", storeImagePrefix, store.getId(), storeImageRefundSuffix));
          store.setAsUrl(String.format("%s%d%s", storeImagePrefix, store.getId(), storeImageAsSuffix));
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

  /**
   * Retrieve All delivery data using GodoMall Open API
   */
  private synchronized void getDeliveryInfoFromGodo() throws JsonProcessingException {
    long start = System.currentTimeMillis();
    int newCount = 0;
    int updatedCount = 0;

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));

    UriComponents uriComponents = UriComponentsBuilder.newInstance()
      .scheme(scheme).host(godoBaseUrl).path(commonCodeUrl)
      .query("partner_key={value}").query("key={value}")
      .buildAndExpand(partnerKey, key);

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("code_type", "delivery");

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

    ResponseEntity<GodoDeliveryResponse> response = restTemplate.exchange(
      uriComponents.toString(), HttpMethod.POST, request, GodoDeliveryResponse.class);

    if (GODOMALL_RESPONSE_OK.equals(response.getBody().getHeader().getCode())) {
      List<GodoDeliveryResponse.CodeData> dataList = response.getBody().getBody();

      for (GodoDeliveryResponse.CodeData deliverydata : dataList) {
        Optional<DeliveryCharge> optional = deliveryChargeRepository.findById(deliverydata.getSno());
        DeliveryCharge deliveryCharge;
        if (optional.isPresent()) {
          deliveryCharge = optional.get();
          BeanUtils.copyProperties(deliverydata, deliveryCharge);
          deliveryChargeRepository.save(deliveryCharge);
          updatedCount++;
        } else {
          deliveryCharge = new DeliveryCharge(deliverydata.getSno());
          BeanUtils.copyProperties(deliverydata, deliveryCharge);
          deliveryCharge = deliveryChargeRepository.save(deliveryCharge);
          if (deliverydata.getChargeData() != null) {
            List<ChargeData> list = new ArrayList<>();
            for (GodoDeliveryResponse.ChargeData chargeData : deliverydata.getChargeData()) {
              list.add(new ChargeData(chargeData));
              deliveryChargeDetailRepository.save(new DeliveryChargeDetail(deliveryCharge, chargeData));
            }
            deliveryCharge.setChargeData(mapper.writeValueAsString(list));
            deliveryChargeRepository.save(deliveryCharge);
          }
          newCount++;
        }
      }
    }

    log.debug(String.format("GatheringDeliveryFromGodomall elapsed: %d miliseconds, " +
        "new delivery items: %d, updated delivery items: %d",
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

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class MustInfo {
    String key;
    String value;
  }

  @Data
  @NoArgsConstructor
  public static class ChargeData {
    private Integer unitStart;
    private Integer unitEnd;
    private Integer price;

    public ChargeData(GodoDeliveryResponse.ChargeData data) {
      this.unitStart = data.getUnitStart().intValue();
      this.unitEnd = data.getUnitEnd().intValue();
      this.price = data.getPrice().intValue();
    }
  }
}