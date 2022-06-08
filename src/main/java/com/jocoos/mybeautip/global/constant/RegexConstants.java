package com.jocoos.mybeautip.global.constant;

public class RegexConstants {

    /** UserName 정규식
     * 1. 길이는 최소 2자 최대 10자.
     * 2. 한글, 영어 대소문자, 숫자 허용
     * 3. 특수 문자 '-', '_' 2개만 허용.
     */
    public static String regexForUsername = "^[\\w가-힣a-zA-Z0-9-_]{2,10}$";

    public static String regexForPhoneNumber = "^01([0|1|6|7|8|9])?([0-9]{3,4})?([0-9]{4})$";
}
