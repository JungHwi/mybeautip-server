package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.legacy.store.LegacyStore;
import com.jocoos.mybeautip.legacy.store.LegacyStoreController;
import com.jocoos.mybeautip.recommendation.GoodsRecommendation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class GoodsDetailInfo extends GoodsInfo {
    private GoodsRecommendation recommendation;
    private LegacyStoreController.StoreInfo store;

    public GoodsDetailInfo(Goods goods) {
        BeanUtils.copyProperties(goods, this);
    }

    public GoodsDetailInfo(Goods goods, GoodsRecommendation recommendation) {
        this(goods);
        this.recommendation = recommendation;
    }

    public void setStore(LegacyStore legacyStore) {
        this.store = new LegacyStoreController.StoreInfo(legacyStore, null);
    }
}
