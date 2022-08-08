package com.jocoos.mybeautip.global.util;

public class FileUtil {

    public static String getFilename(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }
}
