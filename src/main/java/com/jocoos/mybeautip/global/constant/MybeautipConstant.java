package com.jocoos.mybeautip.global.constant;

import static com.jocoos.mybeautip.global.code.UrlDirectory.AVATAR;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;

public class MybeautipConstant {

    // Image
    public static final String DEFAULT_AVATAR_FILE_NAME = "img_profile_default.png";
    public static final String DELETED_AVATAR_FILE_NAME = "img_profile_deleted.png";

    public static final String DEFAULT_AVATAR_URL = toUrl(DEFAULT_AVATAR_FILE_NAME, AVATAR);
    public static final String DELETED_AVATAR_URL = toUrl(DELETED_AVATAR_FILE_NAME, AVATAR);

    // String for Number
    public static final String MAX_LONG_STRING = "576460752303423487";

    public static final int RESPONSE_CODE_OK = 0;
}
