package com.jocoos.mybeautip.domain.community.api.front

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.GENERAL
import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.NORMAL
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCategoryRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.testutil.fixture.makeCommunityCategory
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


class CommunityCategoryControllerTest(
    private val communityCategoryRepository: CommunityCategoryRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun getCommunityCategories() {

        // given
        val generalCategory: CommunityCategory = communityCategoryRepository.save(makeCommunityCategory(type = GENERAL))
        communityCategoryRepository.save(makeCommunityCategory(type = NORMAL, parentId = generalCategory.id))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/community/category")
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_community_categories",
                requestParameters(
                    parameterWithName("type").optional().attributes(getDefault(GENERAL)).description(
                        generateLinkCode(COMMUNITY_CATEGORY_TYPE) + "+ \n" +
                                "일반 게시판의 경우에는 [GENERAL] + \n" +
                                "익명 게시판의 경우에는 [ANONYMOUS]"
                    )
                ),
                responseFields(
                    fieldWithPath("[]").type(ARRAY).description("커뮤니티 카테고리 목록"),
                    fieldWithPath("[].id").type(NUMBER).description("커뮤니티 아이디"),
                    fieldWithPath("[].type").type(STRING).description("카테고리 구분")
                        .description(generateLinkCode(COMMUNITY_CATEGORY_TYPE)),
                    fieldWithPath("[].title").type(STRING).description("제목"),
                    fieldWithPath("[].hint").type(STRING).description("힌트")
                )
            )
        )
    }

    @Test
    fun getCommunityCategory() {

        // given
        val communityCategory: CommunityCategory =
            communityCategoryRepository.save(makeCommunityCategory(type = NORMAL))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/community/category/{category_id}", communityCategory.id)
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_community_category",
                pathParameters(
                    parameterWithName("category_id").description("카테고리 ID")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("커뮤니티 아이디"),
                    fieldWithPath("type").type(STRING).description("카테고리 구분")
                        .description(generateLinkCode(COMMUNITY_CATEGORY_TYPE)),
                    fieldWithPath("title").type(STRING).description("제목"),
                    fieldWithPath("hint").type(STRING).description("힌트")
                )
            )
        )
    }
}
