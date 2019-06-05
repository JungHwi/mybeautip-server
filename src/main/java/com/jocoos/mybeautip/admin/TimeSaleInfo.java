package com.jocoos.mybeautip.admin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.goods.TimeSale;
import com.jocoos.mybeautip.goods.TimeSaleOption;

@Data
@NoArgsConstructor
public class TimeSaleInfo {

  private Long id;
  private String goodsNo;
  private Integer fixedPrice;
  private Integer goodsPrice;
  private Long broker;
  private List<TimeSaleOption> options;
  private Date startedAt;
  private Date endedAt;
  private Date deletedAt;


  public TimeSaleInfo(TimeSale t) {
    BeanUtils.copyProperties(t, this);
  }
}
