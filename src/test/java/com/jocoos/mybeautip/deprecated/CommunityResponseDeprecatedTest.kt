package com.jocoos.mybeautip.deprecated

import com.jocoos.mybeautip.*
import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.BLIND
import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.VOTE
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityRequest
import com.jocoos.mybeautip.domain.community.persistence.domain.Community
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityFile
import com.jocoos.mybeautip.domain.community.persistence.domain.vote.CommunityVote
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCategoryRepository
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityRepository
import com.jocoos.mybeautip.domain.scrap.persistence.repository.ScrapRepository
import com.jocoos.mybeautip.domain.search.code.SearchType.COMMUNITY
import com.jocoos.mybeautip.testutil.fixture.*
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.global.constant.MybeautipConstant.MAX_LONG_STRING
import com.jocoos.mybeautip.member.Member
import com.jocoos.mybeautip.member.MemberRepository
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
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
class CommunityResponseDeprecatedTest(
    private val memberRepository: MemberRepository,
    private val communityCategoryRepository: CommunityCategoryRepository,
    private val communityRepository: CommunityRepository,
    private val scrapRepository: ScrapRepository
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

        // given
        val request = WriteCommunityRequest.builder()
            .categoryId(communityCategory.id)
            .title("Mock Title")
            .contents("Mock Contents")
            .build()

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                post("/api/1/community")
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "write_community_v1",
                requestFields(
                    fieldWithPath("category_id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("event_id").type(NUMBER).description("이벤트 아이디. 드립N드림 일때 관련된 이벤트 아이디.").optional(),
                    fieldWithPath("title").type(STRING).description("제목. 수근수근에서만 필수").optional(),
                    fieldWithPath("contents").type(STRING).description("내용"),
                    fieldWithPath("['files']").type(ARRAY).description("파일 작업 정보 목록").optional(),
                    fieldWithPath("['files'].operation").type(STRING).description("파일 상태")
                        .description(generateLinkCode(FILE_OPERATION_TYPE)),
                    fieldWithPath("['files'].url").type(STRING).description("파일 URL")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("글 ID"),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("is_win").type(BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                    fieldWithPath("title").type(STRING).description("제목").optional(),
                    fieldWithPath("contents").type(STRING).description("내용"),
                    fieldWithPath("['file_url']").type(ARRAY).description("파일 URL List").optional(),
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
                get("/api/1/community")
                    .param("category_id", communityCategory.id.toString())
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_communities_v1",
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
                    fieldWithPath("content.[].['file_url']").type(ARRAY).description("파일 URL List").optional(),
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
                    fieldWithPath("content.[].member.status").type(STRING).description(
                        generateLinkCode(
                            MEMBER_STATUS
                        )
                    ),
                    fieldWithPath("content.[].member.username").type(STRING)
                        .description("작성자 이름").optional(),
                    fieldWithPath("content.[].member.avatar_url").type(STRING)
                        .description("작성자 아바타 URL").optional(),
                    fieldWithPath("content.[].category").type(OBJECT)
                        .description("카테고리 정보"),
                    fieldWithPath("content.[].category.id").type(NUMBER)
                        .description("카테고리 아이디"),
                    fieldWithPath("content.[].category.type").type(STRING)
                        .description("카테고리 구분").description(
                            generateLinkCode(
                                COMMUNITY_CATEGORY_TYPE
                            )
                        ),
                    fieldWithPath("content.[].category.title").type(STRING)
                        .description("카테고리 제목"),
                    fieldWithPath("content.[].category.hint").type(STRING)
                        .description("카테고리 힌트")
                )
            )
        )
    }

    @Test
    fun getCommunity() {

        // given
        val community: Community = saveCommunity(member = writer)

        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/community/{community_id}", community.id)
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_community_v1",
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
                    fieldWithPath("['file_url']").type(ARRAY).description("파일 URL List").optional(),
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
                put("/api/1/community/{community_id}", community.id)
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "edit_community_v1",
                pathParameters(
                    parameterWithName("community_id").description("글 ID")
                ),
                requestFields(
                    fieldWithPath("title").type(STRING).description("제목").optional(),
                    fieldWithPath("contents").type(STRING).description("내용"),
                    fieldWithPath("['files']").type(ARRAY).description("파일 작업 정보 목록").optional(),
                    fieldWithPath("['files'].operation").type(STRING).description("파일 상태")
                        .description(generateLinkCode(FILE_OPERATION_TYPE)),
                    fieldWithPath("['files'].url").type(STRING).description("파일 URL")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("글 ID"),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("is_win").type(BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                    fieldWithPath("title").type(STRING).description("제목").optional(),
                    fieldWithPath("contents").type(STRING).description("내용"),
                    fieldWithPath("['file_url']").type(ARRAY).description("파일 URL List").optional(),
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
    fun summaryCommunityTop() {


        // given
        val category: CommunityCategory = communityCategoryRepository.save(makeCommunityCategory(isInSummary = true))
        communityRepository.save(makeCommunity(category = category))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/summary/community/top")
                    .header(AUTHORIZATION, requestUserToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "summary_community_top_v1",
                responseFields(
                    fieldWithPath("category").type(ARRAY).description("커뮤니티 메인 상단 탭 카테고리"),
                    fieldWithPath("category.[].id").type(NUMBER).description("커뮤니티 아이디"),
                    fieldWithPath("category.[].type").type(STRING).description("카테고리 구분")
                        .description(generateLinkCode(COMMUNITY_CATEGORY_TYPE)),
                    fieldWithPath("category.[].title").type(STRING).description("제목"),
                    fieldWithPath("category.[].hint").type(STRING).description("힌트"),
                    fieldWithPath("content").type(ARRAY).description("커뮤니티 메인 상단 게시글 정보"),
                    fieldWithPath("content.[].category_id").type(NUMBER).description("커뮤니티 카테고리 아이디"),
                    fieldWithPath("content.[].community").type(ARRAY).description("게시글 목록"),
                    fieldWithPath("content.[].community.[].id").type(NUMBER).description("글 ID"),
                    fieldWithPath("content.[].community.[].is_win").type(BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부")
                        .optional(),
                    fieldWithPath("content.[].community.[].status").type(STRING)
                        .description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("content.[].community.[].event_id").type(NUMBER).description("이벤트 ID").optional(),
                    fieldWithPath("content.[].community.[].event_title").type(STRING).description("이벤트 제목").optional(),
                    fieldWithPath("content.[].community.[].title").type(STRING).description("제목").optional(),
                    fieldWithPath("content.[].community.[].contents").type(STRING).description("내용").optional(),
                    fieldWithPath("content.[].community.[].['file_url']").type(ARRAY).description("파일 URL List")
                        .optional(),
                    fieldWithPath("content.[].community.[].view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("content.[].community.[].like_count").type(NUMBER).description("좋아요수"),
                    fieldWithPath("content.[].community.[].comment_count").type(NUMBER).description("댓글/대댓글수"),
                    fieldWithPath("content.[].community.[].report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("content.[].community.[].created_at").type(STRING).description("작성일")
                        .attributes(getZonedDateFormat()),
                    fieldWithPath("content.[].community.[].relation_info").type(OBJECT).description("유저와의 관계 정보"),
                    fieldWithPath("content.[].community.[].relation_info.is_like").type(BOOLEAN)
                        .description("글 좋아요 여부"),
                    fieldWithPath("content.[].community.[].relation_info.is_block").type(BOOLEAN)
                        .description("작성자 차단 여부"),
                    fieldWithPath("content.[].community.[].relation_info.is_report").type(BOOLEAN)
                        .description("글 신고 여부"),
                    fieldWithPath("content.[].community.[].relation_info.is_scrap").type(BOOLEAN)
                        .description("글 스크랩 여부"),
                    fieldWithPath("content.[].community.[].member").type(OBJECT).description("작성자 정보."),
                    fieldWithPath("content.[].community.[].member.id").type(NUMBER).description("작성자 아이디").optional(),
                    fieldWithPath("content.[].community.[].member.status").type(STRING)
                        .description(generateLinkCode(MEMBER_STATUS)),
                    fieldWithPath("content.[].community.[].member.username").type(STRING).description("작성자 이름")
                        .optional(),
                    fieldWithPath("content.[].community.[].member.avatar_url").type(STRING).description("작성자 아바타 URL")
                        .optional(),
                    fieldWithPath("content.[].community.[].category").type(OBJECT).description("카테고리 정보"),
                    fieldWithPath("content.[].community.[].category.id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("content.[].community.[].category.type").type(STRING).description("카테고리 구분")
                        .description(generateLinkCode(COMMUNITY_CATEGORY_TYPE)),
                    fieldWithPath("content.[].community.[].category.title").type(STRING).description("카테고리 제목"),
                    fieldWithPath("content.[].community.[].category.hint").type(STRING).description("카테고리 힌트")
                )
            )
        )
    }

    @Test
    fun summaryCommunityVote() {

        // given
        saveCommunityVote()

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/summary/community/{type}", VOTE)
                    .header(AUTHORIZATION, requestUserToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "summary_community_vote_v1",
                pathParameters(parameterWithName("type").description(generateLinkCode(COMMUNITY_CATEGORY_TYPE))),
                responseFields(
                    fieldWithPath("[]").type(ARRAY).description("Vote 커뮤니티 목록").optional(),
                    fieldWithPath("[].id").type(NUMBER).description("글 ID"),
                    fieldWithPath("[].status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("[].is_win").type(BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                    fieldWithPath("[].title").type(STRING).description("제목").optional(),
                    fieldWithPath("[].contents").type(STRING).description("내용"),
                    fieldWithPath("[].['file_url']").type(ARRAY).description("파일 URL List").optional(),
                    fieldWithPath("[].votes").type(ARRAY).description("투표 파일 List").optional(),
                    fieldWithPath("[].votes.[].id").type(NUMBER).description("투표 파일 아이디"),
                    fieldWithPath("[].votes.[].file_url").type(STRING).description("투표 파일 URL"),
                    fieldWithPath("[].view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("[].like_count").type(NUMBER).description("좋아요수"),
                    fieldWithPath("[].comment_count").type(NUMBER).description("댓글수"),
                    fieldWithPath("[].report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("[].created_at").type(STRING).description("작성일").attributes(getZonedDateFormat()),
                    fieldWithPath("[].relation_info").type(OBJECT).description("유저와의 관계 정보"),
                    fieldWithPath("[].relation_info.is_like").type(BOOLEAN).description("글 좋아요 여부"),
                    fieldWithPath("[].relation_info.is_block").type(BOOLEAN).description("작성자 차단 여부"),
                    fieldWithPath("[].relation_info.is_report").type(BOOLEAN).description("글 신고 여부"),
                    fieldWithPath("[].relation_info.is_scrap").type(BOOLEAN).description("글 스크랩 여부"),
                    fieldWithPath("[].member").type(OBJECT).description("작성자 정보"),
                    fieldWithPath("[].member.id").type(NUMBER).description("작성자 아이디").optional(),
                    fieldWithPath("[].member.status").type(STRING).description(generateLinkCode(MEMBER_STATUS)),
                    fieldWithPath("[].member.username").type(STRING).description("작성자 이름").optional(),
                    fieldWithPath("[].member.avatar_url").type(STRING).description("작성자 아바타 URL").optional(),
                    fieldWithPath("[].category").type(OBJECT).description("카테고리 정보"),
                    fieldWithPath("[].category.id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("[].category.type").type(STRING).description("카테고리 구분")
                        .description(generateLinkCode(COMMUNITY_CATEGORY_TYPE)),
                    fieldWithPath("[].category.title").type(STRING).description("카테고리 제목"),
                    fieldWithPath("[].category.hint").type(STRING).description("카테고리 힌트")
                )
            )
        )
    }

    @Test
    fun summaryCommunityBlind() {

        // given
        val category: CommunityCategory = communityCategoryRepository.save(makeCommunityCategory(type = BLIND))
        communityRepository.save(makeCommunity(category = category))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/summary/community/{type}", BLIND)
                    .header(AUTHORIZATION, requestUserToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "summary_community_blind_v1",
                pathParameters(
                    parameterWithName("type").description(generateLinkCode(COMMUNITY_CATEGORY_TYPE))
                ),
                responseFields(
                    fieldWithPath("[].id").type(NUMBER).description("글 ID"),
                    fieldWithPath("[].status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("[].is_win").type(BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                    fieldWithPath("[].title").type(STRING).description("제목").optional(),
                    fieldWithPath("[].['file_url']").type(ARRAY).description("파일 URL List").optional(),
                    fieldWithPath("[].votes").type(ARRAY).description("투표 파일 List").optional(),
                    fieldWithPath("[].votes.[].id").type(NUMBER).description("투표 파일 아이디"),
                    fieldWithPath("[].votes.[].file_url").type(STRING).description("투표 파일 URL"),
                    fieldWithPath("[].votes.[].count").type(NUMBER).description("투표 수"),
                    fieldWithPath("[].votes.[].is_voted").type(BOOLEAN).description("투표 파일에 대한 유저 투표 여부"),
                    fieldWithPath("[].view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("[].like_count").type(NUMBER).description("좋아요수"),
                    fieldWithPath("[].comment_count").type(NUMBER).description("댓글수"),
                    fieldWithPath("[].report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("[].created_at").type(STRING).description("작성일").attributes(getZonedDateFormat()),
                    fieldWithPath("[].relation_info").type(OBJECT).description("유저와의 관계 정보"),
                    fieldWithPath("[].relation_info.is_like").type(BOOLEAN).description("글 좋아요 여부"),
                    fieldWithPath("[].relation_info.is_block").type(BOOLEAN).description("작성자 차단 여부"),
                    fieldWithPath("[].relation_info.is_report").type(BOOLEAN).description("글 신고 여부"),
                    fieldWithPath("[].relation_info.is_scrap").type(BOOLEAN).description("글 스크랩 여부"),
                    fieldWithPath("[].member").type(OBJECT).description("작성자 정보"),
                    fieldWithPath("[].member.id").type(NUMBER).description("작성자 아이디").optional(),
                    fieldWithPath("[].member.status").type(STRING).description(generateLinkCode(MEMBER_STATUS)),
                    fieldWithPath("[].member.username").type(STRING).description("작성자 이름").optional(),
                    fieldWithPath("[].member.avatar_url").type(STRING).description("작성자 아바타 URL").optional(),
                    fieldWithPath("[].category").type(OBJECT).description("카테고리 정보"),
                    fieldWithPath("[].category.id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("[].category.type").type(STRING).description("카테고리 구분")
                        .description(generateLinkCode(COMMUNITY_CATEGORY_TYPE)),
                    fieldWithPath("[].category.title").type(STRING).description("카테고리 제목"),
                    fieldWithPath("[].category.hint").type(STRING).description("카테고리 힌트")
                )
            )
        )
    }

    @Test
    fun getMyCommunities() {

        // given
        saveCommunity(member = requestUser)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
            get("/api/1/my/community")
                .header(AUTHORIZATION, requestUserToken)
                .contentType(APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_my_communities_v1",
                requestParameters(
                    parameterWithName("cursor").description("커서").optional().attributes(getDefault(MAX_LONG_STRING)),
                    parameterWithName("size").description("페이지 사이즈").optional().attributes(getDefault(20))
                ),
                responseFields(
                    fieldWithPath("next_cursor").type(STRING).description("커서 정보"),
                    fieldWithPath("content").type(ARRAY).description("커뮤니티 글 목록"),
                    fieldWithPath("content.[].id").type(NUMBER).description("글 ID"),
                    fieldWithPath("content.[].is_win").type(BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                    fieldWithPath("content.[].status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("content.[].title").type(STRING).description("제목").optional(),
                    fieldWithPath("content.[].contents").type(STRING).description("내용").optional(),
                    fieldWithPath("content.[].file_url").type(STRING).description("메인 파일 URL").optional(),
                    fieldWithPath("content.[].comment_count").type(NUMBER).description("댓글/대댓글수"),
                    fieldWithPath("content.[].report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("content.[].created_at").type(STRING).description("작성일").attributes(getZonedDateFormat()),
                    fieldWithPath("content.[].category").type(OBJECT).description("카테고리 정보"),
                    fieldWithPath("content.[].category.id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("content.[].category.type").type(STRING).description("카테고리 구분").description(generateLinkCode(COMMUNITY_CATEGORY_TYPE)),
                    fieldWithPath("content.[].category.title").type(STRING).description("카테고리 제목"),
                    fieldWithPath("content.[].category.hint").type(STRING).description("카테고리 힌트")
                )
            )
        )
    }

    @Test
    fun getScrap() {

        // given
        val community: Community = saveCommunity(member = defaultAdmin)
        scrapRepository.save(makeCommunityScrap(requestUser.id, community.id))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
            get("/api/1/my/scrap")
                .header(AUTHORIZATION, requestUserToken)
                .contentType(APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_community_scraps_v1",
                requestParameters(
                    parameterWithName("cursor").description("커서").optional(),
                    parameterWithName("size").description("조회 갯수").optional().attributes(getDefault(20))
                ),
                responseFields(
                    fieldWithPath("next_cursor").type(STRING).description("커서 정보").attributes(getZonedDateMilliFormat()).optional(),
                    fieldWithPath("content").type(ARRAY).description("커뮤니티 스크랩 목록").optional(),
                    fieldWithPath("content.[].id").type(NUMBER).description("커뮤니티 ID"),
                    fieldWithPath("content.[].type").type(STRING).description("스크랩 타"),
                    fieldWithPath("content.[].scrap_id").type(NUMBER).description("스크랩 ID"),
                    fieldWithPath("content.[].is_win").type(BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                    fieldWithPath("content.[].status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("content.[].title").type(STRING).description("제목").optional(),
                    fieldWithPath("content.[].contents").type(STRING).description("내용").optional(),
                    fieldWithPath("content.[].file_url").type(ARRAY).description("파일 URL").optional(),
                    fieldWithPath("content.[].votes").type(ARRAY).description("투표 URL").optional(),
                    fieldWithPath("content.[].view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("content.[].like_count").type(NUMBER).description("좋아요수"),
                    fieldWithPath("content.[].comment_count").type(NUMBER).description("댓글/대댓글수"),
                    fieldWithPath("content.[].report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("content.[].created_at").type(STRING).description("작성일").attributes(getZonedDateFormat()),
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
                    fieldWithPath("content.[].category.type").type(STRING).description("카테고리 구분").description(generateLinkCode(COMMUNITY_CATEGORY_TYPE)),
                    fieldWithPath("content.[].category.title").type(STRING).description("카테고리 제목"),
                    fieldWithPath("content.[].category.hint").type(STRING).description("카테고리 힌트")
                )
            )
        )
    }

    @Test
    fun searchCommunityTest() {

        // given
        val keyword = "key"
        communityRepository.save(makeCommunity(category = communityCategory, member = defaultAdmin, contents = keyword))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
            get("/api/1/search")
                .param("type", "COMMUNITY")
                .param("keyword", keyword)
                .param("size", "20")
                .header(AUTHORIZATION, requestUserToken)
        )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "search_community_v1",
                requestParameters(
                    parameterWithName("type").description("검색 타입").attributes(getDefault(COMMUNITY)).optional().description(generateLinkCode(SEARCH_TYPE)),
                    parameterWithName("keyword").description("검색어 (1자 이상 20자 이하)"),
                    parameterWithName("cursor").description("커서").optional().attributes(getZonedDateMilliFormat(), getDefault("현재 시간")),
                    parameterWithName("size").description("조회 개").optional().attributes(getDefault(20))
                ),
                responseFields(
                    fieldWithPath("next_cursor").type(STRING).description("커서 정보").attributes(getZonedDateMilliFormat()),
                    fieldWithPath("count").type(NUMBER).description("검색 결과 수"),
                    fieldWithPath("community").type(ARRAY).description("커뮤니티 글 목록"),
                    fieldWithPath("community.[].id").type(NUMBER).description("글 ID"),
                    fieldWithPath("community.[].is_win").type(BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                    fieldWithPath("community.[].status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("community.[].event_id").type(NUMBER).description("이벤트 ID").optional(),
                    fieldWithPath("community.[].title").type(STRING).description("제목").optional(),
                    fieldWithPath("community.[].contents").type(STRING).description("내용").optional(),
                    fieldWithPath("community.[].['file_url']").type(ARRAY).description("파일 URL List").optional(),
                    fieldWithPath("community.[].votes").type(ARRAY).description("투표 파일 List").optional(),
                    fieldWithPath("community.[].votes.[].id").type(NUMBER).description("투표 파일 아이디"),
                    fieldWithPath("community.[].votes.[].file_url").type(STRING).description("투표 파일 URL"),
                    fieldWithPath("community.[].votes.[].count").type(NUMBER).description("투표 수"),
                    fieldWithPath("community.[].votes.[].is_voted").type(BOOLEAN).description("투표 파일에 대한 유저 투표 여부"),
                    fieldWithPath("community.[].view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("community.[].like_count").type(NUMBER).description("좋아요수"),
                    fieldWithPath("community.[].comment_count").type(NUMBER).description("댓글/대댓글수"),
                    fieldWithPath("community.[].report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("community.[].created_at").type(STRING).description("작성일").attributes(getZonedDateFormat()),
                    fieldWithPath("community.[].relation_info").type(OBJECT).description("유저와의 관계 정보"),
                    fieldWithPath("community.[].relation_info.is_like").type(BOOLEAN).description("글 좋아요 여부"),
                    fieldWithPath("community.[].relation_info.is_block").type(BOOLEAN).description("작성자 차단 여부"),
                    fieldWithPath("community.[].relation_info.is_report").type(BOOLEAN).description("글 신고 여부"),
                    fieldWithPath("community.[].relation_info.is_scrap").type(BOOLEAN).description("스크랩 여부"),
                    fieldWithPath("community.[].member").type(OBJECT).description("작성자 정보."),
                    fieldWithPath("community.[].member.id").type(NUMBER).description("작성자 아이디").optional(),
                    fieldWithPath("community.[].member.status").type(STRING).description(generateLinkCode(MEMBER_STATUS)),
                    fieldWithPath("community.[].member.username").type(STRING).description("작성자 이름").optional(),
                    fieldWithPath("community.[].member.avatar_url").type(STRING).description("작성자 아바타 URL").optional(),
                    fieldWithPath("community.[].category").type(OBJECT).description("카테고리 정보"),
                    fieldWithPath("community.[].category.id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("community.[].category.type").type(STRING).description("카테고리 구분").description(generateLinkCode(COMMUNITY_CATEGORY_TYPE)),
                    fieldWithPath("community.[].category.title").type(STRING).description("카테고리 제목"),
                    fieldWithPath("community.[].category.hint").type(STRING).description("카테고리 힌트")
                )
            )
        )
    }

    fun saveCommunity(
        category: CommunityCategory = communityCategory,
        member: Member? = null,
        voteList: List<CommunityVote>? = listOf()
    ): Community {
        return communityRepository.save(makeCommunity(category = category, member = member, communityVoteList = voteList));
    }

    private fun saveCommunityVote(): Pair<Community, CommunityVote> {
        val category: CommunityCategory = communityCategoryRepository.save(makeCommunityCategory(type = VOTE))

        val community: Community = makeCommunity(category = category)

        val communityFileToVote: CommunityFile = makeCommunityFile(community = community)
        val communityFile: CommunityFile = makeCommunityFile(community = community)


        val communityVoteToVote: CommunityVote = makeCommunityVote(community, communityFileToVote)
        val communityVote: CommunityVote = makeCommunityVote(community, communityFile)

        community.communityFileList = listOf(communityFileToVote, communityFile)
        community.communityVoteList = listOf(communityVoteToVote, communityVote)

        communityRepository.save(community)
        return Pair(community, communityVoteToVote)
    }
}
