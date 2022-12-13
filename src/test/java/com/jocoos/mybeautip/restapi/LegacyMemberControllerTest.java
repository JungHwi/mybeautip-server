package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LegacyMemberControllerTest extends RestDocsTestSupport {

    @DisplayName("GET /api/1/members/me - 내 정보 조회 성공")
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Test
    void getMeSuccess() throws Exception {

        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/members/me"))
                .andExpect(status().isOk())
                .andDo(print());
        resultDocs(resultActions);
    }

    private void resultDocs(ResultActions resultActions) throws Exception {
        resultActions.andDo(document("get my setting",
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("멤버 아이디"),
                        fieldWithPath("tag").type(JsonFieldType.STRING).description("멤버 태그"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("멤버 상태")
                                .description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.MEMBER_STATUS)),
                        fieldWithPath("grant_type").type(JsonFieldType.STRING)
                                .description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.GRANT_TYPE)).optional(),
                        fieldWithPath("username").type(JsonFieldType.STRING).description("멤버 이름"),
                        fieldWithPath("email").type(JsonFieldType.STRING).description("멤버 이메일").optional(),
                        fieldWithPath("phone_number").type(JsonFieldType.STRING).description("전화 번호").optional(),
                        fieldWithPath("avatar_url").type(JsonFieldType.STRING).description("멤버 아바타 이미지 url"),
                        fieldWithPath("permission.chat_post").type(JsonFieldType.BOOLEAN).description(""),
                        fieldWithPath("permission.comment_post").type(JsonFieldType.BOOLEAN).description(""),
                        fieldWithPath("permission.live_post").type(JsonFieldType.BOOLEAN).description(""),
                        fieldWithPath("permission.motd_post").type(JsonFieldType.BOOLEAN).description(""),
                        fieldWithPath("permission.revenue_return").type(JsonFieldType.BOOLEAN).description(""),
                        fieldWithPath("follower_count").type(JsonFieldType.NUMBER).description("팔로워 수"),
                        fieldWithPath("following_count").type(JsonFieldType.NUMBER).description("팔로잉 수"),
                        fieldWithPath("video_count").type(JsonFieldType.NUMBER).description("영상 업로드 수"),
                        fieldWithPath("point").type(JsonFieldType.NUMBER).description("포인트"),
                        fieldWithPath("revenue").type(JsonFieldType.NUMBER).description(""),
                        fieldWithPath("point_ratio").type(JsonFieldType.NUMBER).description(""),
                        fieldWithPath("revenue_ratio").type(JsonFieldType.NUMBER).description(""),
                        fieldWithPath("pushable").type(JsonFieldType.BOOLEAN).description("알람 동의 여부"),
                        fieldWithPath("created_at").type(JsonFieldType.NUMBER).description("회원 생성일"),
                        fieldWithPath("modified_at").type(JsonFieldType.NUMBER).description("회원 수정일").optional(),
                        fieldWithPath("revenue_modified_at").type(JsonFieldType.NUMBER).description("Revenue 수정일").optional(),
                        fieldWithPath("option_term_accepts[].term_type").type(JsonFieldType.STRING).description("선택 약관 동의 여부 - 선택 약관 종류")
                                .description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.TERM_TYPE)),
                        fieldWithPath("option_term_accepts[].is_accept").type(JsonFieldType.BOOLEAN).description("선택 약관 동의 여부 - 동의 여부")
                )));
    }
}
