package com.jocoos.mybeautip.domain.broadcast.api.front

import com.jocoos.mybeautip.domain.broadcast.BroadcastTestSupport
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.CANCEL
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastCreateRequest
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastEditRequest
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastPinMessageRequest
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastStatusRequest
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastPinMessageRepository
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastRepository
import com.jocoos.mybeautip.domain.community.dto.ReportRequest
import com.jocoos.mybeautip.domain.file.code.FileType.IMAGE
import com.jocoos.mybeautip.global.code.FileOperationType.UPLOAD
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.global.dto.FileDto
import com.jocoos.mybeautip.global.dto.single.BooleanDto
import com.jocoos.mybeautip.testutil.fixture.makeBroadcast
import com.jocoos.mybeautip.testutil.fixture.makePinMessage
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.ZonedDateTime.now


@TestInstance(PER_CLASS)
class BroadcastMessageControllerTest(
    private val broadcastRepository: BroadcastRepository,
    private val broadcastPinMessageRepository: BroadcastPinMessageRepository
) : BroadcastTestSupport() {

    private lateinit var broadcast: Broadcast

    @BeforeAll
    fun beforeAll() {
        broadcast = broadcastRepository.save(makeBroadcast(defaultBroadcastCategory, defaultInfluencer.id))
    }

    @AfterAll
    fun afterAll() {
        broadcastRepository.delete(broadcast);
    }

    @Test
    fun `Broadcast Change Message Room Status API`() {
        val request = BooleanDto(false)

        val result: ResultActions = mockMvc
            .perform(
                patch("/api/1/broadcast/{broadcast_id}/message-room/status", broadcast.id)
                    .header(AUTHORIZATION, defaultInfluencerToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "change_broadcast_message_room_status",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 ID")
                ),
                requestFields(
                    fieldWithPath("bool").type(BOOLEAN).description("채팅룸 채팅 가능 여부")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("방송 아이디"),
                    fieldWithPath("can_chat").type(BOOLEAN).description("채팅룸 채팅 가능 여부")
                )
            )
        )
    }

    @Test
    fun `Broadcast Pin Message API`() {
        val request = BroadcastPinMessageRequest(1L,3L, "message", "avatarUrl", "username", true)

        val result: ResultActions = mockMvc
            .perform(
                patch("/api/1/broadcast/{broadcast_id}/pin-message", broadcast.id)
                    .header(AUTHORIZATION, defaultInfluencerToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "pin_broadcast_message",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 ID")
                ),
                requestFields(
                    fieldWithPath("message_id").type(NUMBER).description("메세지 ID"),
                    fieldWithPath("message").type(STRING).description("메세지 내용"),
                    fieldWithPath("member_id").type(NUMBER).description("회원 ID"),
                    fieldWithPath("username").type(STRING).description("회원 닉네임"),
                    fieldWithPath("avatar_url").type(STRING).description("회원 아바타 URL"),
                    fieldWithPath("is_pin").type(BOOLEAN).description("메세지 고정 여부, false 일 경우 위 파라미터 전부 옵셔널"),
                ),
                responseFields(
                    fieldWithPath("message_id").type(NUMBER).description("메세지 ID"),
                    fieldWithPath("message").type(STRING).description("메세지 내용"),
                    fieldWithPath("created_by").type(OBJECT).description("작성자 정보"),
                    fieldWithPath("created_by.id").type(NUMBER).description("작성자 ID"),
                    fieldWithPath("created_by.username").type(STRING).description("작성자 닉네임"),
                    fieldWithPath("created_by.avatar_url").type(STRING).description("작성자 아바타 URL"),
                    fieldWithPath("is_pin").type(BOOLEAN).description("메세지 고정 여부")
                    )
            )
        )
    }

    @Test
    fun `Broadcast No Pin Message API`() {

        val broadcastEntity = broadcastRepository.save(makeBroadcast(defaultBroadcastCategory))
        broadcastPinMessageRepository.save(makePinMessage(broadcastEntity))
        val request = BroadcastPinMessageRequest(null, null, null, null, null, false)

        val result: ResultActions = mockMvc
            .perform(
                patch("/api/1/broadcast/{broadcast_id}/pin-message", broadcastEntity.id)
                    .header(AUTHORIZATION, defaultInfluencerToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "no_pin_broadcast_message",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 ID")
                ),
                requestFields(
                    fieldWithPath("is_pin").type(BOOLEAN).description("메세지 고정 여부, false"),
                ),
                responseFields(
                    fieldWithPath("is_pin").type(BOOLEAN).description("메세지 고정 여부, false")
                )
            )
        )
    }
}
