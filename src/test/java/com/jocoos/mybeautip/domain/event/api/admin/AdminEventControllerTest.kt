package com.jocoos.mybeautip.domain.event.api.admin

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.DRIP
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCategoryRepository
import com.jocoos.mybeautip.domain.event.code.EventProductType
import com.jocoos.mybeautip.domain.event.code.EventStatus.*
import com.jocoos.mybeautip.domain.event.code.EventType
import com.jocoos.mybeautip.domain.event.code.SortField.CREATED_AT
import com.jocoos.mybeautip.domain.event.dto.EditEventRequest
import com.jocoos.mybeautip.domain.event.dto.EventProductRequest
import com.jocoos.mybeautip.domain.event.dto.EventRequest
import com.jocoos.mybeautip.domain.event.persistence.domain.Event
import com.jocoos.mybeautip.domain.event.persistence.repository.EventRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.global.constant.MybeautipConstant.TEST_FILE_URL
import com.jocoos.mybeautip.global.dto.single.BooleanDto
import com.jocoos.mybeautip.testutil.fixture.makeCommunityCategory
import com.jocoos.mybeautip.testutil.fixture.makeEvent
import com.jocoos.mybeautip.testutil.fixture.makeEvents
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.ZonedDateTime

class AdminEventControllerTest(
    private val eventRepository: EventRepository,
    private val communityCategoryRepository: CommunityCategoryRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun getStatus() {

        eventRepository.saveAll(makeEvents(3, PROGRESS))
        eventRepository.saveAll(makeEvents(3, END))

        val result: ResultActions = mockMvc
            .perform(
                get("/admin/event/status")
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_event_status",
                responseFields(
                    fieldWithPath("[].status").type(STRING).description(generateLinkCode(EVENT_STATUS)).optional(),
                    fieldWithPath("[].status_name").type(STRING).description("이벤트 상태 이름"),
                    fieldWithPath("[].count").type(NUMBER).description("이벤트수")
                )
            )
        )
    }


    @Test
    fun getEvents() {

        eventRepository.save(makeEvent());

        val result: ResultActions = mockMvc
            .perform(
                get("/admin/event")
                    .header(AUTHORIZATION, defaultAdminToken)

            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_events",
                requestParameters(
                    parameterWithName("status").description(generateLinkCode(EVENT_STATUS)).optional(),
                    parameterWithName("page").attributes(getDefault(1)).description("페이지").optional(),
                    parameterWithName("size").attributes(getDefault(10)).description("페이지 크기").optional(),
                    parameterWithName("sort").attributes(getDefault(CREATED_AT))
                        .description(generateLinkCode(SORT_FIELD)).optional(),
                    parameterWithName("order").attributes(getDefault("DESC")).description("정렬 방향").optional(),
                    parameterWithName("search").description("검색 필드,검색 키워드").optional(),
                    parameterWithName("startAt").description("검색 시작일").optional(),
                    parameterWithName("endAt").description("검색 종료").optional(),
                    parameterWithName("is_top_fix").description("상단 고정 여부 (boolean)").optional(),
                    parameterWithName("community_category_id").description("커뮤니티 카테고리 ID").optional()
                ),
                responseFields(
                    fieldWithPath("total").type(NUMBER).description("총 개수"),
                    fieldWithPath("content").type(ARRAY).description("이벤트 목록"),
                    fieldWithPath("content.[].id").type(NUMBER).description("이벤트 ID"),
                    fieldWithPath("content.[].type").type(STRING).description(generateLinkCode(EVENT_TYPE)),
                    fieldWithPath("content.[].status").type(STRING).description(generateLinkCode(EVENT_STATUS)),
                    fieldWithPath("content.[].is_visible").type(BOOLEAN).description("노출 여부"),
                    fieldWithPath("content.[].is_top_fix").type(BOOLEAN).description("상단 고정 여부").optional(),
                    fieldWithPath("content.[].title").type(STRING).description("제목"),
                    fieldWithPath("content.[].description").type(STRING).description("설명"),
                    fieldWithPath("content.[].thumbnail_image_url").type(STRING).description("썸네일 이미지 URL"),
                    fieldWithPath("content.[].join_count").type(NUMBER).description("참여수"),
                    fieldWithPath("content.[].need_point").type(NUMBER).description("이벤트 참여 포인트"),
                    fieldWithPath("content.[].start_at").type(STRING).description("이벤트 시작일시")
                        .attributes(getZonedDateFormat()),
                    fieldWithPath("content.[].end_at").type(STRING).description("이벤트 종료일시")
                        .attributes(getZonedDateFormat()),
                    fieldWithPath("content.[].reservation_at").type(STRING).description("이벤트 예약일시")
                        .attributes(getZonedDateFormat()).optional(),
                    fieldWithPath("content.[].created_at").type(STRING).description("이벤트 생성일시")
                        .attributes(getZonedDateMilliFormat())
                )
            )
        )
    }

    @Test
    fun getEvent() {

        val event: Event = eventRepository.save(makeEvent());

        val result: ResultActions = mockMvc
            .perform(
                get("/admin/event/{event_id}", event.id)
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_event",
                pathParameters(
                    parameterWithName("event_id").description("이벤트 ID").optional()
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("이벤트 ID"),
                    fieldWithPath("type").type(STRING).description(generateLinkCode(EVENT_TYPE)),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(EVENT_STATUS)),
                    fieldWithPath("is_visible").type(BOOLEAN).description("노출 여부"),
                    fieldWithPath("is_top_fix").type(BOOLEAN).description("상단 고정 여부").optional(),
                    fieldWithPath("title").type(STRING).description("제목"),
                    fieldWithPath("description").type(STRING).description("설명"),
                    fieldWithPath("thumbnail_image_url").type(STRING).description("썸네일 이미지 URL"),
                    fieldWithPath("detail_image_url").type(STRING).description("이벤트 상세 이미지 URL"),
                    fieldWithPath("banner_image_url").type(STRING).description("배너 이미지 URL").optional(),
                    fieldWithPath("share_rectangle_image_url").type(STRING).description("웹 공유 이미지 URL"),
                    fieldWithPath("share_square_image_url").type(STRING).description("SNS 공유 이미지 URL"),
                    fieldWithPath("join_count").type(NUMBER).description("참여수"),
                    fieldWithPath("need_point").type(NUMBER).description("이벤트 참여 포인트"),
                    fieldWithPath("start_at").type(STRING).description("이벤트 시작일시").attributes(getZonedDateFormat()),
                    fieldWithPath("end_at").type(STRING).description("이벤트 종료일시").attributes(getZonedDateFormat()),
                    fieldWithPath("reservation_at").type(STRING).description("이벤트 예약일시")
                        .attributes(getZonedDateFormat()).optional(),
                    fieldWithPath("created_at").type(STRING).description("이벤트 생성일시")
                        .attributes(getZonedDateMilliFormat()),
                    fieldWithPath("product").type(OBJECT).description("이벤트 상품 정보").optional(),
                    fieldWithPath("product.type").type(STRING).description(generateLinkCode(EVENT_PRODUCT_TYPE)),
                    fieldWithPath("product.price").type(NUMBER).description("상품 가격")
                )
            )
        )
    }

    @Test
    fun uploadFiles() {
        val result = mockMvc
            .perform(
                multipart("/admin/event/files")
                    .file("files", "mockup".toByteArray())
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_upload_event_file",
                requestParts(
                    partWithName("files").description("업로드할 파일 목록")
                ),
                responseFields(
                    fieldWithPath("[]").type(ARRAY).description("UPLOAD 된 파일 URL")
                )
            )
        )
    }

    @Test
    fun createEvent() {

        // given
        communityCategoryRepository.save(makeCommunityCategory(type = DRIP))

        val productRequest = EventProductRequest.builder()
            .type(EventProductType.POINT)
            .price(3000)
            .build()

        val request = EventRequest.builder()
            .type(EventType.DRIP)
            .status(WAIT)
            .isVisible(true)
            .title("Mock Title")
            .description("Mock Contents")
            .needPoint(0)
            .startAt(ZonedDateTime.now().minusDays(1))
            .endAt(ZonedDateTime.now().plusDays(10))
            .thumbnailImageUrl(TEST_FILE_URL)
            .detailImageUrl(TEST_FILE_URL)
            .shareRectangleImageUrl(TEST_FILE_URL)
            .shareSquareImageUrl(TEST_FILE_URL)
            .product(productRequest)
            .build()

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                post("/admin/event")
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isCreated)
            .andDo(print())

        result.andDo(
            document(
                "admin_create_event",
                requestFields(
                    fieldWithPath("type").type(STRING).description(generateLinkCode(EVENT_TYPE)),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(EVENT_STATUS)),
                    fieldWithPath("is_visible").type(BOOLEAN).description("노출 여부"),
                    fieldWithPath("title").type(STRING).description("제목"),
                    fieldWithPath("description").type(STRING).description("설명"),
                    fieldWithPath("need_point").type(NUMBER).description("이벤트 참가시 필요한 포인트"),
                    fieldWithPath("start_at").type(STRING).description("이벤트 시작일시").attributes(getZonedDateFormat()),
                    fieldWithPath("end_at").type(STRING).description("이벤트 종료일시").attributes(getZonedDateFormat()),
                    fieldWithPath("reservation_at").type(STRING).description("이벤트 예약일시")
                        .attributes(getZonedDateFormat()).optional(),
                    fieldWithPath("thumbnail_image_url").type(STRING).description("썸네일 이미지 URL"),
                    fieldWithPath("detail_image_url").type(STRING).description("상세 이미지 URL"),
                    fieldWithPath("share_rectangle_image_url").type(STRING).description("공유용 사각형 이미지 URL"),
                    fieldWithPath("share_square_image_url").type(STRING).description("공유용 정사각형 이미지 URL"),
                    fieldWithPath("banner_image_url").type(STRING).description("배너 이미지 URL(Drip 전용)").optional(),
                    fieldWithPath("product").type(OBJECT).description("이벤트 상품 정보").optional(),
                    fieldWithPath("product.type").type(STRING).description(generateLinkCode(EVENT_PRODUCT_TYPE)),
                    fieldWithPath("product.price").type(NUMBER).description("상품 가격")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("이벤트 ID"),
                    fieldWithPath("type").type(STRING).description(generateLinkCode(EVENT_TYPE)),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(EVENT_STATUS)),
                    fieldWithPath("is_visible").type(BOOLEAN).description("노출여부"),
                    fieldWithPath("title").type(STRING).description("제목"),
                    fieldWithPath("description").type(STRING).description("설명"),
                    fieldWithPath("thumbnail_image_url").type(STRING).description("썸네일 이미지 URL"),
                    fieldWithPath("detail_image_url").type(STRING).description("상세 이미지 URL"),
                    fieldWithPath("share_rectangle_image_url").type(STRING).description("공유용 사각형 이미지 URL"),
                    fieldWithPath("share_square_image_url").type(STRING).description("공유용 정사각형 이미지 URL"),
                    fieldWithPath("banner_image_url").type(STRING).description("배너 이미지 URL(Drip 전용)").optional(),
                    fieldWithPath("join_count").type(NUMBER).description("이벤트 참여수"),
                    fieldWithPath("need_point").type(NUMBER).description("이벤트 참여시 필요 포인트"),
                    fieldWithPath("start_at").type(STRING).description("이벤트 시작일시").attributes(getZonedDateFormat()),
                    fieldWithPath("end_at").type(STRING).description("이벤트 종료일시").attributes(getZonedDateFormat()),
                    fieldWithPath("reservation_at").type(STRING).description("이벤트 예약일시")
                        .attributes(getZonedDateFormat()).optional(),
                    fieldWithPath("created_at").type(STRING).description("이벤트 등록일시").attributes(getZonedDateFormat()),
                    fieldWithPath("product").type(OBJECT).description("이벤트 상품 정보").optional(),
                    fieldWithPath("product.type").type(STRING).description(generateLinkCode(EVENT_PRODUCT_TYPE)),
                    fieldWithPath("product.price").type(NUMBER).description("상품 가격")
                )
            )
        )
    }

    @Test
    fun editEvent() {

        // given
        val event: Event = eventRepository.save(makeEvent());

        val productRequest = EventProductRequest.builder()
            .type(EventProductType.POINT)
            .price(3000)
            .build()

        val request = EditEventRequest.builder()
            .status(WAIT)
            .isVisible(true)
            .title("Mock Title")
            .description("Mock Contents")
            .needPoint(0)
            .startAt(ZonedDateTime.now())
            .endAt(ZonedDateTime.now().plusDays(10))
            .reservationAt(ZonedDateTime.now())
            .thumbnailImageUrl(TEST_FILE_URL)
            .detailImageUrl(TEST_FILE_URL)
            .shareRectangleImageUrl(TEST_FILE_URL)
            .shareSquareImageUrl(TEST_FILE_URL)
            .product(productRequest)
            .build()

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                put("/admin/event/{event_id}", event.id)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())


        result.andDo(
            document(
                "admin_edit_event",
                pathParameters(
                    parameterWithName("event_id").description("이벤트 ID")
                ),
                requestFields(
                    fieldWithPath("status").type(STRING).description(generateLinkCode(EVENT_STATUS)),
                    fieldWithPath("is_visible").type(BOOLEAN).description("노출 여부"),
                    fieldWithPath("title").type(STRING).description("제목"),
                    fieldWithPath("description").type(STRING).description("설명"),
                    fieldWithPath("need_point").type(NUMBER).description("이벤트 참가시 필요한 포인트"),
                    fieldWithPath("start_at").type(STRING).description("이벤트 시작일시").attributes(getZonedDateFormat()),
                    fieldWithPath("end_at").type(STRING).description("이벤트 종료일시").attributes(getZonedDateFormat()),
                    fieldWithPath("reservation_at").type(STRING).description("이벤트 예약일시")
                        .attributes(getZonedDateFormat()).optional(),
                    fieldWithPath("thumbnail_image_url").type(STRING).description("썸네일 이미지 URL"),
                    fieldWithPath("detail_image_url").type(STRING).description("상세 이미지 URL"),
                    fieldWithPath("share_rectangle_image_url").type(STRING).description("공유용 사각형 이미지 URL"),
                    fieldWithPath("share_square_image_url").type(STRING).description("공유용 정사각형 이미지 URL"),
                    fieldWithPath("banner_image_url").type(STRING).description("배너 이미지 URL(Drip 전용)").optional(),
                    fieldWithPath("product").type(OBJECT).description("이벤트 상품 정보").optional(),
                    fieldWithPath("product.type").type(STRING).description(generateLinkCode(EVENT_PRODUCT_TYPE)),
                    fieldWithPath("product.price").type(NUMBER).description("상품 가격")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("이벤트 ID"),
                    fieldWithPath("type").type(STRING).description(generateLinkCode(EVENT_TYPE)),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(EVENT_STATUS)),
                    fieldWithPath("is_visible").type(BOOLEAN).description("노출여부"),
                    fieldWithPath("title").type(STRING).description("제목"),
                    fieldWithPath("description").type(STRING).description("설명"),
                    fieldWithPath("thumbnail_image_url").type(STRING).description("썸네일 이미지 URL"),
                    fieldWithPath("detail_image_url").type(STRING).description("상세 이미지 URL"),
                    fieldWithPath("share_rectangle_image_url").type(STRING).description("공유용 사각형 이미지 URL"),
                    fieldWithPath("share_square_image_url").type(STRING).description("공유용 정사각형 이미지 URL"),
                    fieldWithPath("banner_image_url").type(STRING).description("배너 이미지 URL(Drip 전용)").optional(),
                    fieldWithPath("join_count").type(NUMBER).description("이벤트 참여수"),
                    fieldWithPath("need_point").type(NUMBER).description("이벤트 참여시 필요 포인트"),
                    fieldWithPath("start_at").type(STRING).description("이벤트 시작일시").attributes(getZonedDateFormat()),
                    fieldWithPath("end_at").type(STRING).description("이벤트 종료일시").attributes(getZonedDateFormat()),
                    fieldWithPath("reservation_at").type(STRING).description("이벤트 예약일시")
                        .attributes(getZonedDateFormat()).optional(),
                    fieldWithPath("created_at").type(STRING).description("이벤트 등록일시").attributes(getZonedDateFormat()),
                    fieldWithPath("product").type(OBJECT).description("이벤트 상품 정보").optional(),
                    fieldWithPath("product.type").type(STRING).description(generateLinkCode(EVENT_PRODUCT_TYPE)),
                    fieldWithPath("product.price").type(NUMBER).description("상품 가격")
                )
            )
        )
    }

    @Test
    fun fixEvent() {

        // given
        val event: Event = eventRepository.save(makeEvent());
        val request = BooleanDto(true)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                patch("/admin/event/{event_id}/hide", event.id)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_hide_event",
                pathParameters(
                    parameterWithName("event_id").description("이벤트 ID")
                ),
                requestFields(
                    fieldWithPath("bool").type(BOOLEAN).description("숨김 여부")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("이벤트 ID")
                )
            )
        )
    }

    @Test
    fun deleteEvent() {

        val event: Event = eventRepository.save(makeEvent());

        val result: ResultActions = mockMvc
            .perform(
                delete("/admin/event/{event_id}", event.id)
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_delete_event",
                pathParameters(
                    parameterWithName("event_id").description("이벤트 ID")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("이벤트 ID")
                )
            )
        )
    }

}
