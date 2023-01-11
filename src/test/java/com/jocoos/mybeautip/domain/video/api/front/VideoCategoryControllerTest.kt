package com.jocoos.mybeautip.domain.video.api.front

import com.jocoos.mybeautip.domain.video.persistence.repository.VideoCategoryRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.VIDEO_CATEGORY_TYPE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.VIDEO_MASK_TYPE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.testutil.fixture.makeVideoCategory
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class VideoCategoryControllerTest(
    private val videoCategoryRepository: VideoCategoryRepository
) : RestDocsIntegrationTestSupport() {
    @Test
    fun getVideoCategories() {

        // given
        videoCategoryRepository.save(makeVideoCategory())

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/video/category")
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)

        result.andDo(
            document(
                "get_video_categories",
                responseFields(
                    fieldWithPath("[]").type(ARRAY).description("비디오 카테고리 목록"),
                    fieldWithPath("[].id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("[].type").type(STRING).description("카테고리 구분")
                        .description(generateLinkCode(VIDEO_CATEGORY_TYPE)),
                    fieldWithPath("[].title").type(STRING).description("제목"),
                    fieldWithPath("[].shape_url").type(STRING).description("Shape URL").optional(),
                    fieldWithPath("[].mask_type").type(STRING).description(generateLinkCode(VIDEO_MASK_TYPE)).optional()
                )
            )
        )
    }
}
