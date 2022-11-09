package com.jocoos.mybeautip.global.util;

import org.apache.commons.lang3.StringUtils;

public class FileUtil {

    public static String getFilename(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        return path.substring(path.lastIndexOf("/") + 1);
    }
}
