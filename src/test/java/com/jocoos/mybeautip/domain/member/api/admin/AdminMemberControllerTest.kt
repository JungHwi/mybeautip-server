package com.jocoos.mybeautip.domain.member.api.admin

import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityReportRepository
import com.jocoos.mybeautip.domain.member.code.InfluencerStatus
import com.jocoos.mybeautip.domain.member.code.MemberStatus.*
import com.jocoos.mybeautip.domain.member.dto.InfluencerRequest
import com.jocoos.mybeautip.domain.member.dto.MemberStatusRequest
import com.jocoos.mybeautip.domain.member.persistence.repository.InfluencerRepository
import com.jocoos.mybeautip.domain.member.persistence.repository.MemberActivityCountRepository
import com.jocoos.mybeautip.domain.term.code.TermType.MARKETING_INFO
import com.jocoos.mybeautip.domain.term.persistence.repository.TermRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.member.Member
import com.jocoos.mybeautip.member.MemberRepository
import com.jocoos.mybeautip.member.point.MemberPointRepository
import com.jocoos.mybeautip.testutil.fixture.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AdminMemberControllerTest(
    private val memberRepository: MemberRepository,
    private val memberPointRepository: MemberPointRepository,
    private val memberActivityCountRepository: MemberActivityCountRepository,
    private val communityReportRepository: CommunityReportRepository,
    private val termRepository: TermRepository,
    private val influencerRepository: InfluencerRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun getMemberStatuses() {

        // given
        memberRepository.saveAll(makeMembers(3, DORMANT))
        memberRepository.saveAll(makeMembers(3, ACTIVE))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/admin/member/status")
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_member_status",
                responseFields(
                    fieldWithPath("[].status").type(STRING).description(generateLinkCode(MEMBER_STATUS)).optional(),
                    fieldWithPath("[].status_name").type(STRING).description("회원 상태 이름"),
                    fieldWithPath("[].count").type(NUMBER).description("회원수")
                )
            )
        )
    }

    @Test
    fun getMemberBasicDetail() {

        // given
        val member: Member = memberRepository.save(makeMember())
        memberActivityCountRepository.save(makeActivityCount(member))
        termRepository.save(makeTerm(type = MARKETING_INFO))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/admin/member/{member_id}", member.id)
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_member_basic_detail",
                pathParameters(
                    parameterWithName("member_id").description("회원 ID")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("회원 ID"),
                    fieldWithPath("role").type(STRING).description(generateLinkCode(ROLE)),
                    fieldWithPath("avatar_url").type(STRING).description("아바타 이미지 URL"),
                    fieldWithPath("username").type(STRING).description("닉네임"),
                    fieldWithPath("name").type(STRING).description("이름").optional(),
                    fieldWithPath("email").type(STRING).description("이메일").optional(),
                    fieldWithPath("phone_number").type(STRING).description("전화번호").optional(),
                    fieldWithPath("point").type(NUMBER).description("보유 포인트"),
                    fieldWithPath("expiry_point").type(NUMBER).description("이번달 만료예정 포인트"),
                    fieldWithPath("is_pushable").type(BOOLEAN).description("푸시 알림 동의 여부"),
                    fieldWithPath("is_agree_marketing_term").type(BOOLEAN).description("마케팅 동의 여부"),
                    fieldWithPath("grant_type").type(STRING).description("가입 경로").optional(),
                    fieldWithPath("created_at").type(STRING).description("가입일자"),
                    fieldWithPath("modified_at").type(STRING).description("수정일자"),
                    fieldWithPath("normal_community_count").type(NUMBER).description("정상 게시물 작성 수"),
                    fieldWithPath("normal_community_comment_count").type(NUMBER).description("정상 게시물 댓글 작성 수"),
                    fieldWithPath("normal_video_comment_count").type(NUMBER).description("정상 비디오 댓글 작성 수"),
                    fieldWithPath("total_community_count").type(NUMBER).description("전체 게시물 작성 수"),
                    fieldWithPath("total_community_comment_count").type(NUMBER).description("전체 커뮤니티 댓글 작성 수"),
                    fieldWithPath("total_video_comment_count").type(NUMBER).description("전체 비디오 댓글 작성 수"),
                    fieldWithPath("invited_friend_count").type(NUMBER).description("초대한 친구들 수"),
                    fieldWithPath("age_group").type(NUMBER).description("연령대, 10 단위 ex) 10, 20, 30").optional(),
                    fieldWithPath("skin_type").type(STRING).description(generateLinkCode(SKIN_TYPE)).optional(),
                    fieldWithPath("skin_worry").type(ARRAY).description(generateLinkCode(SKIN_WORRY)).optional(),
                    fieldWithPath("address").type(STRING).description("주소").optional(),
                    fieldWithPath("memo").type(ARRAY).description("괸리자 작성 메모 목록").optional(),
                    fieldWithPath("memo.[].id").type(NUMBER).description("괸리자 작성 메모 ID"),
                    fieldWithPath("memo.[].content").type(STRING).description("괸리자 작성 메모 내용"),
                    fieldWithPath("memo.[].member").type(OBJECT).description("괸리자 작성 메모 작성자"),
                    fieldWithPath("memo.[].member.id").type(OBJECT).description("괸리자 작성 메모 작성자 ID"),
                    fieldWithPath("memo.[].member.username").type(OBJECT).description("괸리자 작성 메모 작성자 닉네임"),
                    fieldWithPath("memo.[].created_at").type(STRING).description("생성일자")
                        .attributes(getZonedDateFormat())
                )
            )
        )
    }

    @Test
    fun getMembers() {

        // given
        val member = memberRepository.save(makeMember())
        termRepository.save(makeTerm(type = MARKETING_INFO))
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
                "admin_get_members",
                requestParameters(
                    parameterWithName("page").attributes(getDefault(1)).description("페이지 넘버").optional(),
                    parameterWithName("size").attributes(getDefault(10)).description("페이지 내 컨텐츠 개수").optional(),
                    parameterWithName("status").description("멤버 상태").optional(),
                    parameterWithName("is_influencer").description("인플루언서 여부 > " + generateLinkCode(BOOLEAN_TYPE)).optional(),
                    parameterWithName("grant_type").description("가입").optional(),
                    parameterWithName("search").description("검색 (검색필드,검색어) 형식").optional(),
                    parameterWithName("start_at").description("검색 시작 일자").optional(),
                    parameterWithName("end_at").description("검색 종료 일자").optional()
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
                    fieldWithPath("content.[].influencer_info.earned_at").type(STRING).description("인플루언서 권한 획득 일시").attributes(getZonedDateFormat()).optional(),
                    fieldWithPath("content.[].created_at").type(STRING).description("가입일자")
                        .attributes(getZonedDateFormat()),
                    fieldWithPath("content.[].modified_at").type(STRING).description("수정일자")
                        .attributes(getZonedDateFormat())
                )
            )
        )
    }

    @Test
    fun getMemberPointHistory() {

        // given
        val member: Member = memberRepository.save(makeMember())
        memberPointRepository.save(makeMemberPoint(member = member))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/admin/member/{member_id}/point", member.id)
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_member_point_history",
                pathParameters(
                    parameterWithName("member_id").description("회원 ID")
                ),
                requestParameters(
                    parameterWithName("page").attributes(getDefault(1)).description("페이지 넘버").optional(),
                    parameterWithName("size").attributes(getDefault(10)).description("페이지 내 컨텐츠 개수").optional()
                ),
                responseFields(
                    fieldWithPath("total").type(NUMBER).description("총 포인트 내역 수"),
                    fieldWithPath("content").type(ARRAY).description("포인트 내역 목록"),
                    fieldWithPath("content.[].id").type(NUMBER).description("포인트 ID"),
                    fieldWithPath("content.[].status").type(STRING).description(generateLinkCode(DocUrl.POINT_STATUS)),
                    fieldWithPath("content.[].reason").type(STRING).description("이유"),
                    fieldWithPath("content.[].point").type(NUMBER).description("포인트"),
                    fieldWithPath("content.[].earned_at").type(STRING).description("생성일자")
                        .attributes(getZonedDateFormat()),
                    fieldWithPath("content.[].expiry_at").type(STRING).description("만료일자")
                        .attributes(getZonedDateFormat()).optional()
                )
            )
        )
    }

    @Test
    fun getMemberReportHistory() {

        // given
        val member: Member = memberRepository.save(makeMember())
        communityReportRepository.save(makeCommunityReport(requestUser.id, member.id, 1))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/admin/member/{member_id}/report", requestUser.id)
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_member_report_history",
                pathParameters(
                    parameterWithName("member_id").description("회원 ID")
                ),
                responseFields(
                    fieldWithPath("total").type(NUMBER).description("총 신고 내역 수"),
                    fieldWithPath("content").type(ARRAY).description("신고 내역 목록").optional(),
                    fieldWithPath("content.[].id").type(STRING).description("신고 ID, type + id 형태"),
                    fieldWithPath("content.[].accuser").type(OBJECT).description("신고자"),
                    fieldWithPath("content.[].accuser.id").type(NUMBER).description("신고자 ID"),
                    fieldWithPath("content.[].accuser.username").type(STRING).description("신고자 닉네임"),
                    fieldWithPath("content.[].reason").type(STRING).description("신고 사유"),
                    fieldWithPath("content.[].reported_at").type(STRING).description("신고일자")
                        .attributes(getZonedDateFormat())
                )
            )
        )
    }

    @Test
    fun patchMemberStatus() {

        // given
        val member: Member = memberRepository.save(makeMember(status = ACTIVE))
        val request = MemberStatusRequest(SUSPENDED)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                patch("/admin/member/{member_id}/status", member.id)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_patch_member_status",
                pathParameters(
                    parameterWithName("member_id").description("회원 ID")
                ),
                requestFields(
                    fieldWithPath("after_status").type(STRING).description(generateLinkCode(MEMBER_STATUS)),
                    fieldWithPath("description").ignored(),
                    fieldWithPath("operation_type").ignored(),
                    fieldWithPath("target_id").ignored()
                )
            )
        )
    }

    @Test
    fun updateInfluencer() {
        val member: Member = memberRepository.save(makeMember(status = ACTIVE))
        val request = InfluencerRequest(InfluencerStatus.ACTIVE)

        val result: ResultActions = mockMvc
            .perform(
                patch("/admin/member/{member_id}/influencer", member.id)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_patch_influencer",
                pathParameters(
                    parameterWithName("member_id").description("회원 ID")
                ),
                requestFields(
                    fieldWithPath("status").type(STRING).description(generateLinkCode(INFLUENCER_STATUS))
                ),
                responseFields(
                    fieldWithPath("status").type(STRING).description(generateLinkCode(INFLUENCER_STATUS)),
                    fieldWithPath("broadcast_count").type(NUMBER).description("방송 횟수"),
                    fieldWithPath("earned_at").type(STRING).description("인플루언서 권한 획득 일시").attributes(getZonedDateFormat()).optional()
                )
            )
        )
    }
}