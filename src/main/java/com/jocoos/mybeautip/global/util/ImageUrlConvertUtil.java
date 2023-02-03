package com.jocoos.mybeautip.global.util;

import com.jocoos.mybeautip.domain.file.code.FileUrlDomain;
import com.jocoos.mybeautip.global.code.UrlDirectory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImageUrlConvertUtil {

    private static String CF_DOMAIN;
    private static String S3_DOMAIN;
    private static String FLIP_FLOP_DOMAIN;
    private static String DEFAULT_FILENAME;

    public ImageUrlConvertUtil(@Value("${mybeautip.aws.cf.domain}") String cf,
                               @Value("${mybeautip.aws.s3.domain}") String s3,
                               @Value("${flipflop.aws.s3.domain}") String flipFlop,
                               @Value("${flipflop.default-filename}") String defaultFilename) {
        CF_DOMAIN = cf;
        S3_DOMAIN = s3;
        FLIP_FLOP_DOMAIN = flipFlop;
        DEFAULT_FILENAME = defaultFilename;
    }

    public static String toUrl(String filename, UrlDirectory directory) {
        if (StringUtils.isBlank(filename)) {
            return null;
        } else if (filename.startsWith("http")) {
            return filename;
        } else {
            return CF_DOMAIN + directory.getDirectory() + filename;
        }
    }

    public static String toUrl(FileUrlDomain domain, String filename, UrlDirectory directory, Long id) {
        return switch (domain) {
            case MYBEAUTIP -> toUrl(filename, directory, id);
            case FLIPFLOP -> FLIP_FLOP_DOMAIN + filename + "/" + DEFAULT_FILENAME;
        };
    }

    public static String toUrl(String filename, UrlDirectory directory, Long id) {
        if (StringUtils.isBlank(filename)) {
            return null;
        } else if (filename.startsWith("http")) {
            return filename;
        } else {
            return CF_DOMAIN + directory.getDirectory(id) + filename;
        }
    }

    public static String getUri(String Url) {
        return Url.replace(CF_DOMAIN, "").replace(S3_DOMAIN, "");
    }

}
