package com.jocoos.mybeautip.domain.broadcast.api.front

import com.jocoos.mybeautip.domain.broadcast.*
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastReportType
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.CANCEL
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastCreateRequest
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastEditRequest
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastReportRequest
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastStatusRequest
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastRepository
import com.jocoos.mybeautip.domain.file.code.FileType.IMAGE
import com.jocoos.mybeautip.domain.vod.persistence.repository.VodRepository
import com.jocoos.mybeautip.global.code.FileOperationType.UPLOAD
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.global.config.restdoc.util.ExceptionFieldsSnippet
import com.jocoos.mybeautip.global.config.restdoc.util.ExceptionFieldsSnippet.exceptionConvertFieldDescriptor
import com.jocoos.mybeautip.global.dto.FileDto
import com.jocoos.mybeautip.global.dto.single.BooleanDto
import com.jocoos.mybeautip.global.dto.single.IntegerDto
import com.jocoos.mybeautip.global.exception.ErrorCode
import com.jocoos.mybeautip.global.exception.ErrorCode.ACCESS_DENIED
import com.jocoos.mybeautip.global.exception.ErrorCode.NOT_FOUND
import com.jocoos.mybeautip.testutil.fixture.makeBroadcast
import com.jocoos.mybeautip.testutil.fixture.makeVod
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
class BroadcastControllerTest(
    private val broadcastRepository: BroadcastRepository,
    private val vodRepository: VodRepository
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
    fun `Broadcast 생성 API`() {
        val request =
            BroadcastCreateRequest(
                "title",
                FileDto(UPLOAD, "thumbnailUrl"),
                defaultBroadcastCategory.id,
                false,
                "notice",
                now().plusDays(10)
            )

        val result: ResultActions = mockMvc
            .perform(
                post("/api/1/broadcast")
                    .header(AUTHORIZATION, defaultInfluencerToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "create_broadcast",
                requestFields(
                    fieldWithPath("title").type(STRING).description("타이틀. 최대 25자"),
                    fieldWithPath("thumbnail").type(OBJECT).description("썸네일"),
                    fieldWithPath("thumbnail.operation").type(STRING)
                        .description(generateLinkCode(FILE_OPERATION_TYPE) + " UPLOAD 요쳥하시면 됩니다"),
                    fieldWithPath("thumbnail.url").type(STRING).description("썸네일 URL"),
                    fieldWithPath("thumbnail.type").type(ARRAY).attributes(getDefault(IMAGE)).description(FILE_TYPE)
                        .ignored(),
                    fieldWithPath("thumbnail.need_transcode").type(BOOLEAN).ignored(),
                    fieldWithPath("category_id").type(NUMBER).description("방송 카테고리 ID"),
                    fieldWithPath("is_start_now").type(BOOLEAN).description("바로 시작 여부 Boolean"),
                    fieldWithPath("started_at").type(STRING).description("예약 시간, 바로 시작 true일 때 null 가능")
                        .attributes(getZonedDateFormat()).optional(),
                    fieldWithPath("notice").type(STRING).description("공지사항. 최대 100자").optional()
                ),
                responseFields(broadcastResponseWithCreatedBy())
            )
        )
    }

    @Test
    fun `Broadcast 목록 조회 API`() {
        saveTwoBroadcast()

        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/broadcast")
                    .header(AUTHORIZATION, requestUserToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_broadcast_list",
                requestParameters(
                    parameterWithName("cursor").description("커서. 방송 아이디").optional(),
                    parameterWithName("status").description(generateLinkCode(BROADCAST_STATUS)).optional(),
                    parameterWithName("start_date").description("방송 시작일 (라이브 캘린더 UI 용)")
                        .attributes(getLocalDateFormat()).optional(),
                    parameterWithName("size").description("페이지 사이즈").attributes(getDefault(5)).optional()
                ),
               responseFields(
                   fieldWithPath("next_cursor").type(STRING).description("다음 커서. 방송 아이디"),
                   fieldWithPath("content").type(ARRAY).description("방송 목록"))
                   .and(broadcastListResponse("content.[]."))
            )
        )
    }

    @Test
    fun `Broadcast 상세 조회 API`() {
        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/broadcast/{broadcast_id}", broadcast.id)
                    .header(AUTHORIZATION, requestUserToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_broadcast",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 ID")
                ),
                responseFields(broadcastResponseWithCreatedByWithPinMessage())
            )
        )
    }

    @Test
    fun getBroadcastStatistics() {
        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/broadcast/{broadcastId}/statistics", broadcast.id)
                    .header(AUTHORIZATION, defaultInfluencerToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_broadcast_statistics",
                pathParameters(
                    parameterWithName("broadcastId").description("방송 ID")
                ),
                responseFields(
                    fieldWithPath("total_viewer_count").type(NUMBER).description("총 시청자 수"),
                    fieldWithPath("max_viewer_count").type(NUMBER).description("동시 접속 최대 시청자 수"),
                    fieldWithPath("viewer_count").type(NUMBER).description("현재 시청자 수"),
                    fieldWithPath("member_viewer_count").type(NUMBER).description("현재 회원 시청자 수"),
                    fieldWithPath("guest_viewer_count").type(NUMBER).description("현재 비회원 시청자 수"),
                    fieldWithPath("report_count").type(NUMBER).description("방송 신고수"),
                    fieldWithPath("heart_count").type(NUMBER).description("방송 하트수"),
                    fieldWithPath("duration").type(NUMBER).description("방송 진행 시간(초). 음수이면 방송 시작 전"),
                )
            )
        )
    }

    @Test
    fun `Broadcast 방송 날짜 목록 조회 API`() {
        saveTwoBroadcast()

        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/broadcast/dates")
                    .header(AUTHORIZATION, requestUserToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_broadcast_date_list",
                requestParameters(
                    parameterWithName("size").description("응답 데이터 사이즈").attributes(getDefault(14)).optional()
                ),
                responseFields(
                    fieldWithPath("dates").type(ARRAY).description("방송 존재 날짜 리스트").attributes(getLocalDateFormat())
                )
            )
        )
    }

    @Test
    fun `Broadcast 수정 API`() {
        val request = BroadcastEditRequest(
            "new title",
            listOf(),
            broadcast.category.id,
            false,
            "new notice",
            true,
            true,
            now().plusDays(3)
        )


        val result: ResultActions = mockMvc
            .perform(
                patch("/api/1/broadcast/{broadcast_id}", broadcast.id)
                    .header(AUTHORIZATION, defaultInfluencerToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "edit_broadcast",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 ID")
                ),
                requestFields(
                    fieldWithPath("title").type(STRING).description("타이틀"),
                    fieldWithPath("thumbnails").type(ARRAY)
                        .description("썸네일 파일 목록. s 붙어있습니다. 다른 필드와 다르게 변경 사항만 요청하시면 됩니다").optional(),
                    fieldWithPath("thumbnails.[].operation").type(STRING)
                        .description(generateLinkCode(FILE_OPERATION_TYPE)),
                    fieldWithPath("thumbnails.[].url").type(STRING).description("이미지 URL"),
                    fieldWithPath("thumbnails.[].type").type(ARRAY).attributes(getDefault(IMAGE)).description(FILE_TYPE)
                        .ignored(),
                    fieldWithPath("thumbnails.[].need_transcode").type(BOOLEAN).ignored(),
                    fieldWithPath("category_id").type(NUMBER).description("카테고리 ID"),
                    fieldWithPath("is_start_now").type(BOOLEAN).description("바로 시작 여부, false 일 경우 started_at 값이 필수입니다").optional(),
                    fieldWithPath("started_at").type(STRING).description("예약 시간, is_start_now false 일 경우 필수입니다").attributes(getZonedDateFormat()),
                    fieldWithPath("is_sound_on").type(BOOLEAN).description("사운드 여부"),
                    fieldWithPath("is_screen_show").type(BOOLEAN).description("화면 표시 여부"),
                    fieldWithPath("notice").type(STRING).description("공지사항").optional()
                ),
                responseFields(broadcastResponse())
            )
        )
    }

    @Test
    fun `Broadcast 상태 변경 API`() {
        val request = BroadcastStatusRequest(CANCEL)


        val result: ResultActions = mockMvc
            .perform(
                patch("/api/1/broadcast/{broadcast_id}/status", broadcast.id)
                    .header(AUTHORIZATION, defaultInfluencerToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "change_status_broadcast",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 ID")
                ),
                requestFields(
                    fieldWithPath("status").type(STRING).description(generateLinkCode(BROADCAST_STATUS) + " LIVE, CANCEL, END 요청 가능")
                ),
                responseFields(broadcastResponse())
            )
        )
    }

    @Test
    fun `Broadcast Set Notify API`() {
        val request = BooleanDto(false)

        val result: ResultActions = mockMvc
            .perform(
                patch("/api/1/broadcast/{broadcast_id}/notification", broadcast.id)
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "set_notify_broadcast",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 ID")
                ),
                requestFields(
                    fieldWithPath("bool").type(BOOLEAN).description("알림 설정 여부")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("방송 아이디"),
                    fieldWithPath("is_notify_needed").type(BOOLEAN).description("알림 설정 여부")
                )
            )
        )
    }

    @Test
    fun `Broadcast 신고 API`() {

        val request = BroadcastReportRequest.builder()
            .type(BroadcastReportType.MESSAGE)
            .reportedId(targetUser.id)
            .reason("신고 사유")
            .description("신고된 대화 내용")
            .build()

        val result: ResultActions = mockMvc
            .perform(
                post("/api/1/broadcast/{broadcast_id}/report", broadcast.id)
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "report_broadcast",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 ID")
                ),
                requestFields(
                    fieldWithPath("type").type(STRING).description(generateLinkCode(BROADCAST_REPORT_TYPE)),
                    fieldWithPath("reason").type(STRING).description("신고 사유"),
                    fieldWithPath("reported_id").type(NUMBER).description("메세지 신고시, 대상 회원 아이디").optional(),
                    fieldWithPath("description").type(STRING).description("메세지 신고시, 대화내용").optional(),
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("방송 아이디"),
                    fieldWithPath("report_count").type(NUMBER).description("신고수"),
                )
            )
        )
    }

    @Test
    fun `Broadcast Add Heart Count API`() {

        val request = IntegerDto(10)

        val result: ResultActions = mockMvc
            .perform(
                post("/api/1/broadcast/{broadcast_id}/heart", broadcast.id)
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "add_heart_broadcast",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 ID")
                ),
                requestFields(
                    fieldWithPath("number").type(NUMBER).description("유저가 누른 하트수")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("방송 아이디"),
                    fieldWithPath("heart_count").type(NUMBER).description("하트수"),
                )
            )
        )
    }

    @Test
    fun `Broadcast Get Heart Count API`() {

        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/broadcast/{broadcast_id}/heart", broadcast.id)
                    .header(AUTHORIZATION, requestUserToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_heart_count_broadcast",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 ID")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("방송 아이디"),
                    fieldWithPath("heart_count").type(NUMBER).description("하트수"),
                )
            )
        )
    }

    @Test
    fun `Broadcast Choose Visibility Of Vod By End Of Broadcast API - Success`() {
        vodRepository.save(makeVod(broadcast))
        val request = BooleanDto(true)

        val result: ResultActions = mockMvc
            .perform(
                patch("/api/1/broadcast/{broadcast_id}/vod-visibility", broadcast.id)
                    .header(AUTHORIZATION, defaultInfluencerToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "choose_visibility_vod_by_end_of_broadcast",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 ID")
                ),
                requestFields(
                    fieldWithPath("bool").type(BOOLEAN).description("VOD 공개 여부")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("VOD 아이디"),
                    fieldWithPath("is_visible").type(BOOLEAN).description("VOD 공개 여부"),
                )
            )
        )
    }

    @Test
    fun `Broadcast Choose Vod Visibility API - Fail ACCESS_DENIED`() {
        val request = BooleanDto(true)

        val errorCode = ACCESS_DENIED
        val result: ResultActions = mockMvc
            .perform(
                patch("/api/1/broadcast/{broadcast_id}/vod-visibility", 0)
                    .header(AUTHORIZATION, defaultInfluencerToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().is4xxClientError)
            .andExpect(isErrorCode(errorCode))
            .andDo(print())

        result.andDo(
            document(
                "choose_visibility_vod_by_end_of_broadcast_fail",
               ExceptionFieldsSnippet(errorCode.key, exceptionConvertFieldDescriptor(result, "요청한 유저가 해당 방송의 오너가 아닐때"))
            )
        )
    }


    private fun saveTwoBroadcast() {
        broadcastRepository.saveAll(
            listOf(
                makeBroadcast(
                    defaultBroadcastCategory,
                    defaultInfluencer.id,
                    title = "broadcast title",
                    startedAt = now().plusDays(5)
                ),
                makeBroadcast(
                    defaultBroadcastCategory,
                    defaultInfluencer.id,
                    title = "this is title",
                    startedAt = now().plusDays(10)
                )
            )
        )
    }
}
