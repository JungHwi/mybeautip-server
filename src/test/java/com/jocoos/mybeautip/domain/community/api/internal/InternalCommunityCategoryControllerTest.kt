package com.jocoos.mybeautip.domain.community.api.internal

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.GENERAL
import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.NORMAL
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCategoryRepository
import com.jocoos.mybeautip.domain.video.api.internal.InternalVideoControllerTest
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.member.Member
import com.jocoos.mybeautip.member.MemberRepository
import com.jocoos.mybeautip.testutil.fixture.makeCommunityCategory
import com.jocoos.mybeautip.testutil.fixture.makeMember
import com.jocoos.mybeautip.testutil.fixture.makeVideoCategory
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InternalCommunityCategoryControllerTest(
    private val communityCategoryRepository: CommunityCategoryRepository,
    private val memberRepository: MemberRepository
) : RestDocsIntegrationTestSupport() {

    private lateinit var member: Member

    companion object {
        const val MEMBER_ID = "MEMBER-ID"
    }

    @BeforeAll
    fun beforeAll() {
        member = memberRepository.save(makeMember())
    }

    @AfterAll
    fun afterAll() {
        memberRepository.delete(member)
    }

    @Test
    fun getCommunityCategories() {

        // given
        val generalCategory: CommunityCategory = communityCategoryRepository.save(makeCommunityCategory(type = GENERAL))
        communityCategoryRepository.save(makeCommunityCategory(type = NORMAL, parentId = generalCategory.id))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/internal/1/community/category")
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, member.id)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_get_community_categories",
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
                    fieldWithPath("[].hint").type(STRING).description("힌트").optional()
                )
            )
        )
    }
}
