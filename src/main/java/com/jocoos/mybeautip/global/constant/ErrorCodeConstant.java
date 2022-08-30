package com.jocoos.mybeautip.global.constant;

/**
 * Error Code Format
 * (1)00(2)000
 * (1)Domain Code
 * (2)Detail Error Code
 * ex) 10001
 * -> (1) 10 - Member
 * -> (2) 000 - Member Not Found
 */
public class ErrorCodeConstant {


    // Member 10~
    public static final int MEMBER_NOT_FOUND = 10000;

    // Video 20~
    public static final int VIDEO_NOT_FOUND = 20000;

    // Post 21~
    public static final int POST_NOT_FOUND = 21000;

    // Event 22~
    public static final int EVENT_NOT_FOUND = 22000;

    // Goods 30~
    public static final int GOODS_NOT_FOUND = 30000;

    // Order 31~
    public static final int ORDER_NOT_FOUND = 31000;

    // Delivery 32~
    public static final int DELIVERY_NOT_FOUND = 32000;

    // POINT 40~
    public static final int NOT_ENOUGH_POINT = 40000;
    public static final int NOT_POSITIVE_POINT = 40001;

    // ETC 99~
    public static final int DEFAULT_ERROR_CODE = 99000;
}
