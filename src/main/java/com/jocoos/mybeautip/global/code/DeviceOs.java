package com.jocoos.mybeautip.global.code;

import com.jocoos.mybeautip.global.exception.MybeautipException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum DeviceOs implements CodeValue {

    ANDROID(true, "android", "ANDROID OS"),
    IOS(true, "ios", "iOS"),
    WINDOWS(false, "", "Windows"),
    BLACKBERRY(false, "", "BLACKBERRY"),
    ETC(false, "", "그 외..");

    private final boolean isSupport;
    private final String legacyCode;
    private final String description;

    public static DeviceOs of(final String legacyCode) {
        return Arrays.stream(DeviceOs.values())
                .filter(os -> os.legacyCode.equals(legacyCode))
                .findFirst()
                .orElseThrow(() -> new MybeautipException("Not Found DeviceOS. > " + legacyCode));
    }

    public boolean equal(final String legacyCode) {
        return this.legacyCode.equals(legacyCode);
    }

    @Override
    public String getName() {
        return this.name();
    }
}
