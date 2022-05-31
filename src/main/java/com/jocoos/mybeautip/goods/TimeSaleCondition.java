package com.jocoos.mybeautip.goods;

public final class TimeSaleCondition {
    private Long broker;

    private TimeSaleCondition() {
    }

    private TimeSaleCondition(Long broker) {
        this.broker = broker;
    }

    public static TimeSaleCondition createGeneral() {
        return new TimeSaleCondition();
    }

    public static TimeSaleCondition createWithBroker(Long broker) {
        return new TimeSaleCondition(broker);
    }

    Long getBroker() {
        return broker;
    }
}
