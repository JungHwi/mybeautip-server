package com.jocoos.mybeautip.domain.community.api.internal

import com.jocoos.mybeautip.*
import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.DRIP
import com.jocoos.mybeautip.domain.community.dto.EditCommunityCommentRequest
import com.jocoos.mybeautip.domain.community.dto.ReportRequest
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityCommentRequest
import com.jocoos.mybeautip.domain.community.persistence.domain.Community
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCategoryRepository
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCommentRepository
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityRepository
import com.jocoos.mybeautip.domain.file.code.FileType.IMAGE
import com.jocoos.mybeautip.domain.notification.persistence.repository.NotificationMessageCenterRepository
import com.jocoos.mybeautip.domain.notification.persistence.repository.NotificationMessagePushRepository
import com.jocoos.mybeautip.global.code.FileOperationType.DELETE
import com.jocoos.mybeautip.global.code.FileOperationType.UPLOAD
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.global.dto.FileDto
import com.jocoos.mybeautip.global.dto.single.BooleanDto
import com.jocoos.mybeautip.member.LegacyMemberService
import com.jocoos.mybeautip.member.Member
import com.jocoos.mybeautip.member.MemberRepository
import com.jocoos.mybeautip.testutil.fixture.*
import org.junit.jupiter.api.*
import org.mockito.BDDMockito
import org.springframework.boot.test.mock.mockito.MockBean
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InternalCommunityCommentControllerTest(
    private val memberRepository: MemberRepository,
    private val communityRepository: CommunityRepository,
    private val communityCommentRepository: CommunityCommentRepository,
    private val communityCategoryRepository: CommunityCategoryRepository,
    private val notificationMessageCenterRepository: NotificationMessageCenterRepository,
    private val notificationMessagePushRepository: NotificationMessagePushRepository
) : RestDocsIntegrationTestSupport() {

    private lateinit var community: Community
    private lateinit var category: CommunityCategory
    private lateinit var writer: Member

    companion object {
        const val MEMBER_ID = "MEMBER-ID"
    }

    @MockBean
    private val legacyMemberService: LegacyMemberService? = null

    @BeforeAll
    fun beforeAll() {
        category = saveCategory()
        community = saveCommunity(communityCategory = category)
        writer = saveUser()
    }

    @AfterAll
    fun afterAll() {
        communityCategoryRepository.delete(category)
        communityRepository.delete(community)
        memberRepository.delete(writer)
    }

    @BeforeEach
    fun setUp() {
        BDDMockito.given(legacyMemberService!!.currentMember()).willReturn(writer)
        BDDMockito.given(legacyMemberService.currentMemberId()).willReturn(writer.id)
        BDDMockito.given(legacyMemberService.hasCommentPostPermission(writer)).willReturn(true)
    }

    @Test
    fun getComments() {

        // given
        val communityComment: CommunityComment = saveCommunityComment()

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/internal/1/community/{community_id}/comment", communityComment.communityId)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, writer.id)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_get_community_comments",
                pathParameters(
                    parameterWithName("community_id").description("글 ID")
                ),
                requestParameters(
                    parameterWithName("parent_id").description("부모 댓글 아이디").optional(),
                    parameterWithName("cursor").description("커서").optional(),
                    parameterWithName("size").description("페이지 사이즈").optional().attributes(getDefault(20)),
                    parameterWithName("direction").description("정렬 방향").optional().attributes(getDefault("DESC"))
                ),
                responseFields(
                    fieldWithPath("next_cursor").type(STRING).description("커서 정보").optional(),
                    fieldWithPath("content").type(ARRAY).description("커뮤니티 댓글 목록").optional(),
                    fieldWithPath("content.[].id").type(NUMBER).description("댓글 아이디"),
                    fieldWithPath("content.[].category_id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("content.[].community_id").type(NUMBER).description("커뮤니티 아이디"),
                    fieldWithPath("content.[].parent_id").type(NUMBER).description("부모 댓글 아이디").optional(),
                    fieldWithPath("content.[].status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("content.[].contents").type(STRING).description("내용").optional(),
                    fieldWithPath("content.[].file_url").type(STRING).description("이미지 URL").optional(),
                    fieldWithPath("content.[].like_count").type(NUMBER).description("좋아요수"),
                    fieldWithPath("content.[].comment_count").type(NUMBER).description("대댓글수"),
                    fieldWithPath("content.[].report_count").type(NUMBER).description("대댓글수"),
                    fieldWithPath("content.[].created_at").type(STRING).description("작성일")
                        .attributes(getZonedDateFormat()),
                    fieldWithPath("content.[].relation_info").type(OBJECT).description("유저와의 관계 정보"),
                    fieldWithPath("content.[].relation_info.is_like").type(BOOLEAN).description("글 좋아요 여부"),
                    fieldWithPath("content.[].relation_info.is_block").type(BOOLEAN).description("작성자 차단 여부"),
                    fieldWithPath("content.[].relation_info.is_report").type(BOOLEAN).description("글 신고 여부"),
                    fieldWithPath("content.[].member").type(OBJECT).description("작성자 정보"),
                    fieldWithPath("content.[].member.id").type(NUMBER).description("작성자 아이디").optional(),
                    fieldWithPath("content.[].member.status").type(STRING).description(generateLinkCode(MEMBER_STATUS)),
                    fieldWithPath("content.[].member.username").type(STRING).description("작성자 이름").optional(),
                    fieldWithPath("content.[].member.avatar_url").type(STRING).description("작성자 아바타 URL").optional()
                )
            )
        )
    }

    @Test
    fun getComment() {

        // given
        val communityComment: CommunityComment = saveCommunityComment()

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get(
                    "/internal/1/community/{community_id}/comment/{comment_id}",
                    communityComment.communityId,
                    communityComment.id
                )
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, writer.id)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_get_community_comment",
                pathParameters(
                    parameterWithName("community_id").description("글 ID"),
                    parameterWithName("comment_id").description("댓글 ID")
                ),
                requestParameters(
                    parameterWithName("parent_id").description("부모 댓글 아이디").optional()
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("댓글 아이디"),
                    fieldWithPath("category_id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("community_id").type(NUMBER).description("커뮤니티 아이디"),
                    fieldWithPath("parent_id").type(NUMBER).description("부모 댓글 아이디").optional(),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("contents").type(STRING).description("내용").optional(),
                    fieldWithPath("file_url").type(STRING).description("이미지 URL").optional(),
                    fieldWithPath("is_like").type(BOOLEAN).description("좋아요 여부").optional(),
                    fieldWithPath("like_count").type(NUMBER).description("좋아요수"),
                    fieldWithPath("comment_count").type(NUMBER).description("대댓글수"),
                    fieldWithPath("report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("created_at").type(STRING).description("작성일").attributes(getZonedDateFormat()),
                    fieldWithPath("relation_info").type(OBJECT).description("유저와의 관계 정보"),
                    fieldWithPath("relation_info.is_like").type(BOOLEAN).description("글 좋아요 여부"),
                    fieldWithPath("relation_info.is_block").type(BOOLEAN).description("작성자 차단 여부"),
                    fieldWithPath("relation_info.is_report").type(BOOLEAN).description("글 신고 여부"),
                    fieldWithPath("member").type(OBJECT).description("작성자 정보"),
                    fieldWithPath("member.id").type(NUMBER).description("작성자 아이디").optional(),
                    fieldWithPath("member.status").type(STRING).description(generateLinkCode(MEMBER_STATUS)),
                    fieldWithPath("member.username").type(STRING).description("작성자 이름").optional(),
                    fieldWithPath("member.avatar_url").type(STRING).description("작성자 아바타 URL").optional()
                )
            )
        )
    }

    @Test
    fun writeComment() {

        // given
        val community: Community = saveCommunity(saveCategory())
        notificationMessageCenterRepository.save(makeNotificationMessageCenterEntity())
        notificationMessagePushRepository.save(makeNotificationMessagePushEntity())

        val request = WriteCommunityCommentRequest.builder()
            .contents("Mock Comment Contents")
            .file(FileDto(UPLOAD, "imageUrl"))
            .build()

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                post("/internal/1/community/{community_id}/comment", community.id)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, writer.id)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_write_community_comment",
                pathParameters(
                    parameterWithName("community_id").description("글 ID")
                ),
                requestFields(
                    fieldWithPath("parent_id").type(NUMBER).description("부모 댓글 아이디")
                        .optional(),
                    fieldWithPath("contents").type(STRING).description("내용 (contents 나 file 둘 중에 하나는 있어야 합니다)").optional(),
                    fieldWithPath("file").type(OBJECT).description("이미지").optional(),
                    fieldWithPath("file.type").type(ARRAY).attributes(getDefault(IMAGE)).description(FILE_TYPE)
                        .ignored(),
                    fieldWithPath("file.operation").type(STRING).description(generateLinkCode(FILE_OPERATION_TYPE)),
                    fieldWithPath("file.url").type(STRING).description("이미지 URL"),
                    fieldWithPath("file.need_transcode").type(BOOLEAN).ignored()
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("댓글 아이디"),
                    fieldWithPath("category_id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("community_id").type(NUMBER).description("커뮤니티 아이디"),
                    fieldWithPath("parent_id").type(NUMBER).description("부모 댓글 아이디").optional(),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("contents").type(STRING).description("내용").optional(),
                    fieldWithPath("file_url").type(STRING).description("이미지 URL").optional(),
                    fieldWithPath("is_like").type(BOOLEAN).description("좋아요 여부").optional(),
                    fieldWithPath("like_count").type(NUMBER).description("좋아요수"),
                    fieldWithPath("comment_count").type(NUMBER).description("대댓글수"),
                    fieldWithPath("report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("created_at").type(STRING).description("작성일").attributes(getZonedDateFormat()),
                    fieldWithPath("relation_info").type(OBJECT).description("유저와의 관계 정보"),
                    fieldWithPath("relation_info.is_like").type(BOOLEAN).description("글 좋아요 여부"),
                    fieldWithPath("relation_info.is_block").type(BOOLEAN).description("작성자 차단 여부"),
                    fieldWithPath("relation_info.is_report").type(BOOLEAN).description("글 신고 여부"),
                    fieldWithPath("member").type(OBJECT).description("작성자 정보"),
                    fieldWithPath("member.id").type(NUMBER).description("작성자 아이디").optional(),
                    fieldWithPath("member.status").type(STRING).description(generateLinkCode(MEMBER_STATUS)),
                    fieldWithPath("member.username").type(STRING).description("작성자 이름").optional(),
                    fieldWithPath("member.avatar_url").type(STRING).description("작성자 아바타 URL").optional()
                )
            )
        )
    }

    @Test
    fun editComment() {

        // given
        val communityComment: CommunityComment = saveCommunityComment(member = writer)

        val request = EditCommunityCommentRequest.builder()
            .contents("content")
            .files(
                listOf(
                    FileDto(DELETE, "delete image url"),
                    FileDto(UPLOAD, "upload image url")
                )
            )
            .build()

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                put(
                    "/internal/1/community/{community_id}/comment/{comment_id}",
                    communityComment.communityId,
                    communityComment.id
                )
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, writer.id)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_edit_community_comment",
                pathParameters(
                    parameterWithName("community_id").description("글 ID"),
                    parameterWithName("comment_id").description("댓글 ID")
                ),
                requestFields(
                    fieldWithPath("contents").type(STRING).description("내용 (contents 나 file 둘 중에 하나는 존재하도록 해야합니다)").optional(),
                    fieldWithPath("files").type(ARRAY).description("이미지 파일 List").optional(),
                    fieldWithPath("files.[].type").type(ARRAY).attributes(getDefault(IMAGE)).description(FILE_TYPE)
                        .ignored(),
                    fieldWithPath("files.[].operation").type(STRING).description(generateLinkCode(FILE_OPERATION_TYPE)),
                    fieldWithPath("files.[].url").type(STRING).description("이미지 URL"),
                    fieldWithPath("files.[].need_transcode").type(BOOLEAN).ignored()
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("댓글 ID"),
                    fieldWithPath("category_id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("community_id").type(NUMBER).description("커뮤니티 아이디"),
                    fieldWithPath("parent_id").type(NUMBER).description("부모 댓글 아이디").optional(),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("contents").type(STRING).description("내용").optional(),
                    fieldWithPath("file_url").type(STRING).description("이미지 URL").optional(),
                    fieldWithPath("is_like").type(BOOLEAN).description("좋아요 여부").optional(),
                    fieldWithPath("like_count").type(NUMBER).description("좋아요수"),
                    fieldWithPath("comment_count").type(NUMBER).description("대댓글수"),
                    fieldWithPath("report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("created_at").type(STRING).description("작성일").attributes(getZonedDateFormat()),
                    fieldWithPath("relation_info").type(OBJECT).description("유저와의 관계 정보"),
                    fieldWithPath("relation_info.is_like").type(BOOLEAN).description("글 좋아요 여부"),
                    fieldWithPath("relation_info.is_block").type(BOOLEAN).description("작성자 차단 여부"),
                    fieldWithPath("relation_info.is_report").type(BOOLEAN).description("글 신고 여부"),
                    fieldWithPath("member").type(OBJECT).description("작성자 정보"),
                    fieldWithPath("member.id").type(NUMBER).description("작성자 아이디").optional(),
                    fieldWithPath("member.status").type(STRING).description(generateLinkCode(MEMBER_STATUS)),
                    fieldWithPath("member.username").type(STRING).description("작성자 이름").optional(),
                    fieldWithPath("member.avatar_url").type(STRING).description("작성자 아바타 URL").optional()
                )
            )
        )
    }

    @Test
    fun deleteComment() {

        // given
        val communityComment: CommunityComment = saveCommunityComment(member = writer)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                delete(
                    "/internal/1/community/{community_id}/comment/{comment_id}",
                    communityComment.communityId,
                    communityComment.id
                )
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, writer.id)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_delete_community_comment",
                pathParameters(
                    parameterWithName("community_id").description("글 ID"),
                    parameterWithName("comment_id").description("댓글 ID")
                )
            )
        )
    }

    @Test
    fun likeComment() {

        // given
        val communityComment: CommunityComment = saveCommunityComment()
        val bool = BooleanDto(true)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                patch(
                    "/internal/1/community/{community_id}/comment/{comment_id}/like",
                    communityComment.communityId,
                    communityComment.id
                )
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, writer.id)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bool))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_like_community_comment",
                pathParameters(
                    parameterWithName("community_id").description("글 ID"),
                    parameterWithName("comment_id").description("댓글 ID")
                ),
                requestFields(
                    fieldWithPath("bool").type(BOOLEAN).description("좋아요 여부")
                ),
                responseFields(
                    fieldWithPath("is_like").type(BOOLEAN).description("좋아요 여부"),
                    fieldWithPath("like_count").type(NUMBER).description("좋아요 수")
                )
            )
        )
    }

    @Test
    fun reportComment() {

        // given
        val communityComment: CommunityComment = saveCommunityComment(requestUser)
        val report = ReportRequest.builder()
            .isReport(true)
            .description("신고사유")
            .build()

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                patch(
                    "/internal/1/community/{community_id}/comment/{comment_id}/report",
                    communityComment.communityId,
                    communityComment.id
                )
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, writer.id)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(report))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_report_community_comment",
                pathParameters(
                    parameterWithName("community_id").description("글 ID"),
                    parameterWithName("comment_id").description("댓글 ID")
                ),
                requestFields(
                    fieldWithPath("is_report").type(BOOLEAN).description("신고 여부"),
                    fieldWithPath("description").type(STRING).description("신고 사유. 신고여부가 true 일때만 필수").optional()
                ),
                responseFields(
                    fieldWithPath("is_report").type(BOOLEAN).description("신고 여부"),
                    fieldWithPath("report_count").type(NUMBER).description("신고수")
                )
            )
        )
    }

    fun saveCommunityComment(member: Member = writer): CommunityComment {
        return communityCommentRepository.save(makeCommunityComment(member = member, community = community))
    }

    fun saveCommunity(communityCategory: CommunityCategory) =
        communityRepository.save(makeCommunity(category = communityCategory))

    fun saveUser(id: Long? = null) = memberRepository.save(makeMember(id = id, link = 2))
    fun saveCategory() = communityCategoryRepository.save(makeCommunityCategory(type = DRIP))
}
