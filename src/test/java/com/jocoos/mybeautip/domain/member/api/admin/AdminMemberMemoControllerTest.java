package com.jocoos.mybeautip.domain.member.api.admin;

import com.jocoos.mybeautip.domain.member.dto.MemoRequest;
import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminMemberMemoControllerTest extends RestDocsTestSupport {

    @Transactional
    @WithUserDetails(value = "1", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Test
    void writeMemo() throws Exception {
        MemoRequest request = new MemoRequest("memo");

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/admin/member/{member_id}/memo", 4)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_write_member_memo",
                pathParameters(
                        parameterWithName("member_id").description("회원 ID")
                ),
                requestFields(
                        fieldWithPath("memo").type(JsonFieldType.STRING).description("메모")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("괸리자 작성 메모 ID"),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("괸리자 작성 메모 내용"),
                        fieldWithPath("member").type(JsonFieldType.OBJECT).description("괸리자 작성 메모 작성자"),
                        fieldWithPath("member.id").type(JsonFieldType.NUMBER).description("괸리자 작성 메모 작성자 ID"),
                        fieldWithPath("member.username").type(JsonFieldType.STRING).description("괸리자 작성 메모 작성자 닉네임"),
                        fieldWithPath("created_at").type(JsonFieldType.STRING).description("생성일자").attributes(getZonedDateFormat())
                )));
    }

    @Transactional
    @WithUserDetails(value = "1", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Test
    void editMemo() throws Exception {
        MemoRequest request = new MemoRequest("memo");

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/admin/member/{member_id}/memo/{memo_id}", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_edit_member_memo",
                pathParameters(
                        parameterWithName("member_id").description("회원 ID"),
                        parameterWithName("memo_id").description("메모 ID")
                ),
                requestFields(
                        fieldWithPath("memo").type(JsonFieldType.STRING).description("메모")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("괸리자 작성 메모 ID")
                )));
    }

    @Transactional
    @WithUserDetails(value = "1", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Test
    void deleteMemo() throws Exception {

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .delete("/admin/member/{member_id}/memo/{memo_id}", 1, 1))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_delete_member_memo",
                pathParameters(
                        parameterWithName("member_id").description("회원 ID"),
                        parameterWithName("memo_id").description("메모 ID")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("괸리자 작성 메모 ID")
                )));
    }
}
