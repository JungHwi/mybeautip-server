package com.jocoos.mybeautip.global.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddressVo {

    private String zipcode;

    private String address1;

    private String address2;

    public String get() {
        return String.format("(%s) %s %s", zipcode, address1, address2);
    }
}
