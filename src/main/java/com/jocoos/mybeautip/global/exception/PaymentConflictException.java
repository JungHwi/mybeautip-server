package com.jocoos.mybeautip.global.exception;

public class PaymentConflictException extends ConflictException {

    static final String DEFAULT_MESSAGE = "order_state_conflict";
    static final String description = "이용 중 불편을드려 죄송합니다. 고객센터로 문의 주시면 빠르게 안내해드리겠습니다.\n\n" +
            "고객센터: 010-9482-5590\n" +
            "월-금: 10:00 - 19:00\n" +
            "주말 및 공휴일: 휴무\n\n" +
            "메일: mybeautip@mybeautip.tv";

    public PaymentConflictException() {
        super(DEFAULT_MESSAGE, description);
    }
}
