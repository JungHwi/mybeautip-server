package com.jocoos.mybeautip.member.revenue;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.BeanUtils;

import lombok.Data;

@Data
public class RevenueInfo {

  private Long id;
  private Long videoId;
  private String goodsNo;
  private Integer state;  // 상태 (0: 구매가능, 1:품절, 2: 구매불가(판매 안함), 3: 노출안함, 4: 삭제됨)
  private String goodsNm;
  private String optionValue;
  private int quantity;
  private int goodsPrice;
  private String thumbnail;

  private int revenue;
  private Boolean confirmed;
  private Date createdAt;

  public RevenueInfo(Revenue revenue) {
    BeanUtils.copyProperties(revenue, this);
    this.videoId = revenue.getVideo().getId();

    Optional.of(revenue.getPurchase()).ifPresent(p -> {
        this.goodsNo = p.getGoods().getGoodsNo();
        this.state = p.getGoods().getState();
        this.goodsNm = p.getGoods().getGoodsNm();
        this.quantity = p.getQuantity();
        this.goodsPrice = p.getGoodsPrice();
        this.optionValue = p.getOptionValue();
        this.thumbnail = revenue.getVideo().getThumbnailUrl();
      }
    );
  }
}
