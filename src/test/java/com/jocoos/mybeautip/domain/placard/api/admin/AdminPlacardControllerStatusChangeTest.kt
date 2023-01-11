package com.jocoos.mybeautip.domain.placard.api.admin

import com.jocoos.mybeautip.testutil.fixture.PlacardFixture.makePlacards
import com.jocoos.mybeautip.testutil.fixture.PlacardFixture.makePlacard
import com.jocoos.mybeautip.domain.placard.persistence.domain.Placard
import com.jocoos.mybeautip.domain.placard.persistence.repository.PlacardRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.dto.single.BooleanDto
import com.jocoos.mybeautip.global.dto.single.SortOrderDto
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AdminPlacardControllerStatusChangeTest(
    private val placardRepository: PlacardRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun fixPlacard() {

        // given
        val placard: Placard = placardRepository.save(makePlacard());
        val request = BooleanDto(true)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                patch("/admin/placard/{placard_id}/fix", placard.id)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_fix_placard",
                pathParameters(
                    parameterWithName("placard_id").description("플랜카드 ID")
                ),
                requestFields(
                    fieldWithPath("bool").type(BOOLEAN).description("상단 고정 여부")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("플랜카드 ID")
                )
            )
        )
    }

    @Test
    fun changeStatusOfPlacard() {

        val placard: Placard = placardRepository.save(makePlacard());
        val request = BooleanDto(true)

        val result: ResultActions = mockMvc.perform(
            patch("/admin/placard/{placard_id}/status", placard.id)
                .header(AUTHORIZATION, defaultAdminToken)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_change_status_placard",
                pathParameters(
                    parameterWithName("placard_id").description("플랜카드 ID")
                ),
                requestFields(
                    fieldWithPath("bool").type(BOOLEAN).description("공개 여부")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("플랜카드 ID")
                )
            )
        )
    }

    @Test
    fun changeOrderOfPlacards() {

        // given
        val placards: List<Placard> = placardRepository.saveAll(makePlacards(3))

        val ids = placards.map { placard: Placard -> placard.id }
            .shuffled()
            .toList();

        val request = SortOrderDto(ids)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                patch("/admin/placard/order")
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_change_order_placard",
                requestFields(
                    fieldWithPath("sorted_ids").type(ARRAY).description("정렬된 플랜카드 ID 리스트")
                ),
                responseFields(
                    fieldWithPath("sorted_ids").type(ARRAY).description("정렬된 플랜카드 ID 리스트")
                )
            )
        )
    }
}
