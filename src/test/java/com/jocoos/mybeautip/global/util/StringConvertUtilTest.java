package com.jocoos.mybeautip.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jocoos.mybeautip.domain.notification.code.NotificationLinkType;
import com.jocoos.mybeautip.domain.notification.vo.NotificationLink;
import org.junit.jupiter.api.Test;

import java.util.*;

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

    @Test
    public void test3() {
        List<NotificationLink> link = new ArrayList<>();

        link.add(NotificationLink.builder()
                .type(NotificationLinkType.POST)
                .parameter("3")
                .build());


        link.add(NotificationLink.builder()
                .type(NotificationLinkType.COMMENT)
                .parameter("10")
                .build());

        ObjectMapper mapper = new ObjectMapper();
        try {
            String text = mapper.writeValueAsString(link);
            System.out.println(text);
        } catch(Exception e) {
            // [{"type":"POST","parameter":""},{"type":"COMMENT","parameter":""}]
        }
    }

    @Test
    public void test4() {
        String test = "[{\"type\":\"POST\",\"parameter\":\"\"},{\"type\":\"COMMENT\",\"parameter\":\"\"}]";

        ObjectMapper mapper = new ObjectMapper();

        try {
            List<NotificationLink> link = Arrays.asList(mapper.readValue(test, NotificationLink[].class));
            System.out.println();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println();
    }
}