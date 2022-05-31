package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.order.Delivery;
import com.jocoos.mybeautip.member.order.Order;
import com.jocoos.mybeautip.member.order.Payment;
import com.jocoos.mybeautip.member.order.Purchase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;
import java.util.List;

@Projection(name = "order_detail", types = {Order.class, Purchase.class})
public interface OrderExcerpt {

    @Value("#{target.purchases}")
    List<Purchase> getPurchases();

    @Value("#{target.payment}")
    Payment getPayment();

    @Value("#{target.delivery}")
    Delivery getDelivery();

    Long getId();

    int getState();

    String getStatus();

    String getNumber();

    int getGoodsCount();

    Date getCreatedAt();

    Date getDeliveredAt();

    String getOrderTitle();

    int getTotalShippingAmount();

    int getTotalGoodsAmount();

    String getMallOrderId();

    @Value("#{target.createdBy}")
    Member getCreatedBy();
}
