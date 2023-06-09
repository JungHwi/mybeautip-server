package com.jocoos.mybeautip.domain.community.api.internal

import com.jocoos.mybeautip.*
import com.jocoos.mybeautip.domain.community.dto.ReportRequest
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityRequest
import com.jocoos.mybeautip.domain.community.persistence.domain.Community
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory
import com.jocoos.mybeautip.domain.community.persistence.domain.vote.CommunityVote
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCategoryRepository
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityRepository
import com.jocoos.mybeautip.domain.notification.code.TemplateType.COMMUNITY_LIKE_1
import com.jocoos.mybeautip.domain.notification.persistence.repository.NotificationMessageCenterRepository
import com.jocoos.mybeautip.domain.notification.persistence.repository.NotificationMessagePushRepository
import com.jocoos.mybeautip.domain.video.api.internal.InternalVideoControllerTest
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.global.dto.single.BooleanDto
import com.jocoos.mybeautip.member.LegacyMemberService
import com.jocoos.mybeautip.member.Member
import com.jocoos.mybeautip.member.MemberRepository
import com.jocoos.mybeautip.testutil.fixture.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.mockito.BDDMockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.multipart.MultipartFile

@TestInstance(PER_CLASS)
class InternalCommunityControllerTest(
    private val memberRepository: MemberRepository,
    private val communityRepository: CommunityRepository,
    private val communityCategoryRepository: CommunityCategoryRepository,
    private val notificationMessageCenterRepository: NotificationMessageCenterRepository,
    private val notificationMessagePushRepository: NotificationMessagePushRepository,
    @LocalServerPort private val port: Int
) : RestDocsIntegrationTestSupport() {

    private lateinit var writer: Member
    private lateinit var communityCategory: CommunityCategory

    companion object {
        const val MEMBER_ID = "MEMBER-ID"
    }

    @MockBean
    private val legacyMemberService: LegacyMemberService? = null

    @BeforeAll
    fun beforeAll() {
        writer = memberRepository.save(makeMember(link = 2))
        communityCategory = communityCategoryRepository.save(makeCommunityCategory())
    }

    @AfterAll
    fun afterAll() {
        memberRepository.delete(writer)
        communityCategoryRepository.delete(communityCategory)
    }

    @BeforeEach
    fun setUp() {
        BDDMockito.given(legacyMemberService!!.currentMember()).willReturn(writer)
        BDDMockito.given(legacyMemberService.currentMemberId()).willReturn(writer.id)
        BDDMockito.given(legacyMemberService.hasCommentPostPermission(writer)).willReturn(true)
    }

    @Test
    fun writeCommunity() {
        val request = WriteCommunityRequest.builder()
            .categoryId(communityCategory.id)
            .title("Mock Title")
            .contents("Mock Contents")
            .build()

        val result: ResultActions = mockMvc
            .perform(
                post("/internal/2/community")
                    .header(AUTHORIZATION, requestInternalToken)
                    .contentType(APPLICATION_JSON)
                    .header(MEMBER_ID, writer.id)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_write_community",
                requestFields(
                    fieldWithPath("category_id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("event_id").type(NUMBER).description("이벤트 아이디. 드립N드림 일때 관련된 이벤트 아이디.").optional(),
                    fieldWithPath("title").type(STRING).description("제목. 수근수근에서만 필수").optional(),
                    fieldWithPath("contents").type(STRING).description("내용"),
                    fieldWithPath("files").type(ARRAY).description("파일 작업 정보 목록").optional(),
                    fieldWithPath("files.[].operation").type(STRING).description("파일 상태")
                        .description(generateLinkCode(FILE_OPERATION_TYPE)),
                    fieldWithPath("files.[].type").type(STRING).description("파일 타입")
                        .description(generateLinkCode(FILE_TYPE)),
                    fieldWithPath("files.[].thumbnail_url").type(STRING).description("파일 썸네일 URL").optional(),
                    fieldWithPath("files.[].url").type(STRING).description("파일 URL"),
                    fieldWithPath("files.[].duration").type(NUMBER)
                        .description("파일이 비디오일 때 영상 길이 ms (need_transcode가 false일 때 필요합니다)").optional(),
                    fieldWithPath("files.[].need_transcode")
                        .type(BOOLEAN)
                        .description("비디오 파일 트랜스코딩 필요 여부 (FlipFlop 트랜스코딩이 필요할 때 true)")
                        .attributes(getDefault(false))
                        .optional()
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("글 ID"),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("is_win").type(BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                    fieldWithPath("title").type(STRING).description("제목").optional(),
                    fieldWithPath("contents").type(STRING).description("내용"),
                    fieldWithPath("files").type(ARRAY).description("파일 List").optional(),
                    fieldWithPath("files.[].type").type(STRING).description(generateLinkCode(FILE_TYPE)),
                    fieldWithPath("files.[].thumbnail_url").type(STRING).description("파일 썸네일 URL").optional(),
                    fieldWithPath("files.[].url").type(STRING).description("파일 URL"),
                    fieldWithPath("files.[].duration").type(NUMBER).description("비디오 길이 ms").optional(),
                    fieldWithPath("votes").type(ARRAY).description("투표 파일 List").optional(),
                    fieldWithPath("votes.[].id").type(NUMBER).description("투표 파일 아이디"),
                    fieldWithPath("votes.[].file_url").type(STRING).description("투표 파일 URL"),
                    fieldWithPath("votes.[].count").type(NUMBER).description("투표 수"),
                    fieldWithPath("votes.[].is_voted").type(BOOLEAN).description("투표 파일에 대한 유저 투표 여부"),
                    fieldWithPath("view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("like_count").type(NUMBER).description("좋아요수"),
                    fieldWithPath("comment_count").type(NUMBER).description("댓글수"),
                    fieldWithPath("report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("created_at").type(STRING).description("작성일").attributes(getZonedDateFormat()),
                    fieldWithPath("relation_info").type(OBJECT).description("유저와의 관계 정보"),
                    fieldWithPath("relation_info.is_like").type(BOOLEAN).description("글 좋아요 여부"),
                    fieldWithPath("relation_info.is_block").type(BOOLEAN).description("작성자 차단 여부"),
                    fieldWithPath("relation_info.is_report").type(BOOLEAN).description("글 신고 여부"),
                    fieldWithPath("relation_info.is_scrap").type(BOOLEAN).description("글 스크랩 여부"),
                    fieldWithPath("member").type(OBJECT).description("작성자 정보"),
                    fieldWithPath("member.id").type(NUMBER).description("작성자 아이디").optional(),
                    fieldWithPath("member.status").type(STRING).description(generateLinkCode(MEMBER_STATUS)),
                    fieldWithPath("member.username").type(STRING).description("작성자 이름").optional(),
                    fieldWithPath("member.avatar_url").type(STRING).description("작성자 아바타 URL").optional(),
                    fieldWithPath("category").type(OBJECT).description("카테고리 정보"),
                    fieldWithPath("category.id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("category.type").type(STRING).description("카테고리 구분")
                        .description(generateLinkCode(COMMUNITY_CATEGORY_TYPE)),
                    fieldWithPath("category.title").type(STRING).description("카테고리 제목"),
                    fieldWithPath("category.hint").type(STRING).description("카테고리 힌트")
                )
            )
        )
    }

    @Test
    fun getCommunities() {

        saveCommunity(member = writer, voteList = null)

        val result: ResultActions = mockMvc
            .perform(
                get("/internal/2/community")
                    .param("category_id", communityCategory.id.toString())
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, writer.id)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_get_communities",
                requestParameters(
                    parameterWithName("category_id").description("카테고리 아이디").optional(),
                    parameterWithName("event_id").description("이벤트 아이디. Drip Category 일때만 필수.").optional(),
                    parameterWithName("cursor").description("커서").optional()
                        .attributes(getZonedDateMilliFormat(), getDefault("현재 시간")),
                    parameterWithName("size").description("").optional().attributes(getDefault(20))
                ),
                responseFields(
                    fieldWithPath("next_cursor").type(STRING).description("커서 정보")
                        .attributes(getZonedDateMilliFormat()),
                    fieldWithPath("content").type(ARRAY).description("커뮤니티 글 목록"),
                    fieldWithPath("content.[].id").type(NUMBER).description("글 ID"),
                    fieldWithPath("content.[].is_win").type(BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                    fieldWithPath("content.[].status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("content.[].event_id").type(NUMBER).description("이벤트 ID").optional(),
                    fieldWithPath("content.[].title").type(STRING).description("제목").optional(),
                    fieldWithPath("content.[].contents").type(STRING).description("내용").optional(),
                    fieldWithPath("content.[].files").type(ARRAY).description("파일 List").optional(),
                    fieldWithPath("content.[].files.[].type").type(STRING).description(generateLinkCode(FILE_TYPE)),
                    fieldWithPath("content.[].files.[].thumbnail_url").type(STRING).description("파일 썸네일 URL")
                        .optional(),
                    fieldWithPath("content.[].files.[].url").type(STRING).description("파일 URL"),
                    fieldWithPath("content.[].files.[].duration").type(NUMBER).description("비디오 길이 ms").optional(),
                    fieldWithPath("content.[].votes").type(ARRAY).description("투표 파일 List").optional(),
                    fieldWithPath("content.[].votes.[].id").type(NUMBER).description("투표 파일 아이디"),
                    fieldWithPath("content.[].votes.[].file_url").type(STRING).description("투표 파일 URL"),
                    fieldWithPath("content.[].votes.[].count").type(NUMBER).description("투표 수"),
                    fieldWithPath("content.[].votes.[].is_voted").type(BOOLEAN).description("투표 파일에 대한 유저 투표 여부"),
                    fieldWithPath("content.[].view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("content.[].like_count").type(NUMBER).description("좋아요수"),
                    fieldWithPath("content.[].comment_count").type(NUMBER).description("댓글/대댓글수"),
                    fieldWithPath("content.[].report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("content.[].created_at").type(STRING).description("작성일")
                        .attributes(getZonedDateFormat()),
                    fieldWithPath("content.[].relation_info").type(OBJECT).description("유저와의 관계 정보"),
                    fieldWithPath("content.[].relation_info.is_like").type(BOOLEAN).description("글 좋아요 여부"),
                    fieldWithPath("content.[].relation_info.is_block").type(BOOLEAN).description("작성자 차단 여부"),
                    fieldWithPath("content.[].relation_info.is_report").type(BOOLEAN).description("글 신고 여부"),
                    fieldWithPath("content.[].relation_info.is_scrap").type(BOOLEAN).description("글 스크랩 여부"),
                    fieldWithPath("content.[].member").type(OBJECT).description("작성자 정보."),
                    fieldWithPath("content.[].member.id").type(NUMBER).description("작성자 아이디").optional(),
                    fieldWithPath("content.[].member.status").type(STRING).description(generateLinkCode(MEMBER_STATUS)),
                    fieldWithPath("content.[].member.username").type(STRING).description("작성자 이름").optional(),
                    fieldWithPath("content.[].member.avatar_url").type(STRING).description("작성자 아바타 URL").optional(),
                    fieldWithPath("content.[].category").type(OBJECT).description("카테고리 정보"),
                    fieldWithPath("content.[].category.id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("content.[].category.type").type(STRING).description("카테고리 구분")
                        .description(generateLinkCode(COMMUNITY_CATEGORY_TYPE)),
                    fieldWithPath("content.[].category.title").type(STRING).description("카테고리 제목"),
                    fieldWithPath("content.[].category.hint").type(STRING).description("카테고리 힌트")
                )
            )
        )
    }

    @Test
    fun getCommunity() {

        // given
        val community: Community = saveCommunity(member = writer)

        // when & then
        val result: ResultActions = mockMvc.perform(
            get("/internal/2/community/{community_id}", community.id)
                .header(AUTHORIZATION, requestInternalToken)
                .header(MEMBER_ID, writer.id)
                .contentType(APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_get_community",
                pathParameters(
                    parameterWithName("community_id").description("글 ID")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("글 ID"),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("is_win").type(BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                    fieldWithPath("event_id").type(NUMBER).description("이벤트 ID").optional(),
                    fieldWithPath("title").type(STRING).description("제목").optional(),
                    fieldWithPath("contents").type(STRING).description("내용"),
                    fieldWithPath("is_like").type(BOOLEAN).description("좋아요 여부").optional(),
                    fieldWithPath("files").type(ARRAY).description("파일 List").optional(),
                    fieldWithPath("files.[].type").type(STRING).description(generateLinkCode(FILE_TYPE)),
                    fieldWithPath("files.[].thumbnail_url").type(STRING).description("파일 썸네일 URL").optional(),
                    fieldWithPath("files.[].url").type(STRING).description("파일 URL"),
                    fieldWithPath("files.[].duration").type(NUMBER).description("비디오 길이 ms").optional(),
                    fieldWithPath("votes").type(ARRAY).description("투표 파일 List").optional(),
                    fieldWithPath("votes.[].id").type(NUMBER).description("투표 파일 아이디"),
                    fieldWithPath("votes.[].file_url").type(STRING).description("투표 파일 URL"),
                    fieldWithPath("votes.[].count").type(NUMBER).description("투표 수"),
                    fieldWithPath("votes.[].is_voted").type(BOOLEAN).description("투표 파일에 대한 유저 투표 여부"),
                    fieldWithPath("view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("like_count").type(NUMBER).description("좋아요수"),
                    fieldWithPath("comment_count").type(NUMBER).description("댓글/대댓글수"),
                    fieldWithPath("report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("created_at").type(STRING).description("작성일").attributes(getZonedDateFormat()),
                    fieldWithPath("relation_info").type(OBJECT).description("유저와의 관계 정보"),
                    fieldWithPath("relation_info.is_like").type(BOOLEAN).description("글 좋아요 여부"),
                    fieldWithPath("relation_info.is_block").type(BOOLEAN).description("작성자 차단 여부"),
                    fieldWithPath("relation_info.is_report").type(BOOLEAN).description("글 신고 여부"),
                    fieldWithPath("relation_info.is_scrap").type(BOOLEAN).description("글 스크랩 여부"),
                    fieldWithPath("member").type(OBJECT).description("작성자 정보"),
                    fieldWithPath("member.id").type(NUMBER).description("작성자 아이디").optional(),
                    fieldWithPath("member.status").type(STRING).description(generateLinkCode(MEMBER_STATUS)),
                    fieldWithPath("member.username").type(STRING).description("작성자 이름").optional(),
                    fieldWithPath("member.avatar_url").type(STRING).description("작성자 아바타 URL").optional(),
                    fieldWithPath("category").type(OBJECT).description("카테고리 정보"),
                    fieldWithPath("category.id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("category.type").type(STRING).description("카테고리 구분")
                        .description(generateLinkCode(COMMUNITY_CATEGORY_TYPE)),
                    fieldWithPath("category.title").type(STRING).description("카테고리 제목"),
                    fieldWithPath("category.hint").type(STRING).description("카테고리 힌트")
                )
            )
        )
    }

    @Test
    fun editCommunity() {

        // given
        val community: Community = saveCommunity(member = writer)
        val request = mapOf("title" to "Test Title", "contents" to "Test Contents", "files" to null)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                put("/internal/2/community/{community_id}", community.id)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, writer.id)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_edit_community",
                pathParameters(
                    parameterWithName("community_id").description("글 ID")
                ),
                requestFields(
                    fieldWithPath("title").type(STRING).description("제목").optional(),
                    fieldWithPath("contents").type(STRING).description("내용"),
                    fieldWithPath("files").type(ARRAY).description("파일 작업 정보 목록").optional(),
                    fieldWithPath("files.[].operation").type(STRING).description("파일 상태")
                        .description(generateLinkCode(FILE_OPERATION_TYPE)),
                    fieldWithPath("files.[].type").type(STRING).description("파일 타입")
                        .description(generateLinkCode(FILE_TYPE)),
                    fieldWithPath("files.[].thumbnail_url").type(STRING).description("파일 썸네일 URL").optional(),
                    fieldWithPath("files.[].url").type(STRING).description("파일 URL"),
                    fieldWithPath("files.[].need_transcode")
                        .type(BOOLEAN)
                        .description("비디오 파일 트랜스코딩 필요 여부 (현 ios true 요청하면 됩니다)")
                        .attributes(getDefault(false))
                        .optional()
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("글 ID"),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("is_win").type(BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                    fieldWithPath("title").type(STRING).description("제목").optional(),
                    fieldWithPath("contents").type(STRING).description("내용"),
                    fieldWithPath("files").type(ARRAY).description("파일 List").optional(),
                    fieldWithPath("files.[].type").type(STRING).description(generateLinkCode(FILE_TYPE)),
                    fieldWithPath("files.[].thumbnail_url").type(STRING).description("파일 썸네일 URL").optional(),
                    fieldWithPath("files.[].url").type(STRING).description("파일 URL"),
                    fieldWithPath("files.[].duration").type(NUMBER).description("비디오 길이 ms").optional(),
                    fieldWithPath("votes").type(ARRAY).description("투표 파일 List").optional(),
                    fieldWithPath("votes.[].id").type(NUMBER).description("투표 파일 아이디"),
                    fieldWithPath("votes.[].file_url").type(STRING).description("투표 파일 URL"),
                    fieldWithPath("votes.[].count").type(NUMBER).description("투표 수"),
                    fieldWithPath("votes.[].is_voted").type(BOOLEAN).description("투표 파일에 대한 유저 투표 여부"),
                    fieldWithPath("view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("like_count").type(NUMBER).description("좋아요수"),
                    fieldWithPath("comment_count").type(NUMBER).description("댓글/대댓글수"),
                    fieldWithPath("report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("created_at").type(STRING).description("작성일").attributes(getZonedDateFormat()),
                    fieldWithPath("relation_info").type(OBJECT).description("유저와의 관계 정보"),
                    fieldWithPath("relation_info.is_like").type(BOOLEAN).description("글 좋아요 여부"),
                    fieldWithPath("relation_info.is_block").type(BOOLEAN).description("작성자 차단 여부"),
                    fieldWithPath("relation_info.is_report").type(BOOLEAN).description("글 신고 여부"),
                    fieldWithPath("relation_info.is_scrap").type(BOOLEAN).description("글 스크랩 여부"),
                    fieldWithPath("member").type(OBJECT).description("작성자 정보"),
                    fieldWithPath("member.id").type(NUMBER).description("작성자 아이디").optional(),
                    fieldWithPath("member.status").type(STRING).description(generateLinkCode(MEMBER_STATUS)),
                    fieldWithPath("member.username").type(STRING).description("작성자 이름").optional(),
                    fieldWithPath("member.avatar_url").type(STRING).description("작성자 아바타 URL").optional(),
                    fieldWithPath("category").type(OBJECT).description("카테고리 정보"),
                    fieldWithPath("category.id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("category.type").type(STRING).description("카테고리 구분")
                        .description(generateLinkCode(COMMUNITY_CATEGORY_TYPE)),
                    fieldWithPath("category.title").type(STRING).description("카테고리 제목"),
                    fieldWithPath("category.hint").type(STRING).description("카테고리 힌트")
                )
            )
        )
    }

    @Test
    fun deleteCommunity() {

        // given
        val community: Community = saveCommunity(member = writer)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                delete("/internal/1/community/{community_id}", community.id)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, writer.id)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_delete_community",
                pathParameters(
                    parameterWithName("community_id").description("글 ID")
                )
            )
        )
    }

    @Test
    fun likeCommunity() {

        // given
        val community: Community = saveCommunity(member = writer)
        val bool = BooleanDto(true)
        notificationMessageCenterRepository.save(makeNotificationMessageCenterEntity(templateId = COMMUNITY_LIKE_1))
        notificationMessagePushRepository.save(makeNotificationMessagePushEntity(templateId = COMMUNITY_LIKE_1))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                patch("/internal/1/community/{community_id}/like", community.id)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, writer.id)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bool))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_like_community",
                pathParameters(
                    parameterWithName("community_id").description("글 ID")
                ),
                requestFields(
                    fieldWithPath("bool").type(BOOLEAN).description("좋아요 여부")
                ),
                responseFields(
                    fieldWithPath("is_like").type(BOOLEAN).description("좋아요 여부"),
                    fieldWithPath("like_count").type(NUMBER).description("커뮤니티 좋아요 수")
                )
            )
        )
    }

    @Test
    fun reportCommunity() {

        // given
        val community: Community = saveCommunity(member = requestUser)

        val report = ReportRequest.builder()
            .isReport(true)
            .description("신고사유")
            .build()

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                patch("/internal/1/community/{community_id}/report", community.id)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, writer.id)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(report))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_report_community",
                pathParameters(
                    parameterWithName("community_id").description("글 ID")
                ),
                requestFields(
                    fieldWithPath("is_report").type(BOOLEAN).description("신고 여부"),
                    fieldWithPath("description").type(STRING).description("신고 사유. 신고여부가 true 일때만 필수").optional()
                ),
                responseFields(
                    fieldWithPath("is_report").type(BOOLEAN).description("신고 여부"),
                    fieldWithPath("report_count").type(NUMBER).description("커뮤니티 신고수")
                )
            )
        )
    }

    @Test
    fun uploadFiles() {

        // given
        val file = MockMultipartFile("files", "file", "image/jpeg", "mock".toByteArray())

        // when & then
        val result = mockMvc
            .perform(
                multipart("/internal/1/file")
                    .file(file)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .header(MEMBER_ID, writer.id)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_upload_file",
                requestParts(
                    partWithName("files").description("업로드할 파일 목록")
                ),
                responseFields(
                    fieldWithPath("[]").type(ARRAY).description("UPLOAD 된 파일 URL")
                )
            )
        )
    }

    fun saveCommunity(
        category: CommunityCategory = communityCategory,
        member: Member? = null,
        voteList: List<CommunityVote>? = listOf()
    ): Community {
        return communityRepository.save(
            makeCommunity(
                category = category,
                member = member,
                communityVoteList = voteList
            )
        );
    }
}
