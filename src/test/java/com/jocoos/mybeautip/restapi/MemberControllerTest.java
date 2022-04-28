package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.member.MemberMeInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;

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

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void patchAvatar() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("img/breeze.jpeg").getFile());

        MockMultipartFile multipartFile = new MockMultipartFile("image",
                "breeze.jpeg",
                "image/jpeg",
                Files.newInputStream(Paths.get(file.getAbsolutePath())));

        ResponseEntity<String> info = memberController.updateAvatar(multipartFile);
        System.out.println("info >> " + info);
    }
}