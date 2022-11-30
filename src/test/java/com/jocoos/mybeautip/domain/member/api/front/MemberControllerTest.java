package com.jocoos.mybeautip.domain.member.api.front;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator;
import com.jocoos.mybeautip.global.dto.single.LongDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class MemberControllerTest extends RestDocsTestSupport {

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void getMyCommunities() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/member/random-username")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("get_random_username",
                        responseFields(
                                fieldWithPath("string").type(JsonFieldType.STRING).description("랜덤으로 생성된 유저 닉네임")
                        )
                )
        );
    }

    @Test
    @Transactional
    void wakeup() throws Exception {
        LongDto longDto = new LongDto(44L);
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/api/1/member/wakeup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(longDto)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("member_wakeup",
                        requestFields(
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("휴면해제 멤버 아이디")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("popup id"),
                                fieldWithPath("image_url").type(JsonFieldType.STRING).description("팝업 이미지 URL"),
                                fieldWithPath("display_type").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.POPUP_DISPLAY_TYPE)),
                                fieldWithPath("button_list").type(JsonFieldType.ARRAY).description("버튼 정보 목록"),
                                fieldWithPath("button_list.[].name").type(JsonFieldType.STRING).description("버튼명"),
                                fieldWithPath("button_list.[].link_type").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.BUTTON_LINK_TYPE))
                        )
                )
        );
    }
}