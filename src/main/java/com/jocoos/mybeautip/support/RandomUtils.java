package com.jocoos.mybeautip.support;

public class RandomUtils {

    private static final int MIN_INTEGER = 1;
    private static final int MIN_INDEX = 0;

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
}
