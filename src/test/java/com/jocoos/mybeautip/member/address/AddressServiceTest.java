package com.jocoos.mybeautip.member.address;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddressServiceTest {

    @Autowired
    private AddressService addressService;

    @Test
    void validPhoneNumber() {
        assertTrue(addressService.validPhoneNumber("01045739365"));
        assertThrows(BadRequestException.class, () -> addressService.validPhoneNumber("01045739365"));
    }
}