package com.jocoos.mybeautip.domain.community.api.admin

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.DRIP
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityCommentRequest
import com.jocoos.mybeautip.domain.community.persistence.domain.Community
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCategoryRepository
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCommentRepository
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.global.dto.single.BooleanDto
import com.jocoos.mybeautip.testutil.fixture.makeCommunity
import com.jocoos.mybeautip.testutil.fixture.makeCommunityCategory
import com.jocoos.mybeautip.testutil.fixture.makeCommunityComment
import com.jocoos.mybeautip.testutil.fixture.makeMember
import com.jocoos.mybeautip.member.Member
import com.jocoos.mybeautip.member.MemberRepository
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AdminCommunityCommentControllerTest(
    private val memberRepository: MemberRepository,
    private val communityRepository: CommunityRepository,
    private val communityCategoryRepository: CommunityCategoryRepository,
    private val communityCommentRepository: CommunityCommentRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun writeComment() {

        // given
        val communityCategory: CommunityCategory = communityCategoryRepository.save(makeCommunityCategory(type = DRIP))
        val community: Community = communityRepository.save(makeCommunity(category = communityCategory));

        val request = WriteCommunityCommentRequest.builder()
            .contents("Mock Comment Contents")
            .build()

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                post("/admin/community/{community_id}/comment", community.id)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isCreated)
            .andDo(print())

        result.andDo(
            document(
                "admin_write_community_comment",
                pathParameters(
                    parameterWithName("community_id").description("글 ID")
                ),
                requestFields(
                    fieldWithPath("parent_id").type(NUMBER).description("부모 댓글 아이디").optional(),
                    fieldWithPath("contents").type(STRING).description("내용")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("댓글 아이디"),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("contents").type(STRING).description("내용"),
                    fieldWithPath("file_url").type(STRING).description("이미지 URL").optional(),
                    fieldWithPath("like_count").type(NUMBER).description("좋아요수"),
                    fieldWithPath("report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("created_at").type(STRING).description("작성일").attributes(getZonedDateFormat()),
                    fieldWithPath("member").type(OBJECT).description("작성자 정보"),
                    fieldWithPath("member.id").type(NUMBER).description("작성자 아이디").optional(),
                    fieldWithPath("member.status").type(STRING).description(generateLinkCode(MEMBER_STATUS)),
                    fieldWithPath("member.username").type(STRING).description("작성자 이름").optional(),
                    fieldWithPath("member.avatar_url").type(STRING).description("작성자 아바타 URL").optional(),
                    fieldWithPath("member.role").type(STRING).description(generateLinkCode(ROLE)).optional()
                )
            )
        )
    }

    @Test
    fun editComment() {

        // given
        val communityComment: CommunityComment = saveCommunityComment()
        val request = mapOf("contents" to "Test Contents")

        // when & then
        val result = mockMvc
            .perform(
                patch("/admin/community/{community_id}/comment/{comment_id}", communityComment.communityId, communityComment.id)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_edit_community_comment",
                pathParameters(
                    parameterWithName("community_id").description("글 ID"),
                    parameterWithName("comment_id").description("댓글 ID")
                ),
                requestFields(
                    fieldWithPath("contents").type(STRING).description("내용")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("댓글 ID")
                )
            )
        )
    }

    @Test
    fun getCommunityComments() {

        // given
        val communityComment: CommunityComment = saveCommunityComment()


        // when & then
        val result = mockMvc
            .perform(
                get("/admin/community/{community_id}/comment", communityComment.communityId)
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_community_comments",
                pathParameters(
                    parameterWithName("community_id").description("글 ID")
                ),
                requestParameters(
                    parameterWithName("page").description("페이지 넘버").optional().attributes(getDefault(1)),
                    parameterWithName("size").description("페이지 사이즈").optional().attributes(getDefault(10))
                ),
                responseFields(
                    fieldWithPath("total").type(NUMBER).description("총 커뮤니티 댓글 개수"),
                    fieldWithPath("content").type(ARRAY).description("커뮤니티 댓글 목록"),
                    fieldWithPath("content.[].id").type(NUMBER).description("커뮤니티 댓글 ID"),
                    fieldWithPath("content.[].status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("content.[].contents").type(STRING).description("내용"),
                    fieldWithPath("content.[].file_url").type(STRING).description("이미지 URL").optional(),
                    fieldWithPath("content.[].like_count").type(NUMBER).description("좋아요수"),
                    fieldWithPath("content.[].report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("content.[].member").type(OBJECT).description("작성자 정보.").optional(),
                    fieldWithPath("content.[].member.id").type(NUMBER).description("작성자 아이디").optional(),
                    fieldWithPath("content.[].member.status").type(STRING).description(generateLinkCode(MEMBER_STATUS))
                        .optional(),
                    fieldWithPath("content.[].member.username").type(STRING).description("작성자 이름").optional(),
                    fieldWithPath("content.[].member.avatar_url").type(STRING).description("작성자 아바타 URL").optional(),
                    fieldWithPath("content.[].member.role").type(STRING).description(generateLinkCode(ROLE)).optional(),
                    fieldWithPath("content.[].created_at").type(STRING).description("작성일")
                        .attributes(getZonedDateFormat()),
                    fieldWithPath("content.[].children").type(ARRAY)
                        .description("대댓글 목록, children 필드가 없는 것을 제외하고 본 응답과 동일").optional(),


                    fieldWithPath("content.[].children.[].id").type(NUMBER).description("커뮤니티 댓글 ID").optional()
                        .ignored(),
                    fieldWithPath("content.[].children.[].status").type(STRING)
                        .description(generateLinkCode(COMMUNITY_STATUS)).optional().ignored(),
                    fieldWithPath("content.[].children.[].contents").type(STRING).description("내용").optional()
                        .ignored(),
                    fieldWithPath("content.[].children.[].file_url").type(STRING).description("이미지 URL").optional()
                        .ignored(),
                    fieldWithPath("content.[].children.[].like_count").type(NUMBER).description("좋아요수").optional()
                        .ignored(),
                    fieldWithPath("content.[].children.[].report_count").type(NUMBER).description("신고수").optional()
                        .ignored(),
                    fieldWithPath("content.[].children.[].member").type(OBJECT).description("작성자 정보.").optional()
                        .ignored(),
                    fieldWithPath("content.[].children.[].member.id").type(NUMBER).description("작성자 아이디").optional()
                        .ignored(),
                    fieldWithPath("content.[].children.[].member.status").type(STRING)
                        .description(generateLinkCode(MEMBER_STATUS)).optional().ignored(),
                    fieldWithPath("content.[].children.[].member.username").type(STRING).description("작성자 이름")
                        .optional().ignored(),
                    fieldWithPath("content.[].children.[].member.avatar_url").type(STRING).description("작성자 아바타 URL")
                        .optional().ignored(),
                    fieldWithPath("content.[].children.[].member.role").type(STRING).description(generateLinkCode(ROLE))
                        .optional().ignored(),
                    fieldWithPath("content.[].children.[].created_at").type(STRING).description("작성일")
                        .attributes(getZonedDateFormat()).optional().ignored()
                )
            )
        )
    }


    @Test
    fun hideCommunityComment() {

        // given
        val communityComment: CommunityComment = saveCommunityComment()
        val request = BooleanDto(true)

        // when & then
        val result = mockMvc
            .perform(
                patch("/admin/community/comment/{comment_id}/hide", communityComment.id)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_hide_community_comment",
                pathParameters(
                    parameterWithName("comment_id").description("커뮤니티 댓글 ID")
                ),
                requestFields(
                    fieldWithPath("bool").type(BOOLEAN).description("숨김 처리 여부")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("커뮤니티 댓글 ID")
                )
            )
        )
    }

    fun saveCommunityComment() : CommunityComment {
        val admin: Member = memberRepository.save(makeMember(link = 0))
        val communityCategory: CommunityCategory = communityCategoryRepository.save(makeCommunityCategory(type = DRIP))
        val community: Community = communityRepository.save(makeCommunity(category = communityCategory));
        return communityCommentRepository.save(makeCommunityComment(member = admin, community = community))
    }
}
