package com.jocoos.mybeautip.member.revenue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class RevenueOverview {

    @JsonIgnore
    private List<Revenue> revenues;

    @JsonIgnore
    private int platformFeeRatio;

    /**
     * count of purchase
     */
    private int soldGoodsCount;

    /**
     * sum of total price of purchase
     */
    private Long totalPrice;

    /**
     * Platform fee is totalPrice * platform ratio
     */
    private int platformFee;

    /**
     * Equals to revenue of member
     */
    private int estimatedRevenue;

    public RevenueOverview(int platformFeeRatio, List<Revenue> revenues, int estimatedRevenue) {
        this.platformFeeRatio = platformFeeRatio;
        this.revenues = revenues;
        this.estimatedRevenue = estimatedRevenue;
        initOverview();
    }

    private void initOverview() {
        this.totalPrice = 0L;

        if (revenues != null) {
            this.soldGoodsCount = revenues != null ? revenues.size() : 0;
            for (Revenue r : revenues) {
                totalPrice += r.getPurchase().getTotalPrice();
            }

            this.platformFee = Math.toIntExact(((totalPrice * platformFeeRatio) / 100));
        } else {
            this.soldGoodsCount = 0;
        }
    }
}
