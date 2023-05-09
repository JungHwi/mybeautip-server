package com.jocoos.mybeautip.support;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomUtils {

    private static final int MIN_INTEGER = 1;
    private static final int MIN_INDEX = 0;

    private static final int TAG_ALPHABETIC_LENGTH = 5;
    private static final int TAG_MAX_NUMERIC = 99;

    private static final String USERNAME_PREFIX = "뷰띠";
    private static final int USERNAME_ALPHABETIC_LENGTH = 2;
    private static final int USERNAME_MAX_NUMERIC = 99999;

    private static final int FILENAME_ALPHABETIC_LENGTH = 16;

    public static int getRandom(int max) {
        return getRandom(MIN_INTEGER, max);
    }

    public static int getRandomIndex(int max) {
        return getRandom(MIN_INDEX, max - 1);
    }

    public static int getRandom(double min, double max) {
        double x = (int) (Math.random() * ((max - min) + 1)) + min;
        return (int) x;
    }

    public static String generateTag() {
        String tag = RandomStringUtils.randomAlphabetic(TAG_ALPHABETIC_LENGTH).toUpperCase();
        int numberTag = getRandom(TAG_MAX_NUMERIC);
        return String.format("%s%02d", tag, numberTag);
    }

    public static String generateUsername() {
        String middleName = RandomStringUtils.randomAlphabetic(USERNAME_ALPHABETIC_LENGTH).toLowerCase();
        int randomNumber = getRandom(USERNAME_MAX_NUMERIC);
        return String.format("%s%s%05d", USERNAME_PREFIX, middleName, randomNumber);
    }

    public static String generateFilename() {
        return RandomStringUtils.randomAlphanumeric(FILENAME_ALPHABETIC_LENGTH).toLowerCase();
    }

    public static String generateBrandCode() {
        return String.format("B-%05d", getRandom(1, 99999));
    }

    public static String generateStoreCategoryCode() {
        return String.format("C-01-%03d", getRandom(1, 999));
    }
}
