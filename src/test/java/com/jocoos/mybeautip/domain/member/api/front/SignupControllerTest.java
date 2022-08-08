package com.jocoos.mybeautip.domain.member.api.front;

import com.jocoos.mybeautip.domain.member.dto.MemberEntireInfo;
import com.jocoos.mybeautip.domain.member.service.MemberSignupService;
import com.jocoos.mybeautip.domain.term.code.TermType;
import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.restapi.dto.SignupRequest;
import com.jocoos.mybeautip.security.AccessTokenResponse;
import com.jocoos.mybeautip.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.jocoos.mybeautip.domain.member.code.MemberStatus.ACTIVE;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
class SignupControllerTest extends RestDocsTestSupport {

    @MockBean
    MemberSignupService signupService;

    @Autowired
    private JwtTokenProvider provider;

    @BeforeEach
    void setSecurity(final WebApplicationContext context,
               final RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .alwaysDo(MockMvcResultHandlers.print())
                .alwaysDo(restDocs)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @DisplayName("[POST] /api/1/member/signup - 회원가입 성공")
    @Test
    void signupSuccess() throws Exception {

        // given
        final String accessToken = createAccessToken();
        final String socialId = "123123123";
        final String grantType = "naver";
        final String empty = "";
        final Set<TermType> termTypes =
                new HashSet<>(Arrays.asList(TermType.OVER_14, TermType.PRIVACY_POLICY, TermType.TERMS_OF_SERVICE));

        SignupRequest request = createRequest(socialId, grantType, empty, termTypes);
        Member member = new Member(request);
        MemberInfo memberInfo = new MemberInfo(member);
        AccessTokenResponse tokenResponse = provider.auth(member);
        MemberEntireInfo response = MemberEntireInfo.builder()
                .member(memberInfo)
                .token(tokenResponse)
                .build();

        given(signupService.signup(request)).willReturn(response);

        // when, then
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/api/1/member/signup")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.member.tag").value(member.getTag()))
                .andExpect(jsonPath("$.member.status").value(ACTIVE.name()))
                .andExpect(jsonPath("$.member.username").value(member.getUsername()))
                .andExpect(jsonPath("$.member.email").value(member.getEmail()))
                .andExpect(jsonPath("$.member.phone_number").value(memberInfo.getPhoneNumber()))
                .andExpect(jsonPath("$.member.avatar_url").value(member.getAvatarUrl()))
                .andExpect(jsonPath("$.member.permission.chat_post").value(memberInfo.getPermission().getChatPost()))
                .andExpect(jsonPath("$.member.permission.comment_post").value(memberInfo.getPermission().getCommentPost()))
                .andExpect(jsonPath("$.member.permission.live_post").value(memberInfo.getPermission().getLivePost()))
                .andExpect(jsonPath("$.member.permission.motd_post").value(memberInfo.getPermission().getMotdPost()))
                .andExpect(jsonPath("$.member.permission.revenue_return").value(memberInfo.getPermission().getRevenueReturn()))
                .andExpect(jsonPath("$.member.follower_count").value(member.getFollowerCount()))
                .andExpect(jsonPath("$.member.following_count").value(member.getFollowingCount()))
                .andExpect(jsonPath("$.member.video_count").value(member.getPublicVideoCount()))
                .andExpect(jsonPath("$.token.scope").value(tokenResponse.getScope()))
                .andExpect(jsonPath("$.token.jti").value(tokenResponse.getJti()))
                .andExpect(jsonPath("$.token.access_token").value(tokenResponse.getAccessToken()))
                .andExpect(jsonPath("$.token.token_type").value(tokenResponse.getTokenType()))
                .andExpect(jsonPath("$.token.refresh_token").value(tokenResponse.getRefreshToken()))
                .andExpect(jsonPath("$.token.expires_in").value(tokenResponse.getExpiresIn()));

        restdocs(resultActions);
    }

    private void restdocs(ResultActions resultActions) throws Exception {
        resultActions.andDo(document("signup",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + Access Token")
                ),
                requestFields(
                        fieldWithPath("social_id").type(JsonFieldType.STRING).description("소셜 ID"),
                        fieldWithPath("grant_type").type(JsonFieldType.STRING).description("소셜 서비스 타입"),
                        fieldWithPath("username").type(JsonFieldType.STRING).description("멤버 이름"),
                        fieldWithPath("email").type(JsonFieldType.STRING).description("멤버 이메일"),
                        fieldWithPath("avatar_url").type(JsonFieldType.STRING).description("멤버 아바타 이미지 url"),
                        fieldWithPath("term_types[]").type(JsonFieldType.ARRAY).description("동의한 약관 목록")
                                .description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.TERM_TYPE))
                        ),
                responseFields(
                        fieldWithPath("member.tag").type(JsonFieldType.STRING).description("멤버 태그"),
                        fieldWithPath("member.status").type(JsonFieldType.STRING).description("멤버 상태")
                                .description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.MEMBER_STATUS)),
                        fieldWithPath("member.username").type(JsonFieldType.STRING).description("멤버 이름"),
                        fieldWithPath("member.email").type(JsonFieldType.STRING).description("멤버 이메일"),
                        fieldWithPath("member.phone_number").type(JsonFieldType.STRING).description("멤버 전화번호"),
                        fieldWithPath("member.avatar_url").type(JsonFieldType.STRING).description("멤버 아바타 이미지 url"),
                        fieldWithPath("member.permission.chat_post").type(JsonFieldType.BOOLEAN).description(""),
                        fieldWithPath("member.permission.comment_post").type(JsonFieldType.BOOLEAN).description(""),
                        fieldWithPath("member.permission.live_post").type(JsonFieldType.BOOLEAN).description(""),
                        fieldWithPath("member.permission.motd_post").type(JsonFieldType.BOOLEAN).description(""),
                        fieldWithPath("member.permission.revenue_return").type(JsonFieldType.BOOLEAN).description(""),
                        fieldWithPath("member.follower_count").type(JsonFieldType.NUMBER).description("팔로워 수"),
                        fieldWithPath("member.following_count").type(JsonFieldType.NUMBER).description("팔로잉 수"),
                        fieldWithPath("member.video_count").type(JsonFieldType.NUMBER).description("비디오 수"),
                        fieldWithPath("token.scope").type(JsonFieldType.STRING).description(""),
                        fieldWithPath("token.jti").type(JsonFieldType.STRING).description("JTI"),
                        fieldWithPath("token.access_token").type(JsonFieldType.STRING).description("Access Token"),
                        fieldWithPath("token.token_type").type(JsonFieldType.STRING).description("토큰 타입"),
                        fieldWithPath("token.refresh_token").type(JsonFieldType.STRING).description("Refresh Token"),
                        fieldWithPath("token.expires_in").type(JsonFieldType.NUMBER).description("토큰 만료 시간"))));
    }

    private SignupRequest createRequest(String socialId, String grantType, String empty, Set<TermType> termTypes) {
        SignupRequest request = new SignupRequest();
        request.setSocialId(socialId);
        request.setGrantType(grantType);
        request.setUsername(empty);
        request.setEmail(empty);
        request.setAvatarUrl(empty);
        request.setTermTypes(termTypes);
        return request;
    }

    private String createAccessToken() {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken("guest:" + System.currentTimeMillis(), "");
        AccessTokenResponse accessTokenResponse = provider.generateToken(authenticationToken);
        return accessTokenResponse.getAccessToken();
    }
}
