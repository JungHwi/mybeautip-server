package com.jocoos.mybeautip.global.vo;

import com.jocoos.mybeautip.global.code.Telecom;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Phone {

    private Telecom telecom;

    private String phone_1;
    private String phone_2;
    private String phone_3;

    public String toString() {
        return phone_1 + phone_2 + phone_3;
    }
}
