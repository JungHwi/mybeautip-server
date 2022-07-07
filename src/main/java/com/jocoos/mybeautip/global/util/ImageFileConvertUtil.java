package com.jocoos.mybeautip.global.util;

public class ImageFileConvertUtil {
    private static final String POSTFIX_THUMBNAIL = "_thumbnail";

    public static String convertToThumbnail(String original) {
        return convertImageFileName(original, POSTFIX_THUMBNAIL);
    }

    private static String convertImageFileName(String original, String postfix) {
        int index = original.lastIndexOf('.');
        String pre = original.substring(0, index);
        String post = original.substring(index);
        return pre + postfix + post;
    }
}
