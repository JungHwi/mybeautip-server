package com.jocoos.mybeautip.global.util;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.jocoos.mybeautip.global.util.StringConvertUtil.convertJsonToMap;
import static com.jocoos.mybeautip.global.util.StringConvertUtil.convertMapToJson;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StringConvertUtilTest {
    @Test
    public void convertMapToJsonTest() {
        String expect = "{\"joinDate\":\"20220321\",\"name\":\"breeze\"}";

        Map<String, String> map = new HashMap<>();
        map.put("name", "breeze");
        map.put("joinDate", "20220321");
        String actual = convertMapToJson(map);

        assertEquals(expect, actual);
    }

    @Test
    public void test2() {
        Map<String, String> expect = new HashMap<>();
        expect.put("name", "breeze");
        expect.put("joinDate", "20220321");

        String str = "{\"joinDate\":\"20220321\",\"name\":\"breeze\"}";
        Map<String, String> actual = convertJsonToMap(str);

        assertEquals(expect, actual);
    }
}