package com.jocoos.mybeautip.domain.member.api.front

import com.jocoos.mybeautip.domain.member.code.MemberStatus.DORMANT
import com.jocoos.mybeautip.domain.member.converter.DormantMemberConverter
import com.jocoos.mybeautip.domain.member.persistence.domain.DormantMember
import com.jocoos.mybeautip.domain.member.persistence.repository.DormantMemberRepository
import com.jocoos.mybeautip.domain.member.persistence.repository.UsernameCombinationWordRepository
import com.jocoos.mybeautip.domain.popup.code.PopupType.WAKEUP
import com.jocoos.mybeautip.domain.popup.persistence.repository.PopupRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.BUTTON_LINK_TYPE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.POPUP_DISPLAY_TYPE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.global.dto.single.LongDto
import com.jocoos.mybeautip.testutil.fixture.makeMember
import com.jocoos.mybeautip.testutil.fixture.makePopup
import com.jocoos.mybeautip.testutil.fixture.makeUsernameCombinationWord
import com.jocoos.mybeautip.member.Member
import com.jocoos.mybeautip.member.MemberRepository
import com.jocoos.mybeautip.security.JwtTokenProvider
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class MemberControllerTest(
    private val usernameCombinationWordRepository: UsernameCombinationWordRepository,
    private val dormantMemberRepository: DormantMemberRepository,
    private val dormantMemberConverter: DormantMemberConverter,
    private val jwtTokenProvider: JwtTokenProvider,
    private val popupRepository: PopupRepository,
    private val memberRepository: MemberRepository
) : RestDocsIntegrationTestSupport() {
    @Test
    fun getRandomUsername() {

        // given
        usernameCombinationWordRepository.saveAll(
            listOf(
                makeUsernameCombinationWord(1, "first"),
                makeUsernameCombinationWord(2, "second")
            )
        )

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/member/random-username")
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_random_username",
                responseFields(
                    fieldWithPath("string").type(STRING).description("랜덤으로 생성된 유저 닉네임")
                )
            )
        )
    }

    @Test
    fun wakeup() {

        // given
        usernameCombinationWordRepository.saveAll(
            listOf(
                makeUsernameCombinationWord(1, "first"),
                makeUsernameCombinationWord(2, "second")
            )
        )


        val accessToken = jwtTokenProvider.generateToken(
            UsernamePasswordAuthenticationToken(
                "guest:" + System.currentTimeMillis(),
                ""
            )
        ).accessToken

        popupRepository.save(makePopup(type = WAKEUP))

        val member: Member = memberRepository.save(makeMember(status = DORMANT))
        val dormantMember: DormantMember = dormantMemberConverter.convertForDormant(member)
        dormantMemberRepository.save(dormantMember)
        val longDto = LongDto(member.id)

        // when & then
        val result: ResultActions = mockMvc.perform(
            patch("/api/1/member/wakeup")
                .header(AUTHORIZATION, "Bearer $accessToken")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longDto))
        )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "member_wakeup",
                requestFields(
                    fieldWithPath("number").type(NUMBER).description("휴면해제 멤버 아이디")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("popup id"),
                    fieldWithPath("image_url").type(STRING).description("팝업 이미지 URL"),
                    fieldWithPath("display_type").type(STRING).description(generateLinkCode(POPUP_DISPLAY_TYPE)),
                    fieldWithPath("button_list").type(ARRAY).description("버튼 정보 목록"),
                    fieldWithPath("button_list.[].name").type(STRING).description("버튼명"),
                    fieldWithPath("button_list.[].link_type").type(STRING)
                        .description(generateLinkCode(BUTTON_LINK_TYPE))
                )
            )
        )
    }
}
