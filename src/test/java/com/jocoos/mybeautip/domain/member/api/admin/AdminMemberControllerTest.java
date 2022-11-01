package com.jocoos.mybeautip.domain.member.api.admin;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminMemberControllerTest extends RestDocsTestSupport {


    @Test
    void getMemberStatuses() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/member/status"))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_member_status",
                responseFields(
                        fieldWithPath("[].status").type(JsonFieldType.STRING).description(generateLinkCode(MEMBER_STATUS)).optional(),
                        fieldWithPath("[].status_name").type(JsonFieldType.STRING).description("회원 상태 이름"),
                        fieldWithPath("[].count").type(JsonFieldType.NUMBER).description("회원수")
                )));
    }

    @Test
    void getMemberBasicDetail() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/member/{member_id}", 4))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_member_basic_detail",
                pathParameters(
                        parameterWithName("member_id").description("회원 ID")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 ID"),
                        fieldWithPath("avatar_url").type(JsonFieldType.STRING).description("아바타 이미지 URL"),
                        fieldWithPath("username").type(JsonFieldType.STRING).description("닉네임"),
                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일").optional(),
                        fieldWithPath("point").type(JsonFieldType.NUMBER).description("보유 포인트"),
                        fieldWithPath("expiry_point").type(JsonFieldType.NUMBER).description("이번달 만료예정 포인트"),
                        fieldWithPath("is_pushable").type(JsonFieldType.BOOLEAN).description("푸시 알림 동의 여부"),
                        fieldWithPath("is_agree_marketing_term").type(JsonFieldType.BOOLEAN).description("마케팅 동의 여부"),
                        fieldWithPath("grant_type").type(JsonFieldType.STRING).description("가입 경로").optional(),
                        fieldWithPath("created_at").type(JsonFieldType.STRING).description("가입일자"),
                        fieldWithPath("modified_at").type(JsonFieldType.STRING).description("수정일자"),
                        fieldWithPath("community_count").type(JsonFieldType.NUMBER).description("게시물 작성 수"),
                        fieldWithPath("community_comment_count").type(JsonFieldType.NUMBER).description("게시물 댓글 작성 수"),
                        fieldWithPath("video_comment_count").type(JsonFieldType.NUMBER).description("비디오 댓글 작성 수"),
                        fieldWithPath("invited_friend_count").type(JsonFieldType.NUMBER).description("초대한 친구들 수"),
                        fieldWithPath("age_group").type(JsonFieldType.NUMBER).description("연령대").optional(),
                        fieldWithPath("skin_type").type(JsonFieldType.STRING).description(generateLinkCode(SKIN_TYPE)).optional(),
                        fieldWithPath("skin_worry").type(JsonFieldType.STRING).description(generateLinkCode(SKIN_WORRY)).optional(),
                        fieldWithPath("address").type(JsonFieldType.STRING).description("주소").optional())));
    }

    @Test
    void getMemberPointHistory() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/member/{member_id}/point", 4))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_member_point_history",
                pathParameters(
                        parameterWithName("member_id").description("회원 ID")
                ),
                responseFields(
                        fieldWithPath("total").type(JsonFieldType.NUMBER).description("총 포인트 내역 수"),
                        fieldWithPath("content").type(JsonFieldType.ARRAY).description("포인트 내역 목록"),
                        fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("포인트 ID"),
                        fieldWithPath("content.[].status").type(JsonFieldType.STRING).description(generateLinkCode(POINT_STATUS)),
                        fieldWithPath("content.[].reason").type(JsonFieldType.STRING).description("이유"),
                        fieldWithPath("content.[].point").type(JsonFieldType.NUMBER).description("포인트"),
                        fieldWithPath("content.[].earned_at").type(JsonFieldType.STRING).description("생성일자"),
                        fieldWithPath("content.[].expiry_at").type(JsonFieldType.STRING).description("만료일자"))));
    }
}
