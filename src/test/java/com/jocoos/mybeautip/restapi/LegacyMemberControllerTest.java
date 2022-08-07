package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.domain.term.dto.TermTypeResponse;
import com.jocoos.mybeautip.domain.term.service.MemberTermService;
import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.restapi.dto.SignupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.security.Principal;
import java.util.Collections;
import java.util.Optional;

import static com.jocoos.mybeautip.domain.member.code.MemberStatus.ACTIVE;
import static com.jocoos.mybeautip.domain.term.code.TermType.MARKETING_INFO;
import static com.jocoos.mybeautip.global.constant.MybeautipConstant.DEFAULT_AVATAR_URL;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LegacyMemberControllerTest extends RestDocsTestSupport {

    @MockBean
    MemberTermService memberTermService;

    @MockBean
    MemberRepository memberRepository;

    @DisplayName("GET /api/1/members/me - 내 정보 조회 성공")
    @Test
    void getMeSuccess() throws Exception {

        final boolean isAccept = true;
        final Long memberId = 1L;
        final String socialId = "123123123123";
        final String grantType = "naver";
        final String empty = "";

        SignupRequest request = createRequest(socialId, grantType, empty);

        Member member = new Member(request);
        member.setId(memberId);

        Principal principal = Mockito.mock(Principal.class);

        given(principal.getName()).willReturn(String.valueOf(memberId));
        given(memberTermService.getOptionalTermAcceptStatus(memberId))
                .willReturn(Collections.singletonList(TermTypeResponse.builder().termType(MARKETING_INFO).isAccept(isAccept).build()));
        given(memberRepository.findByIdAndDeletedAtIsNull(memberId)).willReturn(Optional.of(member));

        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/members/me")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(memberId))
                .andExpect(jsonPath("$.tag").isString())
                .andExpect(jsonPath("$.status").value(ACTIVE.name()))
                .andExpect(jsonPath("$.username").isString())
                .andExpect(jsonPath("$.email").isString())
                .andExpect(jsonPath("$.avatar_url").value(DEFAULT_AVATAR_URL))
                .andExpect(jsonPath("$.permission.chat_post").value(true))
                .andExpect(jsonPath("$.permission.comment_post").value(true))
                .andExpect(jsonPath("$.permission.live_post").value(false))
                .andExpect(jsonPath("$.permission.motd_post").value(false))
                .andExpect(jsonPath("$.permission.revenue_return").value(true))
                .andExpect(jsonPath("$.follower_count").value(0))
                .andExpect(jsonPath("$.following_count").value(0))
                .andExpect(jsonPath("$.video_count").value(0))
                .andExpect(jsonPath("$.point").value(0))
                .andExpect(jsonPath("$.revenue").value(0))
                .andExpect(jsonPath("$.point_ratio").value(1))
                .andExpect(jsonPath("$.revenue_ratio").value(3))
                .andExpect(jsonPath("$.pushable").value(true))
                .andExpect(jsonPath("$.option_term_accepts[0].term_type").value(MARKETING_INFO.name()))
                .andExpect(jsonPath("$.option_term_accepts[0].is_accept").value(isAccept))
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
                        fieldWithPath("username").type(JsonFieldType.STRING).description("멤버 이름"),
                        fieldWithPath("email").type(JsonFieldType.STRING).description("멤버 이메일"),
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
                        fieldWithPath("option_term_accepts[].term_type").type(JsonFieldType.STRING).description("선택 약관 동의 여부 - 선택 약관 종류")
                                .description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.TERM_TYPE)),
                        fieldWithPath("option_term_accepts[].is_accept").type(JsonFieldType.BOOLEAN).description("선택 약관 동의 여부 - 동의 여부"))));
    }

    private SignupRequest createRequest(String socialId, String grantType, String empty) {
        SignupRequest request = new SignupRequest();
        request.setSocialId(socialId);
        request.setGrantType(grantType);
        request.setUsername(empty);
        request.setEmail(empty);
        request.setAvatarUrl(empty);
        return request;
    }
}
