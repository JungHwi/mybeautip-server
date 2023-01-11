package com.jocoos.mybeautip.domain.community.api.admin

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType
import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.DRIP
import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.NORMAL
import com.jocoos.mybeautip.domain.community.code.CommunityStatus
import com.jocoos.mybeautip.domain.community.dto.PatchCommunityRequest
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityRequest
import com.jocoos.mybeautip.domain.community.persistence.domain.Community
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCategoryRepository
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.global.dto.single.BooleanDto
import com.jocoos.mybeautip.testutil.fixture.makeCommunity
import com.jocoos.mybeautip.testutil.fixture.makeCommunityCategory
import com.jocoos.mybeautip.testutil.fixture.makeMember
import com.jocoos.mybeautip.member.Member
import com.jocoos.mybeautip.member.MemberRepository
import org.junit.jupiter.api.Test
import org.openapitools.jackson.nullable.JsonNullable
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

class AdminCommunityControllerTest(
    private val memberRepository: MemberRepository,
    private val communityCategoryRepository: CommunityCategoryRepository,
    private val communityRepository: CommunityRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun writeCommunity() {

        // given
        val communityCategory: CommunityCategory = saveCommunityCategory(NORMAL)

        val request = WriteCommunityRequest.builder()
            .status(CommunityStatus.NORMAL)
            .categoryId(communityCategory.id)
            .title("Mock Title")
            .contents("Mock Contents")
            .build()

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                post("/admin/community")
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isCreated)
            .andDo(print())

        result.andDo(
            document(
                "admin_write_community",
                requestFields(
                    fieldWithPath("category_id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("event_id").type(NUMBER).description("이벤트 아이디. 써봐줄게 일때 관련된 이벤트 아이디.").optional(),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("title").type(STRING).description("제목").optional(),
                    fieldWithPath("contents").type(STRING).description("내용"),
                    fieldWithPath("files").type(ARRAY).description("파일 List").optional(),
                    fieldWithPath("files.[].type").type(STRING).description("파일 타입")
                        .description(generateLinkCode(FILE_TYPE)),
                    fieldWithPath("files.[].thumbnail_url").type(STRING).description("파일 썸네일 URL").optional(),
                    fieldWithPath("files.[].url").type(STRING).description("파일 URL")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("커뮤니티 ID"),
                    fieldWithPath("is_win").type(BOOLEAN).description("당첨 여부").optional(),
                    fieldWithPath("is_top_fix").type(BOOLEAN).description("상단 고정 여부").optional(),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("title").type(STRING).description("제목").optional(),
                    fieldWithPath("event_title").type(STRING).description("이벤트 제목").optional(),
                    fieldWithPath("contents").type(STRING).description("내용").optional(),
                    fieldWithPath("files").type(ARRAY).description("파일 List").optional(),
                    fieldWithPath("files.[].type").type(STRING).description(generateLinkCode(FILE_TYPE)),
                    fieldWithPath("files.[].url").type(STRING).description("파일 URL"),
                    fieldWithPath("files.[].thumbnail_url").type(STRING).description("파일 썸네일 URL"),
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
                    fieldWithPath("member").type(OBJECT).description("작성자 정보.").optional(),
                    fieldWithPath("member.id").type(NUMBER).description("작성자 아이디").optional(),
                    fieldWithPath("member.status").type(STRING).description(generateLinkCode(MEMBER_STATUS)).optional(),
                    fieldWithPath("member.username").type(STRING).description("작성자 이름").optional(),
                    fieldWithPath("member.avatar_url").type(STRING).description("작성자 아바타 URL").optional(),
                    fieldWithPath("member.role").type(STRING).description(generateLinkCode(ROLE)).optional(),
                    fieldWithPath("category").type(OBJECT).description("카테고리 정보"),
                    fieldWithPath("category.id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("category.type").type(STRING).description("카테고리 구분")
                        .description(generateLinkCode(COMMUNITY_CATEGORY_TYPE)),
                    fieldWithPath("category.title").type(STRING).description("카테고리 제목")
                )
            )
        )
    }

    @Test
    fun editCommunity() {

        // given
        val community: Community = saveCommunity(member = defaultAdmin)

        val request = PatchCommunityRequest
            .builder()
            .contents(JsonNullable.of("수정"))
            .files(ArrayList())
            .build()

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                patch("/admin/community/{community_id}", community.id)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_edit_community",
                pathParameters(
                    parameterWithName("community_id").description("어드민 작성 게시글 ID")
                ),
                requestFields(
                    fieldWithPath("title").type(STRING).description("제목. 속닥속닥에서만 필수").optional(),
                    fieldWithPath("contents").type(STRING).description("내용"),
                    fieldWithPath("files").type(ARRAY).description("파일 List").optional(),
                    fieldWithPath("files.[].type").type(STRING).description("파일 타입")
                        .description(generateLinkCode(FILE_TYPE)),
                    fieldWithPath("files.[].thumbnail_url").type(STRING).description("파일 썸네일 URL").optional(),
                    fieldWithPath("files.[].url").type(STRING).description("파일 URL")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("커뮤니티 ID")
                )
            )
        )
    }

    @Test
    fun deleteAdminWriteCommunity() {

        // given
        val community: Community = saveCommunity(member = defaultAdmin)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                delete("/admin/community/{community_id}", community.id)
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_delete_community",
                pathParameters(
                    parameterWithName("community_id").description("어드민 작성 게시글 ID")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("커뮤니티 ID")
                )
            )
        )
    }

    @Test
    fun getCommunities() {

        // given
        saveCommunity()

        // when & then
        val result: ResultActions = mockMvc.perform(
            get("/admin/community")
                .header(AUTHORIZATION, defaultAdminToken)
        )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_communities",
                requestParameters(
                    parameterWithName("category_id").description("카테고리 아이디").optional(),
                    parameterWithName("page").description("페이지 넘버").optional().attributes(getDefault(1)),
                    parameterWithName("size").description("페이지 사이즈").optional().attributes(getDefault(10)),
                    parameterWithName("sort").description("정렬 필드").optional().attributes(getDefault("sortedAt")),
                    parameterWithName("order").description("정렬 방향").optional().attributes(getDefault("DESC")),
                    parameterWithName("search").description("검색 - 검색필드,검색어").optional(),
                    parameterWithName("start_at").description("검색 시작일자").optional(),
                    parameterWithName("end_at").description("검색 종료일자").optional()
                ),
                responseFields(
                    fieldWithPath("total").type(NUMBER).description("총 커뮤니티 개수"),
                    fieldWithPath("content").type(ARRAY).description("커뮤니티 목록"),
                    fieldWithPath("content.[].id").type(NUMBER).description("커뮤니티 ID"),
                    fieldWithPath("content.[].is_win").type(BOOLEAN).description("당첨 여부").optional(),
                    fieldWithPath("content.[].is_top_fix").type(BOOLEAN).description("상단 고정 여부").optional(),
                    fieldWithPath("content.[].status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("content.[].title").type(STRING).description("제목").optional(),
                    fieldWithPath("content.[].event_title").type(STRING).description("이벤트 제목").optional(),
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
                    fieldWithPath("content.[].member").type(OBJECT).description("작성자 정보.").optional(),
                    fieldWithPath("content.[].member.id").type(NUMBER).description("작성자 아이디").optional(),
                    fieldWithPath("content.[].member.status").type(STRING).description(generateLinkCode(MEMBER_STATUS))
                        .optional(),
                    fieldWithPath("content.[].member.username").type(STRING).description("작성자 이름").optional(),
                    fieldWithPath("content.[].member.avatar_url").type(STRING).description("작성자 아바타 URL").optional(),
                    fieldWithPath("content.[].member.role").type(STRING).description(generateLinkCode(ROLE)).optional(),
                    fieldWithPath("content.[].category").type(OBJECT).description("카테고리 정보"),
                    fieldWithPath("content.[].category.id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("content.[].category.type").type(STRING).description("카테고리 구분")
                        .description(generateLinkCode(COMMUNITY_CATEGORY_TYPE)),
                    fieldWithPath("content.[].category.title").type(STRING).description("카테고리 제목")
                )
            )
        )
    }

    @Test
    fun getCommunity() {

        // given
        val community: Community = saveCommunity(member = saveAdmin())

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/admin/community/{community_id}", community.id)
                    .header(AUTHORIZATION, defaultAdminToken)

            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_community",
                pathParameters(
                    parameterWithName("community_id").description("글 ID")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("커뮤니티 ID"),
                    fieldWithPath("is_win").type(BOOLEAN).description("당첨 여부").optional(),
                    fieldWithPath("is_top_fix").type(BOOLEAN).description("상단 고정 여부").optional(),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("title").type(STRING).description("제목").optional(),
                    fieldWithPath("event_title").type(STRING).description("이벤트 제목").optional(),
                    fieldWithPath("contents").type(STRING).description("내용").optional(),
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
                    fieldWithPath("member").type(OBJECT).description("작성자 정보.").optional(),
                    fieldWithPath("member.id").type(NUMBER).description("작성자 아이디").optional(),
                    fieldWithPath("member.status").type(STRING).description(generateLinkCode(MEMBER_STATUS)).optional(),
                    fieldWithPath("member.username").type(STRING).description("작성자 이름").optional(),
                    fieldWithPath("member.avatar_url").type(STRING).description("작성자 아바타 URL").optional(),
                    fieldWithPath("member.role").type(STRING).description(generateLinkCode(ROLE)).optional(),
                    fieldWithPath("category").type(OBJECT).description("카테고리 정보"),
                    fieldWithPath("category.id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("category.type").type(STRING).description("카테고리 구분")
                        .description(generateLinkCode(COMMUNITY_CATEGORY_TYPE)),
                    fieldWithPath("category.title").type(STRING).description("카테고리 제목")
                )
            )
        )
    }

    @Test
    fun hideCommunity() {

        // given
        val community: Community = saveCommunity()
        val request = BooleanDto(true)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                patch("/admin/community/{community_id}/hide", community.id)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_hide_community",
                pathParameters(
                    parameterWithName("community_id").description("글 ID")
                ),
                requestFields(
                    fieldWithPath("bool").type(BOOLEAN).description("숨김 처리 여부")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("커뮤니티 ID")
                )
            )
        )
    }

    @Test
    fun winCommunity() {

        val community: Community = saveCommunity(categoryType = DRIP)
        val request = BooleanDto(true)

        val result: ResultActions = mockMvc
            .perform(
                patch("/admin/community/{community_id}/win", community.id)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_win_community",
                pathParameters(
                    parameterWithName("community_id").description("글 ID")
                ),
                requestFields(
                    fieldWithPath("bool").type(BOOLEAN).description("당첨 여부")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("커뮤니티 ID")
                )
            )
        )
    }

    @Test
    fun fixCommunity() {

        val community: Community = saveCommunity()
        val request = BooleanDto(true)

        val result: ResultActions = mockMvc
            .perform(
                patch("/admin/community/{community_id}/fix", community.id)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_fix_community",
                pathParameters(
                    parameterWithName("community_id").description("글 ID")
                ),
                requestFields(
                    fieldWithPath("bool").type(BOOLEAN).description("상단 고정 여부")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("커뮤니티 ID")
                )
            )
        )
    }

    fun saveCommunity(
        categoryType: CommunityCategoryType = NORMAL,
        member: Member? = null
    ): Community {
        val communityCategory: CommunityCategory = saveCommunityCategory(categoryType)
        return communityRepository.save(makeCommunity(category = communityCategory, member = member));
    }

    fun saveCommunityCategory(categoryType: CommunityCategoryType): CommunityCategory {
        return communityCategoryRepository.save(makeCommunityCategory(type = categoryType))
    }

    fun saveAdmin(): Member {
        return memberRepository.save(makeMember(link = 0))
    }
}
