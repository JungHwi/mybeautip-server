package com.jocoos.mybeautip.domain.broadcast.api.admin

import com.jocoos.mybeautip.domain.broadcast.BroadcastTestSupport
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastPatchRequest
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastReportRepository
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastRepository
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.BROADCAST_STATUS
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
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
                    parameterWithName("status").description(generateLinkCode(BROADCAST_STATUS)).optional(),
                    parameterWithName("start_at").description("검색 시작일자 YYYY-MM-DD").optional(),
                    parameterWithName("end_at").description("검색 종료일자 YYYY-MM-DD").optional(),
                    parameterWithName("search_field").description("검색 필드 title, username").optional(),
                    parameterWithName("search_keyword").description("검색 키워드").optional(),
                    parameterWithName("is_reported").description("신고 여부 Boolean").optional()
                ),
                responseFields(
                    fieldWithPath("[].id").type(NUMBER).description("방송 아이디"),
                    fieldWithPath("[].video_key").type(NUMBER).description("플립플랍 라이트 비디오 키"),
                    fieldWithPath("[].status").type(STRING).description(generateLinkCode(BROADCAST_STATUS)),
                    fieldWithPath("[].url").type(STRING).description("방송 URL"),
                    fieldWithPath("[].title").type(STRING).description("타이틀"),
                    fieldWithPath("[].notice").type(STRING).description("공지사항").optional(),
                    fieldWithPath("[].thumbnail_url").type(STRING).description("썸네일 URL"),
                    fieldWithPath("[].viewer_count").type(NUMBER).description("시청자수"),
                    fieldWithPath("[].max_viewer_count").type(NUMBER).description("최대 시청자수"),
                    fieldWithPath("[].heart_count").type(NUMBER).description("하트수"),
                    fieldWithPath("[].started_at").type(STRING).description("예약일시").attributes(getZonedDateFormat()),
                    fieldWithPath("[].created_at").type(STRING).description("생성일시").attributes(getZonedDateFormat()),
                    fieldWithPath("[].category").type(OBJECT).description("카테고리 정보"),
                    fieldWithPath("[].category.id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("[].category.title").type(STRING).description("카테고리 타이틀"),
                    fieldWithPath("[].member").type(OBJECT).description("회원 정보"),
                    fieldWithPath("[].member.id").type(NUMBER).description("회원 아이디"),
                    fieldWithPath("[].member.email").type(STRING).description("회원 이메일").optional(),
                    fieldWithPath("[].member.username").type(STRING).description("회원 닉네임"),
                    fieldWithPath("[].member.avatar_url").type(STRING).description("회원 아바타 URL"),
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
                    fieldWithPath("url").type(STRING).description("방송 URL"),
                    fieldWithPath("title").type(STRING).description("타이틀"),
                    fieldWithPath("notice").type(STRING).description("공지사항").optional(),
                    fieldWithPath("thumbnail_url").type(STRING).description("썸네일 URL"),
                    fieldWithPath("viewer_count").type(NUMBER).description("시청자수"),
                    fieldWithPath("max_viewer_count").type(NUMBER).description("최대 시청자수"),
                    fieldWithPath("heart_count").type(NUMBER).description("하트수"),
                    fieldWithPath("started_at").type(STRING).description("예약일시").attributes(getZonedDateFormat()),
                    fieldWithPath("created_at").type(STRING).description("생성일시").attributes(getZonedDateFormat()),
                    fieldWithPath("category").type(OBJECT).description("카테고리 정보"),
                    fieldWithPath("category.id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("category.title").type(STRING).description("카테고리 타이틀"),
                    fieldWithPath("member").type(OBJECT).description("회원 정보"),
                    fieldWithPath("member.id").type(NUMBER).description("회원 아이디"),
                    fieldWithPath("member.email").type(STRING).description("회원 이메일").optional(),
                    fieldWithPath("member.username").type(STRING).description("회원 닉네임"),
                    fieldWithPath("member.avatar_url").type(STRING).description("회원 아바타 URL")
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
            JsonNullable.of(newThumbnailUrl)
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

    private fun saveBroadcast(title: String = "title") =
        broadcastRepository.save(makeBroadcast(defaultBroadcastCategory, title = title))
}
