package com.jocoos.mybeautip.domain.member.api.internal

import com.jocoos.mybeautip.domain.member.code.MemberStatus
import com.jocoos.mybeautip.domain.member.dto.MemberBlockRequest
import com.jocoos.mybeautip.domain.member.dto.MemberRegistrationRequest
import com.jocoos.mybeautip.domain.member.dto.MemberStatusRequest
import com.jocoos.mybeautip.domain.member.persistence.repository.InfluencerRepository
import com.jocoos.mybeautip.domain.member.persistence.repository.MemberActivityCountRepository
import com.jocoos.mybeautip.domain.term.code.TermType
import com.jocoos.mybeautip.domain.term.persistence.repository.TermRepository
import com.jocoos.mybeautip.domain.video.api.internal.InternalVideoControllerTest
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.member.LegacyMemberService
import com.jocoos.mybeautip.member.Member
import com.jocoos.mybeautip.member.MemberRepository
import com.jocoos.mybeautip.member.block.Block
import com.jocoos.mybeautip.member.block.BlockService
import com.jocoos.mybeautip.member.block.BlockStatus
import com.jocoos.mybeautip.testutil.fixture.*
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
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InternalMemberControllerTest(
    private val memberRepository: MemberRepository,
    private val termRepository: TermRepository,
    private val memberActivityCountRepository: MemberActivityCountRepository,
    private val influencerRepository: InfluencerRepository
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

    @Test
    fun getMembers() {

        // given
        val member = memberRepository.save(makeMember())
        termRepository.save(makeTerm(type = TermType.MARKETING_INFO))
        memberActivityCountRepository.save(makeActivityCount(member))
        influencerRepository.save(makeInfluencer(member))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/admin/member")
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_get_members",
                RequestDocumentation.requestParameters(
                    RequestDocumentation.parameterWithName("page").attributes(DocumentAttributeGenerator.getDefault(1))
                        .description("페이지 넘버").optional(),
                    RequestDocumentation.parameterWithName("size").attributes(DocumentAttributeGenerator.getDefault(10))
                        .description("페이지 내 컨텐츠 개수").optional(),
                    RequestDocumentation.parameterWithName("status").description("멤버 상태").optional(),
                    RequestDocumentation.parameterWithName("is_influencer")
                        .description("인플루언서 여부 > " + generateLinkCode(BOOLEAN_TYPE)).optional(),
                    RequestDocumentation.parameterWithName("grant_type").description("가입").optional(),
                    RequestDocumentation.parameterWithName("search").description("검색 (검색필드,검색어) 형식").optional(),
                    RequestDocumentation.parameterWithName("start_at").description("검색 시작 일자").optional(),
                    RequestDocumentation.parameterWithName("end_at").description("검색 종료 일자").optional()
                ),
                responseFields(
                    fieldWithPath("total").type(NUMBER).description("총 회원 수"),
                    fieldWithPath("content").type(ARRAY).description("회원 목록"),
                    fieldWithPath("content.[].id").type(NUMBER).description("회원 ID"),
                    fieldWithPath("content.[].status").type(STRING).description(generateLinkCode(MEMBER_STATUS)),
                    fieldWithPath("content.[].grant_type").type(STRING).description("가입 경로").optional(),
                    fieldWithPath("content.[].avatar_url").type(STRING).description("아바타 이미지 URL"),
                    fieldWithPath("content.[].username").type(STRING).description("닉네임"),
                    fieldWithPath("content.[].email").type(STRING).description("이메일").optional(),
                    fieldWithPath("content.[].point").type(NUMBER).description("보유 포인트"),
                    fieldWithPath("content.[].community_count").type(NUMBER).description("게시물 작성 수"),
                    fieldWithPath("content.[].comment_count").type(NUMBER).description("댓글 작성 수"),
                    fieldWithPath("content.[].report_count").type(NUMBER).description("신고된 수"),
                    fieldWithPath("content.[].order_count").type(NUMBER).description("주문 수"),
                    fieldWithPath("content.[].is_pushable").type(BOOLEAN).description("푸시 알림 동의 여부"),
                    fieldWithPath("content.[].is_agree_marketing_term").type(BOOLEAN).description("마케팅 동의 여부"),
                    fieldWithPath("content.[].influencer_info").type(OBJECT).description("인플루언서 정보").optional(),
                    fieldWithPath("content.[].influencer_info.status").type(STRING).description(generateLinkCode(INFLUENCER_STATUS)),
                    fieldWithPath("content.[].influencer_info.broadcast_count").type(NUMBER).description("방송 횟수"),
                    fieldWithPath("content.[].influencer_info.earned_at").type(STRING).description("인플루언서 권한 획득 일시").attributes(
                        DocumentAttributeGenerator.getZonedDateFormat()
                    ).optional(),
                    fieldWithPath("content.[].created_at").type(STRING).description("가입일자")
                        .attributes(DocumentAttributeGenerator.getZonedDateFormat()),
                    fieldWithPath("content.[].modified_at").type(STRING).description("수정일자")
                        .attributes(DocumentAttributeGenerator.getZonedDateFormat())
                )
            )
        )
    }

    class WithdrawRequest(val reason: String)

    @Test
    fun withdrawMember() {
        // given
        val member: Member = memberRepository.save(makeMember(status = MemberStatus.ACTIVE))
        val request = WithdrawRequest("사용하지 않음")

        BDDMockito.given<Member>(legacyMemberService!!.currentMember()).willReturn(member)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                patch("/internal/1/member/withdrawal")
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_withdraw_member",
                requestFields(
                    fieldWithPath("reason").type(STRING).description("탈퇴 사유"),
                )
            )
        )
    }
}
