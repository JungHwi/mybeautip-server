package com.jocoos.mybeautip.domain.broadcast.api.front

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastCategoryType
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastCategoryType.*
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastCategoryRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator
import com.jocoos.mybeautip.testutil.fixture.makeBroadcastCategory
import io.restassured.module.kotlin.extensions.Given
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.JsonFieldType.NUMBER
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class BroadcastCategoryControllerTest(
    private val broadcastCategoryRepository: BroadcastCategoryRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun `Broadcast,VOD 카테고리 목록 조회`() {

        broadcastCategoryRepository.saveAll(
            listOf(
                makeBroadcastCategory(groupBroadcastCategory.id, CHAT, title = "떠들어보아요"),
                makeBroadcastCategory(groupBroadcastCategory.id, SHARE_TIP, title = "꿀팁 공유"),
                makeBroadcastCategory(groupBroadcastCategory.id, REVIEW, title = "솔직 리뷰")
            )
        )

        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/broadcast/category")
                    .header(AUTHORIZATION, requestUserToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_broadcast_categories",
                responseFields(
                    fieldWithPath("[].id").type(NUMBER).description("방송/VOD 카테고리 ID"),
                    fieldWithPath("[].title").type(STRING).description("방송/VOD 카테고리 이름")
                )
            )
        )
    }

}
