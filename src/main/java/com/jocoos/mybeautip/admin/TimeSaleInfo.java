package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.goods.TimeSale;
import com.jocoos.mybeautip.goods.TimeSaleOption;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

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
