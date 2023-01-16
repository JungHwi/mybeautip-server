package com.jocoos.mybeautip.domain.video.api.admin

import com.jocoos.mybeautip.domain.video.persistence.repository.VideoCategoryRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.VIDEO_CATEGORY_TYPE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.testutil.fixture.makeVideoCategory
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

class AdminVideoCategoryControllerTest(
    private val videoCategoryRepository: VideoCategoryRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun getCategories() {

        videoCategoryRepository.save(makeVideoCategory())

        val result: ResultActions = mockMvc
            .perform(
            get("/admin/video/category")
                .header(AUTHORIZATION, defaultAdminToken)
        )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_video_category",
                responseFields(
                    fieldWithPath("[].id").type(NUMBER).description("비디오 카테고리 ID"),
                    fieldWithPath("[].type").type(STRING).description(generateLinkCode(VIDEO_CATEGORY_TYPE)),
                    fieldWithPath("[].title").type(STRING).description("비디오 카테고리 이름")
                )
            )
        )
    }
}
