package com.jocoos.mybeautip.domain.term.api.front;

import com.jocoos.mybeautip.domain.term.dto.TermTypeRequest;
import com.jocoos.mybeautip.domain.term.dto.TermTypeResponse;
import com.jocoos.mybeautip.domain.term.service.MemberTermService;
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport;
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.ResultActions;

import static com.jocoos.mybeautip.domain.term.code.TermType.MARKETING_INFO;
import static com.jocoos.mybeautip.domain.term.code.TermType.OVER_14;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MemberTermControllerTest extends RestDocsIntegrationTestSupport {
    @MockBean
    private MemberTermService memberTermService;

    @MockBean
    private LegacyMemberService legacyMemberService;

    @DisplayName("[PATCH] /api/1/member/me/term/option/change - 선택 약관 동의로 변경 성공")
    @Test
    void changeOptionalTermTrueByTypeSuccess() throws Exception {

        TermTypeRequest request = new TermTypeRequest(MARKETING_INFO, true);
        TermTypeResponse response = TermTypeResponse.builder().termType(MARKETING_INFO).isAccept(true).build();

        given(legacyMemberService.currentMemberId()).willReturn(requestUser.getId());
        given(memberTermService.changeOptionalTermByType(request.getTermType(), requestUser.getId(), request.getIsAccept()))
                .willReturn(response);

        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/api/1/member/me/term/option/change")
                        .header(HttpHeaders.AUTHORIZATION, requestUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.term_type").value(MARKETING_INFO.toString()))
                .andExpect(jsonPath("$.is_accept").value(true))
                .andDo(print());

        restDocs(resultActions);
    }

    @DisplayName("[PATCH] /api/1/member/me/term/option/change - 필수 약관은 선택 변경 불가")
    @Test
    void changeOptionalTermFail() throws Exception {
        TermTypeRequest request = new TermTypeRequest(OVER_14, false);
        BadRequestException exception = new BadRequestException("only optional term can change");

        given(legacyMemberService.currentMemberId()).willReturn(requestUser.getId());
        given(memberTermService.changeOptionalTermByType(request.getTermType(), requestUser.getId(), request.getIsAccept()))
                .willThrow(exception);

        mockMvc.perform(patch("/api/1/member/me/term/option/change")
                        .header(HttpHeaders.AUTHORIZATION, requestUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(exception.getMessage()))
                .andExpect(jsonPath("$.error_description").value(exception.getDescription()))
                .andDo(print());


    }

    private long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(((MyBeautipUserDetails) authentication.getPrincipal()).getUsername());
    }

    private void restDocs(ResultActions resultActions) throws Exception {
        resultActions.andDo(document("change_choice_optional_term",
                requestFields(
                        fieldWithPath("term_type").type(JsonFieldType.STRING).description("약관 타입")
                                .description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.TERM_TYPE))
                        ,
                        fieldWithPath("is_accept").type(JsonFieldType.BOOLEAN).description("동의 여부")
                ),
                responseFields(
                        fieldWithPath("term_type").type(JsonFieldType.STRING).description("약관 타입")
                                .description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.TERM_TYPE))
                        ,
                        fieldWithPath("is_accept").type(JsonFieldType.BOOLEAN).description("동의 여부")
                )
        ));
    }

}
