package com.jocoos.mybeautip.domain.member.api.admin

import com.jocoos.mybeautip.domain.member.dto.MemoRequest
import com.jocoos.mybeautip.domain.member.persistence.domain.MemberMemo
import com.jocoos.mybeautip.domain.member.persistence.repository.MemberMemoRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat
import com.jocoos.mybeautip.testutil.fixture.makeMember
import com.jocoos.mybeautip.testutil.fixture.makeMemberMemo
import com.jocoos.mybeautip.member.Member
import com.jocoos.mybeautip.member.MemberRepository
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AdminMemberMemoControllerTest(
    private val memberRepository: MemberRepository,
    private val memberMemoRepository: MemberMemoRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun writeMemo() {

        // given
        val member: Member = memberRepository.save(makeMember())
        val request = MemoRequest("memo")

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                post("/admin/member/{member_id}/memo", member.id)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_write_member_memo",
                pathParameters(
                    parameterWithName("member_id").description("회원 ID")
                ),
                requestFields(
                    fieldWithPath("content").type(STRING).description("메모 내용")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("괸리자 작성 메모 ID"),
                    fieldWithPath("content").type(STRING).description("괸리자 작성 메모 내용"),
                    fieldWithPath("member").type(OBJECT).description("괸리자 작성 메모 작성자"),
                    fieldWithPath("member.id").type(NUMBER).description("괸리자 작성 메모 작성자 ID"),
                    fieldWithPath("member.username").type(STRING).description("괸리자 작성 메모 작성자 닉네임"),
                    fieldWithPath("created_at").type(STRING).description("생성일자").attributes(getZonedDateFormat())
                )
            )
        )
    }


    @Test
    fun editMemo() {

        // given
        val target: Member = memberRepository.save(makeMember())
        val memberMemo: MemberMemo =
            memberMemoRepository.save(makeMemberMemo(target = target, createdBy = defaultAdmin))
        val request = MemoRequest("memo")

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                patch("/admin/member/{member_id}/memo/{memo_id}", target.id, memberMemo.id)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_edit_member_memo",
                pathParameters(
                    parameterWithName("member_id").description("회원 ID"),
                    parameterWithName("memo_id").description("메모 ID")
                ),
                requestFields(
                    fieldWithPath("content").type(STRING).description("메모 내용")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("괸리자 작성 메모 ID")
                )
            )
        )
    }

    @Test
    fun deleteMemo() {

        // given
        val target: Member = memberRepository.save(makeMember())
        val memberMemo: MemberMemo =
            memberMemoRepository.save(makeMemberMemo(target = target, createdBy = defaultAdmin))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                delete("/admin/member/{member_id}/memo/{memo_id}", target.id, memberMemo.id)
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_delete_member_memo",
                pathParameters(
                    parameterWithName("member_id").description("회원 ID"),
                    parameterWithName("memo_id").description("메모 ID")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("괸리자 작성 메모 ID")
                )
            )
        )
    }
}
