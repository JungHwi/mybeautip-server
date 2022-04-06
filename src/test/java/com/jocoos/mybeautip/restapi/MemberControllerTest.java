package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.member.MemberMeInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@RunWith(SpringRunner.class)
@SpringBootTest
class MemberControllerTest {
    @Autowired
    private MemberController memberController;

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void getMe() {
        Principal principal = new Principal() {
            @Override
            public String getName() {
                return "4";
            }
        };

        EntityModel<MemberMeInfo> info = memberController.getMe(principal, "ko");
        System.out.println("info >> " + info);
    }
}