package com.jocoos.mybeautip.global.util;

import com.jocoos.mybeautip.global.code.UrlDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImageUrlConvertUtil {


    private static String IMAGE_BASE_URL;

    @Value("${mybeautip.aws.cf.domain}")
    private void setImageBaseUrl(String injectUrl) {
        IMAGE_BASE_URL = injectUrl;
    }

    public static String toUrl(String filename, UrlDirectory directory) {
        return IMAGE_BASE_URL + directory.getDirectory() + filename;
    }

    public static String toUrl(String filename, UrlDirectory directory, Long id) {
        return IMAGE_BASE_URL + directory.getDirectory(id) + filename;
    }
}
