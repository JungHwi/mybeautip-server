package com.jocoos.mybeautip.domain.member.api.front

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastCategoryRepository
import com.jocoos.mybeautip.domain.community.persistence.domain.Community
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCategoryRepository
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityRepository
import com.jocoos.mybeautip.domain.scrap.persistence.repository.ScrapRepository
import com.jocoos.mybeautip.domain.vod.persistence.repository.VodRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.testutil.fixture.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.requestParameters
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ScrapControllerTest(
    private val communityRepository: CommunityRepository,
    private val scrapRepository: ScrapRepository,
    private val communityCategoryRepository: CommunityCategoryRepository,
    private val broadcastCategoryRepository: BroadcastCategoryRepository,
    private val vodRepository: VodRepository
    ) : RestDocsIntegrationTestSupport() {

    @Test
    fun getCommunityScrap() {

        // given
        val communityCategory: CommunityCategory = communityCategoryRepository.save(makeCommunityCategory())
        val community: Community = communityRepository.save(makeCommunity(category = communityCategory, member = defaultAdmin))
        scrapRepository.save(makeCommunityScrap(requestUser.id, community.id))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/api/2/my/scrap")
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_community_scraps",
                requestParameters(
                    parameterWithName("cursor").description("커서").optional(),
                    parameterWithName("size").description("조회 갯수").optional()
                        .attributes(getDefault(20))
                ),
                responseFields(
                    fieldWithPath("next_cursor").type(STRING).description("커서 정보").attributes(getZonedDateMilliFormat())
                        .optional(),
                    fieldWithPath("content").type(ARRAY).description("커뮤니티 스크랩 목록").optional(),
                    fieldWithPath("content.[].scrap_id").type(NUMBER).description("스크랩 ID"),
                    fieldWithPath("content.[].type").type(STRING).description(generateLinkCode(SCRAP_TYPE)),
                    fieldWithPath("content.[].id").type(NUMBER).description("커뮤니티 ID"),
                    fieldWithPath("content.[].type").type(STRING).description("스크랩 타"),
                    fieldWithPath("content.[].scrap_id").type(NUMBER).description("스크랩 ID"),
                    fieldWithPath("content.[].is_win").type(BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                    fieldWithPath("content.[].status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("content.[].title").type(STRING).description("제목").optional(),
                    fieldWithPath("content.[].contents").type(STRING).description("내용").optional(),
                    fieldWithPath("content.[].files").type(ARRAY).description("파일 List").optional(),
                    fieldWithPath("content.[].files.[].type").type(STRING).description(generateLinkCode(FILE_TYPE)),
                    fieldWithPath("content.[].files.[].thumbnail_url").type(STRING).description("파일 썸네일 URL")
                        .optional(),
                    fieldWithPath("content.[].files.[].url").type(STRING).description("파일 URL"),
                    fieldWithPath("content.[].files.[].duration").type(NUMBER).description("비디오 길이 ms").optional(),
                    fieldWithPath("content.[].votes").type(ARRAY).description("투표 URL").optional(),
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
    fun getVodScrap() {

        // given
        val category = broadcastCategoryRepository.save(makeBroadcastCategory(parentId = groupBroadcastCategory.id))
        val vod = vodRepository.save(makeVod(category = category, memberId = defaultInfluencer.id))
        scrapRepository.save(makeVodScrap(requestUser.id, vod.id))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/api/2/my/scrap")
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
                    .param("type", "VOD")
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_vod_scraps",
                requestParameters(
                    parameterWithName("type").description(generateLinkCode(SCRAP_TYPE)).attributes(getDefault("COMMUNITY")).optional(),
                    parameterWithName("cursor").description("커서").optional(),
                    parameterWithName("size").description("조회 갯수").optional()
                        .attributes(getDefault(20))
                ),
                responseFields(
                    fieldWithPath("next_cursor").type(STRING).description("커서 정보").attributes(getZonedDateMilliFormat())
                        .optional(),
                    fieldWithPath("content").type(ARRAY).description("방송 목록"),
                    fieldWithPath("content.[].scrap_id").type(NUMBER).description("스크랩 ID"),
                    fieldWithPath("content.[].type").type(STRING).description(generateLinkCode(SCRAP_TYPE)),
                    fieldWithPath("content.[].id").type(NUMBER).description("VOD 아이디"),
                    fieldWithPath("content.[].video_key").type(NUMBER).description("Flip Flop Lite 비디오 아이디"),
                    fieldWithPath("content.[].url").type(STRING).description("VOD URL"),
                    fieldWithPath("content.[].title").type(STRING).description("타이틀"),
                    fieldWithPath("content.[].thumbnail_url").type(STRING).description("썸네일 URL"),
                    fieldWithPath("content.[].view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("content.[].heart_count").type(NUMBER).description("하트수"),
                    fieldWithPath("content.[].category").type(OBJECT).description("카테고리 정보"),
                    fieldWithPath("content.[].category.id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("content.[].category.title").type(STRING).description("카테고리 타이틀"),
                    fieldWithPath("content.[].member").type(OBJECT).description("회원 정보"),
                    fieldWithPath("content.[].member.id").type(NUMBER).description("회원 아이디"),
                    fieldWithPath("content.[].member.email").type(STRING).description("회원 이메일").optional(),
                    fieldWithPath("content.[].member.username").type(STRING).description("회원 닉네임"),
                    fieldWithPath("content.[].member.avatar_url").type(STRING).description("회원 아바타 URL")
                )
            )
        )
    }

    @Test
    fun scrapExist() {
        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/my/scrap/exist")
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "scrap_exist_check",
                responseFields(
                    fieldWithPath("bool").type(BOOLEAN).description("스크랩 존재 여부")
                )
            )
        )
    }
}
