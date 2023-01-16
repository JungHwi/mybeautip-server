package com.jocoos.mybeautip.domain.event.api.admin

import com.jocoos.mybeautip.domain.event.persistence.domain.Event
import com.jocoos.mybeautip.domain.event.persistence.repository.EventRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.dto.SortOrderDto
import com.jocoos.mybeautip.global.dto.single.BooleanDto
import com.jocoos.mybeautip.testutil.fixture.makeEvent
import com.jocoos.mybeautip.testutil.fixture.makeEvents
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

class AdminEventSortOrderControllerTest(
    private val eventRepository: EventRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun fixEvent() {

        val event: Event = eventRepository.save(makeEvent());
        val request = BooleanDto(true)

        val result: ResultActions = mockMvc
            .perform(
                patch("/admin/event/{event_id}/fix", event.id)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_fix_event",
                pathParameters(
                    parameterWithName("event_id").description("이벤트 ID")
                ),
                requestFields(
                    fieldWithPath("bool").type(BOOLEAN).description("상단 고정 여부")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("이벤트 ID")
                )
            )
        )
    }

    @Test
    fun changeOrderOfEvent() {

        // given
        val events: List<Event> = eventRepository.saveAll(makeEvents(3))
        val ids: List<Long> = events.map { event: Event -> event.id }.shuffled();
        val request = SortOrderDto(ids)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                patch("/admin/event/order")
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_change_order_event",
                requestFields(
                    fieldWithPath("sorted_ids").type(ARRAY).description("정렬된 이벤트 ID 리스트")
                ),
                responseFields(
                    fieldWithPath("sorted_ids").type(ARRAY).description("정렬된 이벤트 ID 리스트")
                )
            )
        )
    }
}
