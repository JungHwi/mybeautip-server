package com.jocoos.mybeautip.restapi

import com.jocoos.mybeautip.domain.term.code.TermType
import com.jocoos.mybeautip.domain.term.persistence.repository.TermRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.testutil.fixture.makeMember
import com.jocoos.mybeautip.testutil.fixture.makeTerm
import com.jocoos.mybeautip.member.Member
import com.jocoos.mybeautip.member.MemberRepository
import com.jocoos.mybeautip.security.JwtTokenProvider
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class LegacyMemberControllerTest(
    private val memberRepository: MemberRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val termRepository: TermRepository
) : RestDocsIntegrationTestSupport() {

    @DisplayName("GET /api/1/members/me - 내 정보 조회 성공")
    @Test
    fun getMeSuccess() {

        // given
        termRepository.save(makeTerm(type = TermType.MARKETING_INFO))
        val member: Member = memberRepository.save(makeMember(link = 2))
        val accessToken: String = "Bearer " + jwtTokenProvider.auth(member).accessToken

        // when & then
        val resultActions: ResultActions = mockMvc
            .perform(
                get("/api/1/members/me")
                    .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        resultDocs(resultActions)
    }

    private fun resultDocs(resultActions: ResultActions) {
        resultActions.andDo(
            document(
                "get my setting",
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("멤버 아이디"),
                    fieldWithPath("tag").type(STRING).description("멤버 태그"),
                    fieldWithPath("status").type(STRING).description("멤버 상태")
                        .description(generateLinkCode(MEMBER_STATUS)),
                    fieldWithPath("grant_type").type(STRING).description(generateLinkCode(GRANT_TYPE)).optional(),
                    fieldWithPath("username").type(STRING).description("멤버 이름"),
                    fieldWithPath("email").type(STRING).description("멤버 이메일").optional(),
                    fieldWithPath("phone_number").type(STRING).description("전화 번호").optional(),
                    fieldWithPath("avatar_url").type(STRING).description("멤버 아바타 이미지 url"),
                    fieldWithPath("permission.chat_post").type(BOOLEAN).description(""),
                    fieldWithPath("permission.comment_post").type(BOOLEAN).description(""),
                    fieldWithPath("permission.live_post").type(BOOLEAN).description(""),
                    fieldWithPath("permission.motd_post").type(BOOLEAN).description(""),
                    fieldWithPath("permission.revenue_return").type(BOOLEAN).description(""),
                    fieldWithPath("follower_count").type(NUMBER).description("팔로워 수"),
                    fieldWithPath("following_count").type(NUMBER).description("팔로잉 수"),
                    fieldWithPath("video_count").type(NUMBER).description("영상 업로드 수"),
                    fieldWithPath("point").type(NUMBER).description("포인트"),
                    fieldWithPath("revenue").type(NUMBER).description(""),
                    fieldWithPath("point_ratio").type(NUMBER).description(""),
                    fieldWithPath("revenue_ratio").type(NUMBER).description(""),
                    fieldWithPath("pushable").type(BOOLEAN).description("알람 동의 여부"),
                    fieldWithPath("created_at").type(NUMBER).description("회원 생성일"),
                    fieldWithPath("modified_at").type(NUMBER).description("회원 수정일").optional(),
                    fieldWithPath("revenue_modified_at").type(NUMBER).description("Revenue 수정일").optional(),
                    fieldWithPath("option_term_accepts[].term_type").type(STRING).description("선택 약관 동의 여부 - 선택 약관 종류")
                        .description(generateLinkCode(TERM_TYPE)),
                    fieldWithPath("option_term_accepts[].is_accept").type(BOOLEAN).description("선택 약관 동의 여부 - 동의 여부")
                )
            )
        )
    }
}
