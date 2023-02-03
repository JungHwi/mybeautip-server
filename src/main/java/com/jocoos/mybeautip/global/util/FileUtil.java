package com.jocoos.mybeautip.global.util;

import com.jocoos.mybeautip.domain.file.code.FileUrlDomain;
import org.apache.commons.lang3.StringUtils;

public class FileUtil {

    public static String getFileName(String path, FileUrlDomain domain) {
        return domain.getGetFilename().apply(path);
    }

    public static String getFileName(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        return path.substring(path.lastIndexOf("/") + 1);
    }

    public static boolean isChange(String originalFile, String newFileUrl) {
        String newFile = getFileName(newFileUrl);

        if (originalFile == null && newFile == null) {
            return false;
        }

        if (originalFile != null && originalFile.equals(newFile)) {
            return false;
        }

        return true;
    }
}
