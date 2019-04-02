package com.jocoos.mybeautip.admin;

import org.springframework.beans.BeanUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.recommendation.GoodsRecommendation;
import com.jocoos.mybeautip.restapi.StoreController;
import com.jocoos.mybeautip.store.Store;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class GoodsDetailInfo extends GoodsInfo {
  private GoodsRecommendation recommendation;
  private StoreController.StoreInfo store;

  public GoodsDetailInfo(Goods goods) {
    BeanUtils.copyProperties(goods, this);
  }

  public GoodsDetailInfo(Goods goods, GoodsRecommendation recommendation) {
    this(goods);
    this.recommendation = recommendation;
  }

  public void setStore(Store store) {
    this.store = new StoreController.StoreInfo(store, null);
  }
}
