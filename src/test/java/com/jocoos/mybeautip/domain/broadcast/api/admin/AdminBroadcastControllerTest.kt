package com.jocoos.mybeautip.domain.broadcast.api.admin

import com.jocoos.mybeautip.domain.broadcast.BroadcastTestSupport
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastReportType
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastReportType.BROADCAST
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastPatchRequest
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastReport
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastReportRepository
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastRepository
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT
import com.jocoos.mybeautip.global.dto.single.IntegerDto
import com.jocoos.mybeautip.testutil.fixture.makeBroadcast
import com.jocoos.mybeautip.testutil.fixture.makeBroadcastReport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.openapitools.jackson.nullable.JsonNullable
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@TestInstance(PER_CLASS)
class AdminBroadcastControllerTest(
    private val broadcastRepository: BroadcastRepository,
    private val broadcastReportRepository: BroadcastReportRepository
) : BroadcastTestSupport() {

    private lateinit var broadcast: Broadcast

    @BeforeAll
    fun beforeAll() {
        broadcast = saveBroadcast()
    }

    @AfterAll
    fun afterAll() {
        broadcastRepository.delete(broadcast)
    }

    @Test
    fun `Admin Broadcast 목록 조회 API`() {

        saveBroadcast(title = "another broadcast")
        saveBroadcast(title = "this is broadcast")

        val result: ResultActions = mockMvc
            .perform(
                get("/admin/broadcast")
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_broadcast_list",
                requestParameters(
                    parameterWithName("page").description("페이지 넘버").attributes(getDefault(1)).optional(),
                    parameterWithName("size").description("페이지 사이타이").attributes(getDefault(5)).optional(),
                    parameterWithName("sort").description(generateLinkCode(BROADCAST_SORT_FIELD)).optional().attributes(getDefault("SORTED_STATUS")),
                    parameterWithName("order").description("정렬 방향 ASC, DESC").optional().attributes(getDefault("ASC")),
                    parameterWithName("status").description(generateLinkCode(BROADCAST_STATUS)).optional(),
                    parameterWithName("start_at").description("검색 시작일자 YYYY-MM-DD").optional(),
                    parameterWithName("end_at").description("검색 종료일자 YYYY-MM-DD").optional(),
                    parameterWithName("search").description("검색 필드 (title, username) (검색필드,검색어)").optional(),
                    parameterWithName("is_reported").description("신고 여부 Boolean").optional()
                ),
                responseFields(
                    fieldWithPath("total").type(NUMBER).description("방송 총 개수"),
                    fieldWithPath("content").type(ARRAY).description("방송 목록"),
                    fieldWithPath("content.[].id").type(NUMBER).description("방송 아이디"),
                    fieldWithPath("content.[].video_key").type(NUMBER).description("플립플랍 라이트 비디오 키"),
                    fieldWithPath("content.[].status").type(STRING).description(generateLinkCode(BROADCAST_STATUS)),
                    fieldWithPath("content.[].url").type(STRING).description("방송 URL").optional(),
                    fieldWithPath("content.[].title").type(STRING).description("타이틀"),
                    fieldWithPath("content.[].notice").type(STRING).description("공지사항").optional(),
                    fieldWithPath("content.[].thumbnail_url").type(STRING).description("썸네일 URL"),
                    fieldWithPath("content.[].can_chat").type(BOOLEAN).description("채팅 가능 여부"),
                    fieldWithPath("content.[].is_sound_on").type(BOOLEAN).description("사운드 여부"),
                    fieldWithPath("content.[].is_screen_show").type(BOOLEAN).description("화면 표시 여부"),
                    fieldWithPath("content.[].viewer_count").type(NUMBER).description("시청자수"),
                    fieldWithPath("content.[].max_viewer_count").type(NUMBER).description("최대 시청자수"),
                    fieldWithPath("content.[].heart_count").type(NUMBER).description("하트수"),
                    fieldWithPath("content.[].started_at").type(STRING).description("예약일시").attributes(getZonedDateFormat()),
                    fieldWithPath("content.[].created_at").type(STRING).description("생성일시").attributes(getZonedDateFormat()),
                    fieldWithPath("content.[].category").type(OBJECT).description("카테고리 정보"),
                    fieldWithPath("content.[].category.id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("content.[].category.title").type(STRING).description("카테고리 타이틀"),
                    fieldWithPath("content.[].created_by").type(OBJECT).description("회원 정보"),
                    fieldWithPath("content.[].created_by.id").type(NUMBER).description("회원 아이디"),
                    fieldWithPath("content.[].created_by.email").type(STRING).description("회원 이메일").optional(),
                    fieldWithPath("content.[].created_by.username").type(STRING).description("회원 닉네임"),
                    fieldWithPath("content.[].created_by.avatar_url").type(STRING).description("회원 아바타 URL")
                )
            )
        )
    }

    @Test
    fun `Admin Broadcast 상세 조회 API`() {

        val result: ResultActions = mockMvc
            .perform(
                get("/admin/broadcast/{broadcast_id}", broadcast.id)
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_broadcast",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 아이디")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("방송 아이디"),
                    fieldWithPath("video_key").type(NUMBER).description("플립플랍 라이트 비디오 키"),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(BROADCAST_STATUS)),
                    fieldWithPath("url").type(STRING).description("방송 URL").optional(),
                    fieldWithPath("title").type(STRING).description("타이틀"),
                    fieldWithPath("notice").type(STRING).description("공지사항").optional(),
                    fieldWithPath("thumbnail_url").type(STRING).description("썸네일 URL"),
                    fieldWithPath("can_chat").type(BOOLEAN).description("채팅 가능 여부"),
                    fieldWithPath("is_sound_on").type(BOOLEAN).description("사운드 여부"),
                    fieldWithPath("is_screen_show").type(BOOLEAN).description("화면 표시 여부"),
                    fieldWithPath("viewer_count").type(NUMBER).description("시청자수"),
                    fieldWithPath("max_viewer_count").type(NUMBER).description("최대 시청자수"),
                    fieldWithPath("heart_count").type(NUMBER).description("하트수"),
                    fieldWithPath("started_at").type(STRING).description("예약일시").attributes(getZonedDateFormat()),
                    fieldWithPath("created_at").type(STRING).description("생성일시").attributes(getZonedDateFormat()),
                    fieldWithPath("category").type(OBJECT).description("카테고리 정보"),
                    fieldWithPath("category.id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("category.title").type(STRING).description("카테고리 타이틀"),
                    fieldWithPath("created_by").type(OBJECT).description("회원 정보"),
                    fieldWithPath("created_by.id").type(NUMBER).description("회원 아이디"),
                    fieldWithPath("created_by.email").type(STRING).description("회원 이메일").optional(),
                    fieldWithPath("created_by.username").type(STRING).description("회원 닉네임"),
                    fieldWithPath("created_by.avatar_url").type(STRING).description("회원 아바타 URL"),
                    fieldWithPath("participant").type(OBJECT).description("참여자 정보"),
                    fieldWithPath("participant.member_id").type(NUMBER).description("참여자 아이디"),
                    fieldWithPath("participant.username").type(STRING).description("참여자 닉네임"),
                    fieldWithPath("participant.avatar_url").type(STRING).description("참여자 아바타 URL"),
                    fieldWithPath("participant.type").type(STRING).description(generateLinkCode(BROADCAST_VIEWER_TYPE)),
                    fieldWithPath("participant.status").type(STRING).description(generateLinkCode(BROADCAST_VIEWER_STATUS)),
                    fieldWithPath("participant.is_suspended").type(BOOLEAN).description("참여자 추방 여부"),
                    fieldWithPath("participant.broadcast_key").type(OBJECT).description("참여자 방송 관련 키 및 토큰 정보"),
                    fieldWithPath("participant.broadcast_key.stream_key").type(STRING).description("참여자 Stream Key, 방송 진행자일 경우에만 존재").optional(),
                    fieldWithPath("participant.broadcast_key.gossip_token").type(STRING).description("참여자 채팅 토큰"),
                    fieldWithPath("participant.broadcast_key.app_id").type(STRING).description("참여자 채팅 앱 ID"),
                    fieldWithPath("participant.broadcast_key.channel_key").type(STRING).description("참여자 채팅 채널 키")
                )
            )
        )
    }

    @Test
    fun `Admin Broadcast 수정 API`() {
        val newTitle = "new title"
        val newNotice = "new notice"
        val newThumbnailUrl = "newThumbnailUrl"
        val request = BroadcastPatchRequest(
            JsonNullable.of(newTitle),
            JsonNullable.of(newNotice),
            JsonNullable.of(newThumbnailUrl),
        )

        val result: ResultActions = mockMvc
            .perform(
                patch("/admin/broadcast/{broadcast_id}", broadcast.id)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        val updateBroadcast = broadcastRepository.findById(broadcast.id).orElseThrow()
        assertThat(updateBroadcast.title).isEqualTo(newTitle)
        assertThat(updateBroadcast.notice).isEqualTo(newNotice)
        assertThat(updateBroadcast.thumbnailUrl).contains(newThumbnailUrl)

        result
            .andDo(
                document(
                    "admin_edit_broadcast",
                    pathParameters(
                        parameterWithName("broadcast_id").description("방송 아이디")
                    ),
                    requestFields(
                        fieldWithPath("title").type(STRING).description("타이틀").optional(),
                        fieldWithPath("notice").type(STRING).description("공지사항").optional(),
                        fieldWithPath("thumbnail_url").type(STRING).description("썸네일 URL").optional(),
                    ),
                    responseFields(
                        fieldWithPath("id").type(NUMBER).description("방송 아이디")
                    )
                )
            )
    }

    @Test
    fun `Admin Broadcast 신고된 수 조회 API`() {

        val report = broadcastReportRepository.save(makeBroadcastReport(broadcast, requestUser.id))
        val startAt = report.createdAt.minusDays(1).format(DateTimeFormatter.ofPattern(ZONE_DATE_TIME_FORMAT))

        val result: ResultActions = mockMvc
            .perform(
                get("/admin/broadcast/report-count")
                    .header(AUTHORIZATION, defaultAdminToken)
                    .param("start_at", startAt)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result
            .andDo(
                document(
                    "admin_get_report_count_broadcast",
                    requestParameters(
                        parameterWithName("start_at").description("검색 시작 시간").attributes(getZonedDateFormat())
                    ),
                    responseFields(
                        fieldWithPath("count").type(NUMBER).description("검색 시작 시간 이후 신고된 방송수")
                    )
                )
            )
    }

    @Test
    fun `Admin Broadcast Shutdown API`() {
        val readyBroadcast = broadcastRepository.save(makeBroadcast(defaultBroadcastCategory, isStartNow = true))
        readyBroadcast.start("url", ZonedDateTime.now())

        val result: ResultActions = mockMvc
            .perform(
                patch("/admin/broadcast/{broadcast_id}/shutdown", readyBroadcast.id)
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isNoContent)
            .andDo(print())

        result
            .andDo(
                document(
                    "admin_shutdown_broadcast",
                    pathParameters(
                        parameterWithName("broadcast_id").description("방송 아이디")
                    )
                )
            )
    }

    @Test
    fun `Admin Broadcast Add Heart Count API`() {

        val request = IntegerDto(10)

        val result: ResultActions = mockMvc
            .perform(
                post("/admin/broadcast/{broadcast_id}/heart", broadcast.id)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_add_heart_broadcast",
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
    fun `Admin Broadcast Get Reports API`() {

        broadcastReportRepository.saveAll(listOf(
            makeBroadcastReport(broadcast, requestUser.id),
            makeBroadcastReport(broadcast, defaultAdmin.id)
        ))

        val result: ResultActions = mockMvc
            .perform(
                get("/admin/broadcast/{broadcast_id}/report", broadcast.id)
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_broadcast_report_list",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 아이디")
                ),
                requestParameters(
                    parameterWithName("page").description("페이지 넘버").attributes(getDefault(1)).optional(),
                    parameterWithName("size").description("페이지 사이타이").attributes(getDefault(3)).optional(),
                    parameterWithName("type").description(generateLinkCode(BROADCAST_REPORT_TYPE)).attributes(getDefault(BROADCAST)).optional(),
                ),
                responseFields(
                    fieldWithPath("total").type(NUMBER).description("방송 총 개수"),
                    fieldWithPath("content").type(ARRAY).description("방송 목록"),
                    fieldWithPath("content.[].id").type(NUMBER).description("방송 신고 아이디"),
                    fieldWithPath("content.[].reporter").type(OBJECT).description("신고자 정보"),
                    fieldWithPath("content.[].reporter.id").type(NUMBER).description("신고자 아이디"),
                    fieldWithPath("content.[].reporter.username").type(STRING).description("신고자 닉네임"),
                    fieldWithPath("content.[].reported").type(OBJECT).description("신고당한 회원 정보"),
                    fieldWithPath("content.[].reported.id").type(NUMBER).description("신고당한 회원 아이디"),
                    fieldWithPath("content.[].reported.username").type(STRING).description("신고당한 회원 닉네임"),
                    fieldWithPath("content.[].description").type(STRING).description("신고 내용 (신고당한 댓글 내용 등)"),
                    fieldWithPath("content.[].reason").type(STRING).description("신고 사유"),
                    fieldWithPath("content.[].created_at").type(STRING).description("생성일시").attributes(getZonedDateFormat())
                )))
    }

    @Test
    fun `Admin Broadcast Get Heart Count API`() {

        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/broadcast/{broadcast_id}/heart", broadcast.id)
                    .header(AUTHORIZATION, requestUserToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_heart_count_broadcast",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 ID")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("방송 아이디"),
                    fieldWithPath("heart_count").type(NUMBER).description("하트수")
                )
            )
        )
    }

    private fun saveBroadcast(title: String = "title") =
        broadcastRepository.save(makeBroadcast(defaultBroadcastCategory, title = title))
}
