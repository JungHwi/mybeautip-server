package com.jocoos.mybeautip.support;

import org.junit.jupiter.api.Test;

class RandomUtilsTest {

    @Test
    void getRandom() {
        for (int i = 0; i < 100; i++) {
            int random = RandomUtils.getRandom(1);
            System.out.println(random);
        }
    }
}