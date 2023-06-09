package com.jocoos.mybeautip.global.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImageFileConvertUtilTest {

    @Test
    public void convertImageFileName() {
        String real = ImageFileConvertUtil.convertToThumbnail("https://static-dev.mybeautip.com/event/event_signup.png");
        assertEquals("https://static-dev.mybeautip.com/event/event_signup_thumbnail.png", real);
    }
}