package com.jocoos.mybeautip.domain.community.api.admin

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.NORMAL
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCategoryRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.testutil.fixture.makeCommunityCategory
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.JsonFieldType.NUMBER
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AdminCommunityCategoryControllerTest(
    private val communityCategoryRepository: CommunityCategoryRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun getAdminCategories() {

        // given
        communityCategoryRepository.save(makeCommunityCategory(type = NORMAL))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/admin/community/category")
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_community_status",
                responseFields(
                    fieldWithPath("[].id").type(NUMBER).description("커뮤니티 카테고리 ID"),
                    fieldWithPath("[].title").type(STRING).description("커뮤니티 카테고리 이름"),
                    fieldWithPath("[].type").type(STRING).description(generateLinkCode(COMMUNITY_CATEGORY_TYPE))
                )
            )
        )
    }
}
