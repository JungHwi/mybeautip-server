package com.jocoos.mybeautip.domain.event.api.front

import com.jocoos.mybeautip.domain.event.code.EventType.JOIN
import com.jocoos.mybeautip.domain.event.persistence.domain.Event
import com.jocoos.mybeautip.domain.event.persistence.repository.EventRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.testutil.fixture.makeEvent
import com.jocoos.mybeautip.member.address.AddressRepository
import com.jocoos.mybeautip.restapi.AddressController.CreateAddressRequest
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class EventJoinControllerTest(
    private val eventRepository: EventRepository,
    private val addressRepository: AddressRepository,
    @LocalServerPort private val port: Int
) : RestDocsIntegrationTestSupport() {

    @AfterEach
    fun afterAll() {
        addressRepository.deleteAll()
    }

    @Test
    fun joinEvent() {

        // given
        makeAddress()
        val event: Event = eventRepository.save(makeEvent(type = JOIN))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                post("/api/1/event/join/{eventId}", event.id)
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "event_join",
                pathParameters(
                    parameterWithName("eventId").description("Event ID")
                ),
                responseFields(
                    fieldWithPath("result").description("이벤트 참여 결과")
                )
            )
        )
    }

    private fun makeAddress() {
        val request = CreateAddressRequest(
            true, "title", "recipent", "phone", "zipNo",
            "roadAddrPart1", "roadAddrPart2", "jibunAddr", "detailAddr"
        )

        Given {
            port(port)
            header(AUTHORIZATION, requestUserToken)
            contentType(APPLICATION_JSON_VALUE)
            body(request)
        } When {
            post("/api/1/members/me/addresses")
        } Then {
            status().isOk
        }
    }
}
