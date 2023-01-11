package com.jocoos.mybeautip.domain.community.api.front

import com.jocoos.mybeautip.domain.community.persistence.domain.Community
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCategoryRepository
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.SCRAP_TYPE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.global.dto.single.BooleanDto
import com.jocoos.mybeautip.testutil.fixture.makeCommunity
import com.jocoos.mybeautip.testutil.fixture.makeCommunityCategory
import com.jocoos.mybeautip.member.Member
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class CommunityScrapControllerTest(
    private val communityRepository: CommunityRepository,
    private val communityCategoryRepository: CommunityCategoryRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun scrapTest() {

        // given
        val community: Community = saveCommunity()
        val request = BooleanDto(true)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                patch("/api/1/community/{community_id}/scrap", community.id)
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "scrap_community",
                pathParameters(
                    parameterWithName("community_id").description("글 ID")
                ),
                requestFields(
                    fieldWithPath("bool").type(BOOLEAN).description("스크랩 여부")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("스크랩 ID"),
                    fieldWithPath("type").type(STRING).description(generateLinkCode(SCRAP_TYPE)),
                    fieldWithPath("community_id").type(NUMBER).description("커뮤니티 ID"),
                    fieldWithPath("is_scrap").type(BOOLEAN).description("스크랩 여부"),
                    fieldWithPath("created_at").type(STRING).description("생성일자")
                )
            )
        )
    }

    fun saveCommunity(
        category: CommunityCategory? = null,
        member: Member? = null
    ): Community {
        communityCategoryRepository.save(makeCommunityCategory())
        return communityRepository.save(makeCommunity(category = category, member = member));
    }
}
