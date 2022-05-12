package com.jocoos.mybeautip.support;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomUtils {

    private static final int MIN_INTEGER = 1;
    private static final int MIN_INDEX = 0;

    public static final int TAG_ALPHABETIC_LENGTH = 5;
    public static final int TAG_MAX_NUMERIC = 99;

    public static int getRandom(int max) {
        return getRandom(MIN_INTEGER, max);
    }

    public static int getRandomIndex(int max) {
        return getRandom(MIN_INDEX, max - 1);
    }

    public static int getRandom(double min, double max){
        double x = (int)(Math.random()*((max-min)+1)) + min;
        return (int)x;
    }

    public static String generateTag() {
        String tag = RandomStringUtils.randomAlphabetic(TAG_ALPHABETIC_LENGTH).toUpperCase();
        int numberTag = RandomUtils.getRandom(TAG_MAX_NUMERIC);
        return String.format("%s%02d", tag, numberTag);
    }
}
