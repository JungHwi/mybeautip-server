package com.jocoos.mybeautip.domain.event.api.front;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EventControllerTest extends RestDocsTestSupport {

    @Test
    void getEventList() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/event")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("get_events",
                        requestParameters(
                                parameterWithName("event_type").description("이벤트 타입").optional()
                        ),
                        responseFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("이벤트 목록"),
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("이벤트 ID"),
                                fieldWithPath("[].type").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.EVENT_TYPE)),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.EVENT_STATUS)),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("[].thumbnail_image_url").type(JsonFieldType.STRING).description("썸네일 이미지 URL"),
                                fieldWithPath("[].banner_image_url").type(JsonFieldType.STRING).description("배너 이미지 URL").optional(),
                                fieldWithPath("[].start_at").type(JsonFieldType.STRING).description("이벤트 시작일시").attributes(getZonedDateFormat()),
                                fieldWithPath("[].end_at").type(JsonFieldType.STRING).description("이벤트 종료일시").attributes(getZonedDateFormat()).optional()
                        )
                )
        );
    }

    @Test
    void getEvent() throws Exception {

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/event/{event_id}", 3)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("get_event",
                        pathParameters(
                                parameterWithName("event_id").description("이벤트 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("이벤트 ID"),
                                fieldWithPath("type").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.EVENT_TYPE)),
                                fieldWithPath("relation_id").type(JsonFieldType.NUMBER).description("관련된 아이디. 현재는 Community Category 의 ID 밖에 없음.").optional(),
                                fieldWithPath("status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.EVENT_STATUS)),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("설명").optional(),
                                fieldWithPath("image_url").type(JsonFieldType.STRING).description("메인 이미지 URL"),
                                fieldWithPath("banner_image_url").type(JsonFieldType.STRING).description("배너 이미지").optional(),
                                fieldWithPath("share_square_image_url").type(JsonFieldType.STRING).description("배너 이미지").optional(),
                                fieldWithPath("share_rectangle_image_url").type(JsonFieldType.STRING).description("배너 이미지").optional(),
                                fieldWithPath("need_point").type(JsonFieldType.NUMBER).description("이벤트 참가시 필요한 포인트"),
                                fieldWithPath("start_at").type(JsonFieldType.STRING).description("이벤트 시작일시").attributes(getZonedDateFormat()),
                                fieldWithPath("end_at").type(JsonFieldType.STRING).description("이벤트 종료일시").attributes(getZonedDateFormat()),
                                fieldWithPath("event_product_list").type(JsonFieldType.ARRAY).description("이벤트 상품 목록").optional(),
                                fieldWithPath("event_product_list.[].id").type(JsonFieldType.NUMBER).description("이벤트 상품 아이디"),
                                fieldWithPath("event_product_list.[].type").type(JsonFieldType.STRING).description("이벤트 상품 구분"),
                                fieldWithPath("event_product_list.[].name").type(JsonFieldType.STRING).description("이벤트 상품명"),
                                fieldWithPath("event_product_list.[].image_url").type(JsonFieldType.STRING).description("이벤트 상품 이미지 URL").optional()
                        )
                )
        );
    }
}
