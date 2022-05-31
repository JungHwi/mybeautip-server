package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.member.order.Order;
import com.jocoos.mybeautip.member.order.OrderInquiry;
import com.jocoos.mybeautip.member.order.Purchase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

@Projection(name = "inquiry_detail", types = {OrderInquiry.class, Purchase.class, Goods.class, Order.class})
public interface OrderInquireExceprt {

    Long getId();

    @Value("#{target.purchase}")
    Purchase getPurchase();

    @Value("#{target.purchase?.goods}")
    Goods getGoods();

    @Value("#{target.order}")
    Order getOrder();

    int getState();

    String getReason();

    String getComment();

    boolean isCompleted();

    Date getCreatedAt();

    @Value("#{target.purchase?.goods?.getScmNo()}")
    Long getSupplierId();

    @Value("#{target.order?.delivery.recipient}")
    String getInquireName();


}
