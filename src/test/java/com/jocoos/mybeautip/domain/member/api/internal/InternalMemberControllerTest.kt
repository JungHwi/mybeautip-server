package com.jocoos.mybeautip.domain.member.api.internal

import com.jocoos.mybeautip.domain.member.code.MemberStatus.DORMANT
import com.jocoos.mybeautip.domain.member.converter.DormantMemberConverter
import com.jocoos.mybeautip.domain.member.dto.MemberRegistrationRequest
import com.jocoos.mybeautip.domain.member.persistence.domain.DormantMember
import com.jocoos.mybeautip.domain.member.persistence.repository.DormantMemberRepository
import com.jocoos.mybeautip.domain.member.persistence.repository.UsernameCombinationWordRepository
import com.jocoos.mybeautip.domain.popup.code.PopupType.WAKEUP
import com.jocoos.mybeautip.domain.popup.persistence.repository.PopupRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class InternalMemberControllerTest(
) : RestDocsIntegrationTestSupport() {

    fun requestMember() : MemberRegistrationRequest {
        var request = MemberRegistrationRequest()
        request.id = 100
        request.username = "plus-member"
        return request
    }

    @Test
    fun signUp() {
        var request: MemberRegistrationRequest = requestMember()

        // when & then
        val result: ResultActions = mockMvc.perform(
            post("/internal/1/member")
                .header(AUTHORIZATION, requestInternalToken)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_add_member",
                requestFields(
                    fieldWithPath("id").type(NUMBER).description("멤버 아이디"),
                    fieldWithPath("username").type(STRING).description("멤버 닉네임").optional(),
                    fieldWithPath("avatar_url").type(STRING).description("아바타 이미지 URL").optional()
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("멤버 아이디"),
                    fieldWithPath("username").type(STRING).description("멤버 닉네임"),
                    fieldWithPath("avatar_url").type(STRING).description("아바타 이미지 URL"),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(MEMBER_STATUS))
                )
            )
        )
    }
}
