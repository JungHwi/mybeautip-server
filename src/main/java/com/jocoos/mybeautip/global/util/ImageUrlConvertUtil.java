package com.jocoos.mybeautip.global.util;

import com.jocoos.mybeautip.global.code.UrlDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImageUrlConvertUtil {

    private static String CF_DOMAIN;
    private static String S3_DOMAIN;

    public ImageUrlConvertUtil(@Value("${mybeautip.aws.cf.domain}") String cf,
                               @Value("${mybeautip.aws.s3.domain}") String s3) {
        CF_DOMAIN = cf;
        S3_DOMAIN = s3;
    }

    public static String toUrl(String filename, UrlDirectory directory) {
        return CF_DOMAIN + directory.getDirectory() + filename;
    }

    public static String toUrl(String filename, UrlDirectory directory, Long id) {
        return CF_DOMAIN + directory.getDirectory(id) + filename;
    }

    public static String getUri(String Url) {
        return Url.replace(CF_DOMAIN, "").replace(S3_DOMAIN, "");
    }

}
