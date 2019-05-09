package com.jocoos.mybeautip.goods;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.notification.MessageService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class GoodsOptionService {

  private final MessageService messageService;
  private final TimeSaleService timeSaleService;
  private final GoodsRepository goodsRepository;
  private final GoodsOptionRepository goodsOptionRepository;

  private static final String GOODS_NOT_FOUND = "goods.not_found";
  
  public GoodsOptionService(MessageService messageService,
                            TimeSaleService timeSaleService,
                            GoodsRepository goodsRepository,
                            GoodsOptionRepository goodsOptionRepository) {
    this.messageService = messageService;
    this.goodsRepository = goodsRepository;
    this.goodsOptionRepository = goodsOptionRepository;
    this.timeSaleService = timeSaleService;
  }
  
  public GoodsOptionInfo getGoodsOptionData(int goodsNo, String lang, TimeSaleCondition timeSaleCondition) {
    Goods goods = goodsRepository.findByGoodsNo(String.valueOf(goodsNo))
        .orElseThrow(() -> new NotFoundException("goods_not_found", messageService.getMessage(GOODS_NOT_FOUND, lang)));
  
    List<GoodsOption> options = goodsOptionRepository.findByGoodsNo(goodsNo);
    if (options.size() == 0) {
      return new GoodsOptionInfo(0, new ArrayList<>());
    }

    timeSaleService.applyTimeSaleOptions(goodsNo, options, timeSaleCondition);
  
    Map<String, List<OptionData>> map = new LinkedHashMap<>();
    List<OptionData> subOptions;
    for (GoodsOption option : options) {
      if ("n".equalsIgnoreCase(option.getOptionViewFl())) {
        continue;
      }
      String name = option.getOptionValue1();
      if (StringUtils.isNotEmpty(option.getOptionValue2())) {
        if (map.containsKey(name)) {
          subOptions = map.get(name);
          subOptions.add(new OptionData(option.getOptionValue2(), isSoldOut(goods, option), option));
        } else {
          subOptions = new ArrayList<>();
          subOptions.add(new OptionData(option.getOptionValue2(), isSoldOut(goods, option), option));
        }
        map.put(name, subOptions);
      }
    }
  
    GoodsOptionInfo info = new GoodsOptionInfo();
    if (map.size() <= 0) {
      info.setLevel(1);
      List<OptionData> list = new ArrayList<>();
      for (GoodsOption option : options) {
        if ("n".equalsIgnoreCase(option.getOptionViewFl())) {
          continue;
        }
        list.add(new OptionData(option.getOptionValue1(), isSoldOut(goods, option), option));
      }
      info.setContent(list);
    } else {
      info.setLevel(2);
      List<OptionData> list = new ArrayList<>();
      for (String name : map.keySet()) {
        boolean soldOut = true;
        for (OptionData optionData : map.get(name)) {
          soldOut = soldOut & optionData.getSoldOut();
        }
        list.add(new OptionData(name, soldOut, map.get(name)));
      }
      info.setContent(list);
    }

    return info;
  }
  
  private boolean isSoldOut(Goods goods, GoodsOption option) {
    if ("n".equals(option.getOptionSellFl())) { // 옵션 판매안함
      return true;
    }
    
    if ("y".equals(goods.getSoldOutFl())) { // 상품 품절 플래그
      return true;
    }
    
    if ("y".equals(goods.getStockFl()) && goods.getTotalStock() <= 0) { // 재고량에 따름, 총 재고량 부족
      return true;
    }
  
    // 재고량에 따름, 옵션 재고량 부족
    return "y".equals(goods.getStockFl()) && option.getStockCnt() <= 0;
  }
  
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class GoodsOptionInfo {
    int level;
    List<OptionData> content;
  }
  
  @Data
  static class OptionData {
    private String name;
    private Boolean soldOut;
    private Integer optionPrice;
    private Integer stockCnt;
    private Integer no;
    private List<OptionData> subOptions;
    
    public OptionData(String name, boolean soldOut, GoodsOption option) {
      this.name = name;
      this.soldOut = soldOut;
      this.optionPrice = option.getOptionPrice();
      this.stockCnt = option.getStockCnt();
      this.no = option.getOptionNo();
    }
  
    public OptionData(String name, boolean soldOut, List<OptionData> subOptions) {
      this.name = name;
      this.soldOut = soldOut;
      this.subOptions = subOptions;
    }
  }
}
