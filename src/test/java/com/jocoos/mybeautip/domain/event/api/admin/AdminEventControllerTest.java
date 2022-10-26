package com.jocoos.mybeautip.domain.event.api.admin;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static com.jocoos.mybeautip.domain.event.code.SortField.CREATED_AT;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.*;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.EVENT_STATUS;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.SORT_FIELD;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
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
                                parameterWithName("status").description("이벤트 상태").optional(),
                                parameterWithName("page").attributes(getDefault(1)).description("페이지").optional(),
                                parameterWithName("size").attributes(getDefault(10)).description("페이지 크기").optional(),
                                parameterWithName("sort").attributes(getDefault(CREATED_AT)).description(generateLinkCode(SORT_FIELD)).optional(),
                                parameterWithName("order").attributes(getDefault("DESC")).description("정렬 방향").optional(),
                                parameterWithName("search").description("검색 필드,검색 키워드").optional(),
                                parameterWithName("startAt").description("검색 시작일").optional(),
                                parameterWithName("endAt").description("검색 종료").optional()
                        ),
                        responseFields(
                                fieldWithPath("total").type(JsonFieldType.NUMBER).description("총 개수"),
                                fieldWithPath("content").type(JsonFieldType.ARRAY).description("이벤트 목록"),
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("이벤트 ID"),
                                fieldWithPath("content.[].status").type(JsonFieldType.STRING).description(generateLinkCode(EVENT_STATUS)),
                                fieldWithPath("content.[].title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("content.[].thumbnail_image_url").type(JsonFieldType.STRING).description("썸네일 이미지 URL"),
                                fieldWithPath("content.[].join_count").type(JsonFieldType.NUMBER).description("참여수"),
                                fieldWithPath("content.[].point").type(JsonFieldType.NUMBER).description("이벤트 참여 포인트"),
                                fieldWithPath("content.[].start_at").type(JsonFieldType.STRING).description("이벤트 시작일시").attributes(getZonedDateFormat()),
                                fieldWithPath("content.[].end_at").type(JsonFieldType.STRING).description("이벤트 종료일시").attributes(getZonedDateFormat()),
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
                                fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(EVENT_STATUS)),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("thumbnail_image_url").type(JsonFieldType.STRING).description("썸네일 이미지 URL"),
                                fieldWithPath("detail_image_url").type(JsonFieldType.STRING).description("이벤트 상세 이미지 URL"),
                                fieldWithPath("banner_image_url").type(JsonFieldType.STRING).description("롤링배너 이미지 URL").optional(),
                                fieldWithPath("share_web_image_url").type(JsonFieldType.STRING).description("웹 공유 이미지 URL"),
                                fieldWithPath("share_sns_image_url").type(JsonFieldType.STRING).description("SNS 공유 이미지 URL"),
                                fieldWithPath("join_count").type(JsonFieldType.NUMBER).description("참여수"),
                                fieldWithPath("point").type(JsonFieldType.NUMBER).description("이벤트 참여 포인트"),
                                fieldWithPath("start_at").type(JsonFieldType.STRING).description("이벤트 시작일시").attributes(getZonedDateFormat()),
                                fieldWithPath("end_at").type(JsonFieldType.STRING).description("이벤트 종료일시").attributes(getZonedDateFormat()),
                                fieldWithPath("created_at").type(JsonFieldType.STRING).description("이벤트 생성일시").attributes(getZonedDateMilliFormat())
                        )
                )
        );
    }
}
