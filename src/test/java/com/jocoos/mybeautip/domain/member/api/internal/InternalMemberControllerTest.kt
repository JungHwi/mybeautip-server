package com.jocoos.mybeautip.domain.member.api.internal

import com.jocoos.mybeautip.domain.member.dto.MemberBlockRequest
import com.jocoos.mybeautip.domain.member.dto.MemberRegistrationRequest
import com.jocoos.mybeautip.domain.video.api.internal.InternalVideoControllerTest
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.member.LegacyMemberService
import com.jocoos.mybeautip.member.Member
import com.jocoos.mybeautip.member.MemberRepository
import com.jocoos.mybeautip.member.block.Block
import com.jocoos.mybeautip.member.block.BlockService
import com.jocoos.mybeautip.member.block.BlockStatus
import com.jocoos.mybeautip.testutil.fixture.makeMember
import com.jocoos.mybeautip.testutil.fixture.makeVideoCategory
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.BDDMockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InternalMemberControllerTest(
    private val memberRepository: MemberRepository
) : RestDocsIntegrationTestSupport() {

    @MockBean
    private val legacyMemberService: LegacyMemberService? = null

    @MockBean
    private val blockService: BlockService? = null

    private lateinit var member: Member

    companion object {
        const val MEMBER_ID = "MEMBER-ID"
    }

    fun requestMember() : MemberRegistrationRequest {
        var request = MemberRegistrationRequest()
        request.id = 100
        request.username = "plus-member"
        return request
    }

    @BeforeAll
    fun beforeAll() {
        member = memberRepository.save(makeMember())
    }

    @AfterAll
    fun afterAll() {
        memberRepository.delete(member)
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

    @Test
    @Throws(Exception::class)
    fun memberBlockSuccess() {
        val block = Block(defaultAdmin.id, requestUser)
        val request = MemberBlockRequest(requestUser.id, true)
        block.changeStatus(BlockStatus.BLOCK)
        BDDMockito.given<Long>(legacyMemberService!!.currentMemberId()).willReturn(defaultAdmin.id)
        BDDMockito.given<Block>(
            blockService!!.changeTargetBlockStatus(defaultAdmin.id, request.targetId, request.isBlock)
        )
            .willReturn(block)
        val resultActions = mockMvc.perform(
            patch("/internal/1/member/block/")
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, defaultAdminToken)
                .header(MEMBER_ID, member.id)
                .contentType(APPLICATION_JSON)
                .characterEncoding("utf-8")
        )
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.member_id").value(defaultAdmin.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.target_id").value(requestUser.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.blocked").value(BlockStatus.BLOCK == block.status))
        restdocs(resultActions)
    }

    @Throws(java.lang.Exception::class)
    private fun restdocs(resultActions: ResultActions) {
        resultActions.andDo(
            document(
                "internal_block_member",
                requestFields(
                    fieldWithPath("target_id").type(NUMBER).description("블락 타겟 멤버 아이디"),
                    fieldWithPath("is_block").type(BOOLEAN).description("타겟 블락 여부")
                ),
                responseFields(
                    fieldWithPath("member_id").type(NUMBER).description("요청 멤버 아이디"),
                    fieldWithPath("target_id").type(NUMBER).description("블락한 멤버 아이디"),
                    fieldWithPath("blocked").type(BOOLEAN).description("블락 여부")
                )
            )
        )
    }
}
