package com.jocoos.mybeautip.domain.community.api.front

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
import com.jocoos.mybeautip.testutil.fixture.*
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.global.dto.single.BooleanDto
import com.jocoos.mybeautip.member.Member
import com.jocoos.mybeautip.member.MemberRepository
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.boot.test.web.server.LocalServerPort
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

@TestInstance(PER_CLASS)
class CommunityControllerTest(
    private val memberRepository: MemberRepository,
    private val communityRepository: CommunityRepository,
    private val communityCategoryRepository: CommunityCategoryRepository,
    private val notificationMessageCenterRepository: NotificationMessageCenterRepository,
    private val notificationMessagePushRepository: NotificationMessagePushRepository,
    @LocalServerPort private val port: Int
) : RestDocsIntegrationTestSupport() {

    private lateinit var writer: Member
    private lateinit var communityCategory: CommunityCategory

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

    @Test
    fun writeCommunity() {

        val request = WriteCommunityRequest.builder()
            .categoryId(communityCategory.id)
            .title("Mock Title")
            .contents("Mock Contents")
            .build()

        val result: ResultActions = mockMvc
            .perform(
                post("/api/2/community")
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "write_community",
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
                    fieldWithPath("files.[].url").type(STRING).description("파일 URL")
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

//    @Test
//    fun uploadFiles() {
//        val file = MockMultipartFile("files", "file", "image/jpeg", "mock".toByteArray())
//        val result: ResultActions = mockMvc.perform(
//            RestDocumentationRequestBuilders
//                .multipart("/api/1/community/files")
//                .file(file)
//        )
//            .andExpect(status().isOk)
//            .andDo(print())
//        result.andDo(
//            document(
//                "upload_file_community",
//                RequestDocumentation.requestParts(
//                    RequestDocumentation.partWithName("files").description("업로드할 파일 목록")
//                ),
//                responseFields(
//                    fieldWithPath("[]").type(ARRAY).description("UPLOAD 된 파일 URL")
//                )
//            )
//        )
//    }

    @Test
    fun getCommunities() {

        saveCommunity(member = writer, voteList = null)

        val result: ResultActions = mockMvc
            .perform(
                get("/api/2/community")
                    .param("category_id", communityCategory.id.toString())
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_communities",
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
            get("/api/2/community/{community_id}", community.id)
                .header(AUTHORIZATION, requestUserToken)
                .contentType(APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_community",
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
        val community: Community = saveCommunity(member = requestUser)
        val request = mapOf("title" to "Test Title", "contents" to "Test Contents", "files" to null)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                put("/api/2/community/{community_id}", community.id)
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "edit_community",
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
                    fieldWithPath("files.[].url").type(STRING).description("파일 URL")
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
        val community: Community = saveCommunity(member = requestUser)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                delete("/api/1/community/{community_id}", community.id)
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "delete_community",
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
                patch("/api/1/community/{community_id}/like", community.id)
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bool))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "like_community",
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
        val community: Community = saveCommunity(member = writer)
        val report = ReportRequest.builder()
            .isReport(true)
            .description("신고사유")
            .build()

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                patch("/api/1/community/{community_id}/report", community.id)
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(report))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "report_community",
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
    fun isReportCommunity() {

        // given
        val community: Community = saveCommunity(member = writer)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/community/{community_id}/report", community.id)
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "check_report_community",
                pathParameters(
                    parameterWithName("community_id").description("글 ID")
                ),
                responseFields(
                    fieldWithPath("is_report").type(BOOLEAN).description("신고 여부"),
                    fieldWithPath("report_count").type(NUMBER)
                        .description("커뮤니티 신고수")
                )
            )
        )
    }

    @Test
    fun scrap() {

        // given
        val community: Community = saveCommunity(member = writer)
        val bool = BooleanDto(true)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                patch("/api/1/community/{community_id}/scrap", community.id)
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bool))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "community_scrap",
                pathParameters(
                    parameterWithName("community_id").description("글 ID")
                ),
                requestFields(
                    fieldWithPath("bool").type(BOOLEAN).description("스크랩 여부")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("스크랩 아이디"),
                    fieldWithPath("type").type(STRING).description("스크랩 타입"),
                    fieldWithPath("community_id").type(NUMBER).description("스크랩 커뮤니티 아이디"),
                    fieldWithPath("is_scrap").type(BOOLEAN).description("스크랩 여부"),
                    fieldWithPath("created_at").type(STRING).description("스크랩 생성일시")
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

//    fun saveCommunityByApiCall() {
//        val request = WriteCommunityRequest.builder()
//            .categoryId(communityCategory.id)
//            .title("Mock Title")
//            .contents("Mock Contents")
//            .build()
//
//        Given {
//            port(port)
//            header(AUTHORIZATION, requestUserToken)
//            contentType(APPLICATION_JSON_VALUE)
//            body(request)
//        } When {
//            post("/api/1/community")
//        } Then {
//            status().isOk
//        }
//    }
}
