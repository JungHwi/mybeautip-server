package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.member.MemberMeInfo;
import com.jocoos.mybeautip.member.code.SkinType;
import com.jocoos.mybeautip.member.code.SkinWorry;
import com.jocoos.mybeautip.member.detail.MemberDetailRequest;
import com.jocoos.mybeautip.member.detail.MemberDetailResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
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
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@RunWith(SpringRunner.class)
@SpringBootTest
class MemberControllerTest {
    @Autowired
    private MemberController memberController;

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    public void getMe() {
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
    public void patchAvatar() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("img/breeze.jpeg").getFile());

        MockMultipartFile multipartFile = new MockMultipartFile("image",
                "breeze.jpeg",
                "image/jpeg",
                Files.newInputStream(Paths.get(file.getAbsolutePath())));

        ResponseEntity<Map<String, String>> info = memberController.uploadAvatar(multipartFile);
        assertEquals(HttpStatus.OK, info.getStatusCode());
    }

    @Test
    @WithUserDetails(value = "10", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    public void getDetailInfo() {
        ResponseEntity<MemberDetailResponse> result =memberController.getDetailInfo();
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    public void updateDetail() {
        MemberDetailRequest request = MemberDetailRequest.builder()
                .ageGroup(10)
                .skinType(SkinType.COMBINATION)
                .skinWorry(Stream.of(SkinWorry.PORES_SCARS, SkinWorry.SEBUM).collect(Collectors.toSet()))
                .build();

        ResponseEntity result = memberController.updateDetailInfo(request);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void migrationTag() {
        memberController.migrationTag();
    }
}