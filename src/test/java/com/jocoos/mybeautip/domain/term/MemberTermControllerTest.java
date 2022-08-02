package com.jocoos.mybeautip.domain.term;

import com.jocoos.mybeautip.domain.term.dto.TermTypeRequest;
import com.jocoos.mybeautip.domain.term.dto.TermTypeResponse;
import com.jocoos.mybeautip.domain.term.service.MemberTermService;
import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.security.annotation.WithMockCustomUser;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import static com.jocoos.mybeautip.domain.term.code.TermType.MARKETING_INFO;
import static com.jocoos.mybeautip.domain.term.code.TermType.OVER_14;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@AutoConfigureMockMvc(addFilters = false)
//@WebMvcTest(value = MemberTermController.class,
//        excludeFilters = {
//                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
//        })
class MemberTermControllerTest extends RestDocsTestSupport {

    @Autowired
    private WebApplicationContext context;
    @MockBean
    private MemberTermService memberTermService;

    @MockBean
    private LegacyMemberService legacyMemberService;

//    @BeforeEach
//    void init() {
//        mvc = MockMvcBuilders.webAppContextSetup(context)
//                .apply(SecurityMockMvcConfigurers.springSecurity())
//                .build();
//    }

    @DisplayName("[PATCH] /api/1/members/me/terms/option/change - 선택 약관 동의로 변경 성공")
    @WithMockCustomUser
    @Test
    void changeOptionalTermTrueByTypeSuccess() throws Exception {

        TermTypeRequest request = new TermTypeRequest(MARKETING_INFO, true);
        TermTypeResponse response = TermTypeResponse.builder().termType(MARKETING_INFO).isAccept(true).build();

        long userId = getUserId();

        given(legacyMemberService.currentMemberId()).willReturn(userId);
        given(memberTermService.changeOptionalTermByType(request.getTermType(), userId, request.getIsAccept()))
                .willReturn(response);

        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/api/1/members/me/terms/option/change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.term_type").value(MARKETING_INFO.toString()))
                .andExpect(jsonPath("$.is_accept").value(true))
                .andDo(print());

        restDocs(resultActions);
    }

    @DisplayName("[PATCH] /api/1/members/me/terms/option/change - 필수 약관은 선택 변경 불가")
    @WithMockCustomUser
    @Test
    void changeOptionalTermFail() throws Exception {
        TermTypeRequest request = new TermTypeRequest(OVER_14, false);
        BadRequestException exception = new BadRequestException("only optional term can change");
        long userId = getUserId();

        given(legacyMemberService.currentMemberId()).willReturn(userId);
        given(memberTermService.changeOptionalTermByType(request.getTermType(), userId, request.getIsAccept()))
                .willThrow(exception);

        mockMvc.perform(patch("/api/1/members/me/terms/option/change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
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
                        fieldWithPath("term_type").type(JsonFieldType.STRING).description("약관 타입"),
                        fieldWithPath("is_accept").type(JsonFieldType.BOOLEAN).description("동의 여부")
                ),
                responseFields(
                        fieldWithPath("term_type").type(JsonFieldType.STRING).description("약관 타입"),
                        fieldWithPath("is_accept").type(JsonFieldType.BOOLEAN).description("동의 여부")
                )
        ));
    }

}
