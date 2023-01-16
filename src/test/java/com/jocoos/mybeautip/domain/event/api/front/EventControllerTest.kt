package com.jocoos.mybeautip.domain.event.api.front

import com.jocoos.mybeautip.domain.event.persistence.domain.Event
import com.jocoos.mybeautip.domain.event.persistence.repository.EventRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.EVENT_STATUS
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.EVENT_TYPE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.testutil.fixture.makeEvent
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

class EventControllerTest(
    private val eventRepository: EventRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun getEventList() {
        eventRepository.save(makeEvent())

        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/event")
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_events",
                requestParameters(
                    parameterWithName("event_type").description("이벤트 타입").optional()
                ),
                responseFields(
                    fieldWithPath("[]").type(ARRAY).description("이벤트 목록"),
                    fieldWithPath("[].id").type(NUMBER).description("이벤트 ID"),
                    fieldWithPath("[].type").type(STRING).description(generateLinkCode(EVENT_TYPE)),
                    fieldWithPath("[].status").type(STRING).description(generateLinkCode(EVENT_STATUS)),
                    fieldWithPath("[].title").type(STRING).description("제목"),
                    fieldWithPath("[].thumbnail_image_url").type(STRING).description("썸네일 이미지 URL"),
                    fieldWithPath("[].banner_image_url").type(STRING).description("배너 이미지 URL").optional(),
                    fieldWithPath("[].start_at").type(STRING).description("이벤트 시작일시").attributes(getZonedDateFormat()),
                    fieldWithPath("[].end_at").type(STRING).description("이벤트 종료일시").attributes(getZonedDateFormat())
                        .optional()
                )
            )
        )
    }

    @Test
    fun getEvent() {

        val event: Event = eventRepository.save(makeEvent())

        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/event/{event_id}", event.id)
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_event",
                pathParameters(
                    parameterWithName("event_id").description("이벤트 ID")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("이벤트 ID"),
                    fieldWithPath("type").type(STRING).description(generateLinkCode(EVENT_TYPE)),
                    fieldWithPath("relation_id").type(NUMBER).description("관련된 아이디. 현재는 Community Category 의 ID 밖에 없음.").optional(),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(EVENT_STATUS)),
                    fieldWithPath("title").type(STRING).description("제목"),
                    fieldWithPath("description").type(STRING).description("설명").optional(),
                    fieldWithPath("image_url").type(STRING).description("메인 이미지 URL"),
                    fieldWithPath("banner_image_url").type(STRING).description("배너 이미지").optional(),
                    fieldWithPath("share_square_image_url").type(STRING).description("배너 이미지").optional(),
                    fieldWithPath("share_rectangle_image_url").type(STRING).description("배너 이미지").optional(),
                    fieldWithPath("need_point").type(NUMBER).description("이벤트 참가시 필요한 포인트"),
                    fieldWithPath("start_at").type(STRING).description("이벤트 시작일시").attributes(getZonedDateFormat()),
                    fieldWithPath("end_at").type(STRING).description("이벤트 종료일시").attributes(getZonedDateFormat()),
                    fieldWithPath("event_product_list").type(ARRAY).description("이벤트 상품 목록").optional(),
                    fieldWithPath("event_product_list.[].id").type(NUMBER).description("이벤트 상품 아이디"),
                    fieldWithPath("event_product_list.[].type").type(STRING).description("이벤트 상품 구분"),
                    fieldWithPath("event_product_list.[].name").type(STRING).description("이벤트 상품명"),
                    fieldWithPath("event_product_list.[].image_url").type(STRING).description("이벤트 상품 이미지 URL").optional()
                )
            )
        )
    }
}
