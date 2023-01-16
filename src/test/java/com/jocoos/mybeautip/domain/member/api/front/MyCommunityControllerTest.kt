package com.jocoos.mybeautip.domain.member.api.front

import com.jocoos.mybeautip.domain.community.persistence.domain.Community
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCategoryRepository
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCommentRepository
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.global.constant.MybeautipConstant.MAX_LONG_STRING
import com.jocoos.mybeautip.testutil.fixture.makeCommunity
import com.jocoos.mybeautip.testutil.fixture.makeCommunityCategory
import com.jocoos.mybeautip.testutil.fixture.makeCommunityComment
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

class MyCommunityControllerTest(
    private val communityRepository: CommunityRepository,
    private val communityCategoryRepository: CommunityCategoryRepository,
    private val communityCommentRepository: CommunityCommentRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun getMyCommunities() {

        // given
        val communityCategory: CommunityCategory = communityCategoryRepository.save(makeCommunityCategory())
        communityRepository.save(makeCommunity(member = requestUser, category = communityCategory));

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/api/2/my/community")
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_my_communities",
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
                    fieldWithPath("content.[].file").type(OBJECT).description("메인 파일").optional(),
                    fieldWithPath("content.[].file.type").type(STRING).description(generateLinkCode(FILE_TYPE)),
                    fieldWithPath("content.[].file.thumbnail_url").type(STRING).description("메인 파일 썸네일 URL").optional(),
                    fieldWithPath("content.[].file.url").type(STRING).description("메인 파일 URL").optional(),
                    fieldWithPath("content.[].comment_count").type(NUMBER).description("댓글/대댓글수"),
                    fieldWithPath("content.[].report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("content.[].created_at").type(STRING).description("작성일")
                        .attributes(getZonedDateFormat()),
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
    fun getMyCommunityComments() {

        // given
        val communityCategory: CommunityCategory = communityCategoryRepository.save(makeCommunityCategory())
        val community: Community =
            communityRepository.save(makeCommunity(member = defaultAdmin, category = communityCategory));
        communityCommentRepository.save(makeCommunityComment(member = requestUser, community = community))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/my/community/comment")
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_my_community_comments",
                requestParameters(
                    parameterWithName("cursor").description("커서").optional().attributes(getDefault(MAX_LONG_STRING)),
                    parameterWithName("size").description("페이지 사이즈").optional().attributes(getDefault(20))
                ),
                responseFields(
                    fieldWithPath("next_cursor").type(STRING).description("커서 정보"),
                    fieldWithPath("content").type(ARRAY).description("커뮤니티 댓글 목록"),
                    fieldWithPath("content.[].id").type(NUMBER).description("댓글 아이디"),
                    fieldWithPath("content.[].category_id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("content.[].community_id").type(NUMBER).description("커뮤니티 아이디"),
                    fieldWithPath("content.[].parent_id").type(NUMBER).description("부모 댓글 아이디").optional(),
                    fieldWithPath("content.[].status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("content.[].contents").type(STRING).description("내용"),
                    fieldWithPath("content.[].file_url").type(STRING).description("이미지 URL").optional(),
                    fieldWithPath("content.[].report_count").type(NUMBER).description("대댓글수"),
                    fieldWithPath("content.[].created_at").type(STRING).description("작성일")
                        .attributes(getZonedDateFormat())
                )
            )
        )
    }
}
