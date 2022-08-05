package com.jocoos.mybeautip.global.util;

import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;

public class FileUtil {

    public static final MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();

    public static String getMimeType(MultipartFile file) {
        return fileTypeMap.getContentType(file.getContentType());
    }
}
