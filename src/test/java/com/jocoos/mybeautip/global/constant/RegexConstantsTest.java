package com.jocoos.mybeautip.global.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegexConstantsTest {

    @Test
    public void testUsername() {
        assertTrue("Breeze".matches(RegexConstants.regexForUsername));  // 영어만
        assertTrue("김정휘".matches(RegexConstants.regexForUsername));    // 한글만
        assertTrue("830205".matches(RegexConstants.regexForUsername));  // 숫자만
        assertTrue("-_-_-".matches(RegexConstants.regexForUsername));  // 특수기호만

        assertFalse("김".matches(RegexConstants.regexForUsername));              // 2자 미만
        assertFalse("김정휘Breeze83".matches(RegexConstants.regexForUsername));   // 10자 이상
        assertFalse("!@#$".matches(RegexConstants.regexForUsername));           // -_ 이외의 특수문자
    }

}