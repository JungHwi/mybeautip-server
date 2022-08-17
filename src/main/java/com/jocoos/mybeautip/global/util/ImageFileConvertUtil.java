package com.jocoos.mybeautip.global.util;

import org.springframework.util.StringUtils;

public class ImageFileConvertUtil {
    private static final String POSTFIX_THUMBNAIL = "_thumbnail";

    public static String convertToThumbnail(String original) {
        return convertImageFileName(original, POSTFIX_THUMBNAIL);
    }

    public static String toFileName(String imgUrl) {
        return StringUtils.getFilename(imgUrl);
    }

    private static String convertImageFileName(String original, String postfix) {
        int index = original.lastIndexOf('.');
        String pre = original.substring(0, index);
        String post = original.substring(index);
        return pre + postfix + post;
    }
}
