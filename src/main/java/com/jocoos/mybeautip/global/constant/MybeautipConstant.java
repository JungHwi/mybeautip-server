package com.jocoos.mybeautip.global.constant;

import java.time.ZoneId;

import static com.jocoos.mybeautip.global.code.UrlDirectory.AVATAR;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;

public class MybeautipConstant {
    // Service
    public static final String DISABLE_USERNAME = "집나간파우치";
    public static final String GUEST_TOKEN_PREFIX = "guest:";
    public static final String GUEST_USERNAME_PREFIX = "Guest_";

    // Image
    public static final String DEFAULT_AVATAR_FILE_NAME = "img_profile_default.png";

    public static final String DEFAULT_AVATAR_URL = toUrl(DEFAULT_AVATAR_FILE_NAME, AVATAR);

    public static final String HTTP_PREFIX = "http";

    public static final String TEST_FILE_URL = "https://test.com/test_file";
    public static final String TEST_FILE = "test_file";

    // String for Number
    public static final String MAX_INTEGER_STRING = "2147483647";
    public static final String MAX_LONG_STRING = "576460752303423487";

    public static final ZoneId KOREA_ZONED_ID = ZoneId.of("Asia/Seoul");

    public static final int RESPONSE_CODE_OK = 0;
    public static final int MAX_RETRY_FFL = 3;
}
