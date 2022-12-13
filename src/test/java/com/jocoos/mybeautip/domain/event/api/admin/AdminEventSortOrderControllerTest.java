package com.jocoos.mybeautip.domain.event.api.admin;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.dto.SortOrderDto;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminEventSortOrderControllerTest extends RestDocsTestSupport {


    @Transactional
    @Test
    void fixEvent() throws Exception {
        BooleanDto request = new BooleanDto(true);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/admin/event/{event_id}/fix", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_fix_event",
                pathParameters(
                        parameterWithName("event_id").description("이벤트 ID")
                ),
                requestFields(
                        fieldWithPath("bool").type(JsonFieldType.BOOLEAN).description("상단 고정 여부")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("이벤트 ID")
                )
        ));
    }

    @Transactional
    @Test
    void changeOrderOfEvent() throws Exception {
        SortOrderDto request = new SortOrderDto(List.of(1L, 2L, 3L));

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/admin/event/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_change_order_event",
                requestFields(
                        fieldWithPath("sorted_ids").type(JsonFieldType.ARRAY).description("정렬된 이벤트 ID 리스트")
                ),
                responseFields(
                        fieldWithPath("sorted_ids").type(JsonFieldType.ARRAY).description("정렬된 이벤트 ID 리스트")
                )
        ));
    }
}
