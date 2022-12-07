package com.jocoos.mybeautip.domain.event.api.admin;

import com.jocoos.mybeautip.domain.event.code.EventProductType;
import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.dto.EditEventRequest;
import com.jocoos.mybeautip.domain.event.dto.EventProductRequest;
import com.jocoos.mybeautip.domain.event.dto.EventRequest;
import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.domain.event.code.SortField.CREATED_AT;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.*;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode;
import static com.jocoos.mybeautip.global.constant.MybeautipConstant.TEST_FILE_URL;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminEventControllerTest extends RestDocsTestSupport {

    @Test
    void getStatus() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/event/status"))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_event_status",
                responseFields(
                        fieldWithPath("[].status").type(JsonFieldType.STRING).description(generateLinkCode(EVENT_STATUS)).optional(),
                        fieldWithPath("[].status_name").type(JsonFieldType.STRING).description("이벤트 상태 이름"),
                        fieldWithPath("[].count").type(JsonFieldType.NUMBER).description("이벤트수")
                )
        ));
    }


    @Test
    void getEvents() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/event"))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_events",
                        requestParameters(
                                parameterWithName("status").description(generateLinkCode(EVENT_STATUS)).optional(),
                                parameterWithName("page").attributes(getDefault(1)).description("페이지").optional(),
                                parameterWithName("size").attributes(getDefault(10)).description("페이지 크기").optional(),
                                parameterWithName("sort").attributes(getDefault(CREATED_AT)).description(generateLinkCode(SORT_FIELD)).optional(),
                                parameterWithName("order").attributes(getDefault("DESC")).description("정렬 방향").optional(),
                                parameterWithName("search").description("검색 필드,검색 키워드").optional(),
                                parameterWithName("startAt").description("검색 시작일").optional(),
                                parameterWithName("endAt").description("검색 종료").optional(),
                                parameterWithName("is_top_fix").description("상단 고정 여부 (boolean)").optional(),
                                parameterWithName("community_category_id").description("커뮤니티 카테고리 ID").optional()
                        ),
                        responseFields(
                                fieldWithPath("total").type(JsonFieldType.NUMBER).description("총 개수"),
                                fieldWithPath("content").type(JsonFieldType.ARRAY).description("이벤트 목록"),
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("이벤트 ID"),
                                fieldWithPath("content.[].type").type(JsonFieldType.STRING).description(generateLinkCode(EVENT_TYPE)),
                                fieldWithPath("content.[].status").type(JsonFieldType.STRING).description(generateLinkCode(EVENT_STATUS)),
                                fieldWithPath("content.[].is_visible").type(JsonFieldType.BOOLEAN).description("노출 여부"),
                                fieldWithPath("content.[].title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("content.[].description").type(JsonFieldType.STRING).description("설명"),
                                fieldWithPath("content.[].thumbnail_image_url").type(JsonFieldType.STRING).description("썸네일 이미지 URL"),
                                fieldWithPath("content.[].join_count").type(JsonFieldType.NUMBER).description("참여수"),
                                fieldWithPath("content.[].need_point").type(JsonFieldType.NUMBER).description("이벤트 참여 포인트"),
                                fieldWithPath("content.[].start_at").type(JsonFieldType.STRING).description("이벤트 시작일시").attributes(getZonedDateFormat()),
                                fieldWithPath("content.[].end_at").type(JsonFieldType.STRING).description("이벤트 종료일시").attributes(getZonedDateFormat()),
                                fieldWithPath("content.[].reservation_at").type(JsonFieldType.STRING).description("이벤트 예약일시").attributes(getZonedDateFormat()).optional(),
                                fieldWithPath("content.[].created_at").type(JsonFieldType.STRING).description("이벤트 생성일시").attributes(getZonedDateMilliFormat())
                        )
                )
        );
    }

    @Test
    void getEvent() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/event/{event_id}", 1))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_event",
                        pathParameters(
                                parameterWithName("event_id").description("이벤트 ID").optional()
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("이벤트 ID"),
                                fieldWithPath("type").type(JsonFieldType.STRING).description(generateLinkCode(EVENT_TYPE)),
                                fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(EVENT_STATUS)),
                                fieldWithPath("is_visible").type(JsonFieldType.BOOLEAN).description("노출 여부"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("설명"),
                                fieldWithPath("thumbnail_image_url").type(JsonFieldType.STRING).description("썸네일 이미지 URL"),
                                fieldWithPath("detail_image_url").type(JsonFieldType.STRING).description("이벤트 상세 이미지 URL"),
                                fieldWithPath("banner_image_url").type(JsonFieldType.STRING).description("배너 이미지 URL").optional(),
                                fieldWithPath("share_rectangle_image_url").type(JsonFieldType.STRING).description("웹 공유 이미지 URL"),
                                fieldWithPath("share_square_image_url").type(JsonFieldType.STRING).description("SNS 공유 이미지 URL"),
                                fieldWithPath("join_count").type(JsonFieldType.NUMBER).description("참여수"),
                                fieldWithPath("need_point").type(JsonFieldType.NUMBER).description("이벤트 참여 포인트"),
                                fieldWithPath("start_at").type(JsonFieldType.STRING).description("이벤트 시작일시").attributes(getZonedDateFormat()),
                                fieldWithPath("end_at").type(JsonFieldType.STRING).description("이벤트 종료일시").attributes(getZonedDateFormat()),
                                fieldWithPath("reservation_at").type(JsonFieldType.STRING).description("이벤트 예약일시").attributes(getZonedDateFormat()).optional(),
                                fieldWithPath("created_at").type(JsonFieldType.STRING).description("이벤트 생성일시").attributes(getZonedDateMilliFormat()),
                                fieldWithPath("product").type(JsonFieldType.OBJECT).description("이벤트 상품 정보").optional(),
                                fieldWithPath("product.type").type(JsonFieldType.STRING).description(generateLinkCode(EVENT_PRODUCT_TYPE)),
                                fieldWithPath("product.price").type(JsonFieldType.NUMBER).description("상품 가격")

                        )
                )
        );
    }

    @Test
    @WithUserDetails(value = "1", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void uploadFiles() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .multipart("/admin/event/files")
                        .file("files", "mockup".getBytes()))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_upload_event_file",
                        requestParts(
                                partWithName("files").description("업로드할 파일 목록")
                        ),
                        responseFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("UPLOAD 된 파일 URL")
                        )
                )
        );
    }

    @Test
    @Transactional
    @WithUserDetails(value = "1", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void createEvent() throws Exception {
        EventProductRequest productRequest = EventProductRequest.builder()
                .type(EventProductType.POINT)
                .price(3000)
                .build();

        EventRequest request = EventRequest.builder()
                .type(EventType.DRIP)
                .status(EventStatus.WAIT)
                .isVisible(true)
                .title("Mock Title")
                .description("Mock Contents")
                .needPoint(0)
                .startAt(ZonedDateTime.now())
                .endAt(ZonedDateTime.now().plusDays(10))
                .thumbnailImageUrl(TEST_FILE_URL)
                .detailImageUrl(TEST_FILE_URL)
                .shareRectangleImageUrl(TEST_FILE_URL)
                .shareSquareImageUrl(TEST_FILE_URL)
                .product(productRequest)
                .build();

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                    .post("/admin/event")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(print());

        result.andDo(document("admin_create_event",
                requestFields(
                        fieldWithPath("type").type(JsonFieldType.STRING).description(generateLinkCode(EVENT_TYPE)),
                        fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(EVENT_STATUS)),
                        fieldWithPath("is_visible").type(JsonFieldType.BOOLEAN).description("노출 여부"),
                        fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("설명"),
                        fieldWithPath("need_point").type(JsonFieldType.NUMBER).description("이벤트 참가시 필요한 포인트"),
                        fieldWithPath("start_at").type(JsonFieldType.STRING).description("이벤트 시작일시").attributes(getZonedDateFormat()),
                        fieldWithPath("end_at").type(JsonFieldType.STRING).description("이벤트 종료일시").attributes(getZonedDateFormat()),
                        fieldWithPath("reservation_at").type(JsonFieldType.STRING).description("이벤트 예약일시").attributes(getZonedDateFormat()).optional(),
                        fieldWithPath("thumbnail_image_url").type(JsonFieldType.STRING).description("썸네일 이미지 URL"),
                        fieldWithPath("detail_image_url").type(JsonFieldType.STRING).description("상세 이미지 URL"),
                        fieldWithPath("share_rectangle_image_url").type(JsonFieldType.STRING).description("공유용 사각형 이미지 URL"),
                        fieldWithPath("share_square_image_url").type(JsonFieldType.STRING).description("공유용 정사각형 이미지 URL"),
                        fieldWithPath("banner_image_url").type(JsonFieldType.STRING).description("배너 이미지 URL(Drip 전용)").optional(),
                        fieldWithPath("product").type(JsonFieldType.OBJECT).description("이벤트 상품 정보").optional(),
                        fieldWithPath("product.type").type(JsonFieldType.STRING).description(generateLinkCode(EVENT_PRODUCT_TYPE)),
                        fieldWithPath("product.price").type(JsonFieldType.NUMBER).description("상품 가격")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("이벤트 ID"),
                        fieldWithPath("type").type(JsonFieldType.STRING).description(generateLinkCode(EVENT_TYPE)),
                        fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(EVENT_STATUS)),
                        fieldWithPath("is_visible").type(JsonFieldType.BOOLEAN).description("노출여부"),
                        fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("설명"),
                        fieldWithPath("thumbnail_image_url").type(JsonFieldType.STRING).description("썸네일 이미지 URL"),
                        fieldWithPath("detail_image_url").type(JsonFieldType.STRING).description("상세 이미지 URL"),
                        fieldWithPath("share_rectangle_image_url").type(JsonFieldType.STRING).description("공유용 사각형 이미지 URL"),
                        fieldWithPath("share_square_image_url").type(JsonFieldType.STRING).description("공유용 정사각형 이미지 URL"),
                        fieldWithPath("banner_image_url").type(JsonFieldType.STRING).description("배너 이미지 URL(Drip 전용)").optional(),
                        fieldWithPath("join_count").type(JsonFieldType.NUMBER).description("이벤트 참여수"),
                        fieldWithPath("need_point").type(JsonFieldType.NUMBER).description("이벤트 참여시 필요 포인트"),
                        fieldWithPath("start_at").type(JsonFieldType.STRING).description("이벤트 시작일시").attributes(getZonedDateFormat()),
                        fieldWithPath("end_at").type(JsonFieldType.STRING).description("이벤트 종료일시").attributes(getZonedDateFormat()),
                        fieldWithPath("reservation_at").type(JsonFieldType.STRING).description("이벤트 예약일시").attributes(getZonedDateFormat()).optional(),
                        fieldWithPath("created_at").type(JsonFieldType.STRING).description("이벤트 등록일시").attributes(getZonedDateFormat()),
                        fieldWithPath("product").type(JsonFieldType.OBJECT).description("이벤트 상품 정보").optional(),
                        fieldWithPath("product.type").type(JsonFieldType.STRING).description(generateLinkCode(EVENT_PRODUCT_TYPE)),
                        fieldWithPath("product.price").type(JsonFieldType.NUMBER).description("상품 가격")
                )
            )
        );
    }

    @Test
    @Transactional
    @WithUserDetails(value = "1", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void editEvent() throws Exception {
        EventProductRequest productRequest = EventProductRequest.builder()
                .type(EventProductType.POINT)
                .price(3000)
                .build();

        EditEventRequest request = EditEventRequest.builder()
                .status(EventStatus.WAIT)
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
                .build();

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .put("/admin/event/{event_id}", 4)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_edit_event",
                        pathParameters(
                                parameterWithName("event_id").description("이벤트 ID")
                        ),
                        requestFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(EVENT_STATUS)),
                                fieldWithPath("is_visible").type(JsonFieldType.BOOLEAN).description("노출 여부"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("설명"),
                                fieldWithPath("need_point").type(JsonFieldType.NUMBER).description("이벤트 참가시 필요한 포인트"),
                                fieldWithPath("start_at").type(JsonFieldType.STRING).description("이벤트 시작일시").attributes(getZonedDateFormat()),
                                fieldWithPath("end_at").type(JsonFieldType.STRING).description("이벤트 종료일시").attributes(getZonedDateFormat()),
                                fieldWithPath("reservation_at").type(JsonFieldType.STRING).description("이벤트 예약일시").attributes(getZonedDateFormat()).optional(),
                                fieldWithPath("thumbnail_image_url").type(JsonFieldType.STRING).description("썸네일 이미지 URL"),
                                fieldWithPath("detail_image_url").type(JsonFieldType.STRING).description("상세 이미지 URL"),
                                fieldWithPath("share_rectangle_image_url").type(JsonFieldType.STRING).description("공유용 사각형 이미지 URL"),
                                fieldWithPath("share_square_image_url").type(JsonFieldType.STRING).description("공유용 정사각형 이미지 URL"),
                                fieldWithPath("banner_image_url").type(JsonFieldType.STRING).description("배너 이미지 URL(Drip 전용)").optional(),
                                fieldWithPath("product").type(JsonFieldType.OBJECT).description("이벤트 상품 정보").optional(),
                                fieldWithPath("product.type").type(JsonFieldType.STRING).description(generateLinkCode(EVENT_PRODUCT_TYPE)),
                                fieldWithPath("product.price").type(JsonFieldType.NUMBER).description("상품 가격")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("이벤트 ID"),
                                fieldWithPath("type").type(JsonFieldType.STRING).description(generateLinkCode(EVENT_TYPE)),
                                fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(EVENT_STATUS)),
                                fieldWithPath("is_visible").type(JsonFieldType.BOOLEAN).description("노출여부"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("설명"),
                                fieldWithPath("thumbnail_image_url").type(JsonFieldType.STRING).description("썸네일 이미지 URL"),
                                fieldWithPath("detail_image_url").type(JsonFieldType.STRING).description("상세 이미지 URL"),
                                fieldWithPath("share_rectangle_image_url").type(JsonFieldType.STRING).description("공유용 사각형 이미지 URL"),
                                fieldWithPath("share_square_image_url").type(JsonFieldType.STRING).description("공유용 정사각형 이미지 URL"),
                                fieldWithPath("banner_image_url").type(JsonFieldType.STRING).description("배너 이미지 URL(Drip 전용)").optional(),
                                fieldWithPath("join_count").type(JsonFieldType.NUMBER).description("이벤트 참여수"),
                                fieldWithPath("need_point").type(JsonFieldType.NUMBER).description("이벤트 참여시 필요 포인트"),
                                fieldWithPath("start_at").type(JsonFieldType.STRING).description("이벤트 시작일시").attributes(getZonedDateFormat()),
                                fieldWithPath("end_at").type(JsonFieldType.STRING).description("이벤트 종료일시").attributes(getZonedDateFormat()),
                                fieldWithPath("reservation_at").type(JsonFieldType.STRING).description("이벤트 예약일시").attributes(getZonedDateFormat()).optional(),
                                fieldWithPath("created_at").type(JsonFieldType.STRING).description("이벤트 등록일시").attributes(getZonedDateFormat()),
                                fieldWithPath("product").type(JsonFieldType.OBJECT).description("이벤트 상품 정보").optional(),
                                fieldWithPath("product.type").type(JsonFieldType.STRING).description(generateLinkCode(EVENT_PRODUCT_TYPE)),
                                fieldWithPath("product.price").type(JsonFieldType.NUMBER).description("상품 가격")
                        )
                )
        );
    }

    @Transactional
    @Test
    void deleteEvent() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .delete("/admin/event/{event_id}", 1))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_delete_event",
                pathParameters(
                        parameterWithName("event_id").description("이벤트 ID")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("이벤트 ID")
                )
        ));
    }
}
