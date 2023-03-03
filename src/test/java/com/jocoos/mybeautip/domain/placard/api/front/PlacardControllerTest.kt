package com.jocoos.mybeautip.domain.placard.api.front

import com.jocoos.mybeautip.testutil.fixture.PlacardFixture.makePlacard
import com.jocoos.mybeautip.domain.placard.persistence.repository.PlacardRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.PLACARD_LINK_TYPE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class PlacardControllerTest(
    private val placardRepository: PlacardRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun getActivePlacards() {

        placardRepository.save(makePlacard())

        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/placard")
                    .header(AUTHORIZATION, requestUserToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_placards",
                responseFields(
                    fieldWithPath("[]").type(ARRAY).description("플랜카드 목록"),
                    fieldWithPath("[].title").type(STRING).description("플랜카드 타이틀"),
                    fieldWithPath("[].image_url").type(STRING).description("플랜카드 이미지 URL"),
                    fieldWithPath("[].color").type(STRING).description("플랜카드 색깔 정보"),
                    fieldWithPath("[].placard_link").type(OBJECT).description("플랜카드 클릭 시 연결 정보"),
                    fieldWithPath("[].placard_link.link_type").type(STRING)
                        .description(generateLinkCode(PLACARD_LINK_TYPE)),
                    fieldWithPath("[].placard_link.parameter").type(STRING)
                        .description("연결 정보 파라미터 (이벤트나 비디오 ID, null일 경우 해당 탭으로").optional()
                )
            )
        )
    }
}
