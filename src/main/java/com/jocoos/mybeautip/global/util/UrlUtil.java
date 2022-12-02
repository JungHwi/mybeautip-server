package com.jocoos.mybeautip.global.util;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtil {

    public static boolean isUrl(String urlString) {
        try {
            new URL(urlString);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
