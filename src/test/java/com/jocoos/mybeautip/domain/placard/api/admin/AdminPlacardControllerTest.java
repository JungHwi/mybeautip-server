package com.jocoos.mybeautip.domain.placard.api.admin;

import com.jocoos.mybeautip.domain.placard.dto.PatchPlacardRequest;
import com.jocoos.mybeautip.domain.placard.dto.PlacardRequest;
import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import com.jocoos.mybeautip.global.dto.single.SortOrderDto;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.domain.placard.code.PlacardLinkType.EVENT;
import static com.jocoos.mybeautip.domain.placard.code.PlacardStatus.ACTIVE;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.PLACARD_LINK_TYPE;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.PLACARD_STATUS;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminPlacardControllerTest extends RestDocsTestSupport {

    @Test
    void getPlacards() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/placard"))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_placards",
                requestParameters(
                        parameterWithName("status").description(generateLinkCode(PLACARD_STATUS)).optional(),
                        parameterWithName("page").description("페이지 넘버").optional().attributes(getDefault(1)),
                        parameterWithName("size").description("페이지 사이즈").optional().attributes(getDefault(10)),
                        parameterWithName("search").description("검색 - 검색 필드,검색어 / 가능한 검색 필드 - description").optional(),
                        parameterWithName("start_at").description("검색 시작일자").optional().attributes(getZonedDateFormat()),
                        parameterWithName("end_at").description("검색 종료일자").optional().attributes(getZonedDateFormat()),
                        parameterWithName("is_top_fix").description("상단 고정 여부 (boolean)").optional()
                ),
                responseFields(
                        fieldWithPath("total").type(JsonFieldType.NUMBER).description("총 플랜카드 개수"),
                        fieldWithPath("content").type(JsonFieldType.ARRAY).description("플랜카드 목록"),
                        fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("플랜카드 ID"),
                        fieldWithPath("content.[].status").type(JsonFieldType.STRING).description(generateLinkCode(PLACARD_STATUS)),
                        fieldWithPath("content.[].link_type").type(JsonFieldType.STRING).description(generateLinkCode(PLACARD_LINK_TYPE)),
                        fieldWithPath("content.[].image_url").type(JsonFieldType.STRING).description("이미지 Url"),
                        fieldWithPath("content.[].description").type(JsonFieldType.STRING).description("배너명 (플랜카드 설명)").optional(),
                        fieldWithPath("content.[].is_top_fix").type(JsonFieldType.BOOLEAN).description("고정 여부 (true 일 때만 응답)").optional(),
                        fieldWithPath("content.[].start_at").type(JsonFieldType.STRING).description("시작일시").attributes(getZonedDateFormat()),
                        fieldWithPath("content.[].end_at").type(JsonFieldType.STRING).description("종료일시").attributes(getZonedDateFormat()),
                        fieldWithPath("content.[].created_at").type(JsonFieldType.STRING).description("생성일시").attributes(getZonedDateFormat())
                )
        ));
    }

    @Test
    void getPlacard() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/placard/{placard_id}", 1))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_placard",
                pathParameters(
                        parameterWithName("placard_id").description("플랜카드 ID")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("플랜카드 ID"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(PLACARD_STATUS)),
                        fieldWithPath("title").type(JsonFieldType.STRING).description("백 타이틀"),
                        fieldWithPath("link_type").type(JsonFieldType.STRING).description(generateLinkCode(PLACARD_LINK_TYPE)),
                        fieldWithPath("link_argument").type(JsonFieldType.STRING).description("링크 연결 파라미터").optional(),
                        fieldWithPath("image_url").type(JsonFieldType.STRING).description("이미지 Url"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("배너명 (플랜카드 설명)").optional(),
                        fieldWithPath("color").type(JsonFieldType.STRING).description("색깔"),
                        fieldWithPath("is_top_fix").type(JsonFieldType.BOOLEAN).description("고정 여부 (true 일 때만 응답)").optional(),
                        fieldWithPath("start_at").type(JsonFieldType.STRING).description("시작일시").attributes(getZonedDateFormat()),
                        fieldWithPath("end_at").type(JsonFieldType.STRING).description("종료일시").attributes(getZonedDateFormat()),
                        fieldWithPath("created_at").type(JsonFieldType.STRING).description("생성일시").attributes(getZonedDateFormat())
                )
        ));
    }

    @Transactional
    @Test
    void createPlacard() throws Exception {

        PlacardRequest request = new PlacardRequest(ACTIVE,
                "https://static-dev.mybeautip.com/avatar/filename",
                "title",
                EVENT,
                "2",
                "description",
                "#color",
                ZonedDateTime.now(),
                ZonedDateTime.now().plusDays(1));

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/admin/placard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(print());

        result.andDo(document("admin_create_placard",
                requestFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(PLACARD_STATUS)),
                        fieldWithPath("image_url").type(JsonFieldType.STRING).description("이미지 URL"),
                        fieldWithPath("title").type(JsonFieldType.STRING).description("백 타이틀"),
                        fieldWithPath("link_type").type(JsonFieldType.STRING).description(generateLinkCode(PLACARD_LINK_TYPE)),
                        fieldWithPath("link_argument").type(JsonFieldType.STRING).description("링크 파라미터").optional(),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("배너명 (플랜카드 설명)").optional(),
                        fieldWithPath("color").type(JsonFieldType.STRING).description("색깔값 (RGB)"),
                        fieldWithPath("started_at").type(JsonFieldType.STRING).description("시작일시").attributes(getZonedDateFormat()),
                        fieldWithPath("ended_at").type(JsonFieldType.STRING).description("종료일시").attributes(getZonedDateFormat())
                ),
                responseHeaders(
                        headerWithName("Location").description("생성 자원 확인 가능 위치")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("플랜카드 ID"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(PLACARD_STATUS)),
                        fieldWithPath("link_type").type(JsonFieldType.STRING).description(generateLinkCode(PLACARD_LINK_TYPE)),
                        fieldWithPath("image_url").type(JsonFieldType.STRING).description("이미지 Url"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("배너명 (플랜카드 설명)").optional(),
                        fieldWithPath("start_at").type(JsonFieldType.STRING).description("시작일시").attributes(getZonedDateFormat()),
                        fieldWithPath("end_at").type(JsonFieldType.STRING).description("종료일시").attributes(getZonedDateFormat()),
                        fieldWithPath("created_at").type(JsonFieldType.STRING).description("생성일시").attributes(getZonedDateFormat())
                )
        ));
    }

    @Transactional
    @Test
    void editPlacard() throws Exception {
        PatchPlacardRequest request = PatchPlacardRequest
                .builder()
                .title(JsonNullable.of("수정"))
                .build();

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/admin/placard/{placard_id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_edit_placard",
                pathParameters(
                        parameterWithName("placard_id").description("플랜카드 ID")
                ),
                requestFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(PLACARD_STATUS)).optional(),
                        fieldWithPath("image_url").type(JsonFieldType.STRING).description("이미지 URL").optional(),
                        fieldWithPath("title").type(JsonFieldType.STRING).description("백 타이틀").optional(),
                        fieldWithPath("link_type").type(JsonFieldType.STRING).description(generateLinkCode(PLACARD_LINK_TYPE)).optional(),
                        fieldWithPath("link_argument").type(JsonFieldType.STRING).description("링크 파라미터").optional(),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("배너명 (플랜카드 설명)").optional(),
                        fieldWithPath("color").type(JsonFieldType.STRING).description("색깔값 (RGB)").optional(),
                        fieldWithPath("started_at").type(JsonFieldType.STRING).description("시작일시").attributes(getZonedDateFormat()).optional(),
                        fieldWithPath("ended_at").type(JsonFieldType.STRING).description("종료일시").attributes(getZonedDateFormat()).optional()
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("플랜카드 ID")
                )
        ));
    }

    @Transactional
    @Test
    void deletePlacard() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .delete("/admin/placard/{placard_id}", 1))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_delete_placard",
                pathParameters(
                        parameterWithName("placard_id").description("플랜카드 ID")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("플랜카드 ID")
                )
        ));
    }

    @Transactional
    @Test
    void fixPlacard() throws Exception {
        BooleanDto request = new BooleanDto(true);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/admin/placard/{placard_id}/fix", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_fix_placard",
                pathParameters(
                        parameterWithName("placard_id").description("플랜카드 ID")
                ),
                requestFields(
                        fieldWithPath("bool").type(JsonFieldType.BOOLEAN).description("상단 고정 여부")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("플랜카드 ID")
                )
        ));
    }

    @Transactional
    @Test
    void changeStatusOfPlacard() throws Exception {
        BooleanDto request = new BooleanDto(true);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/admin/placard/{placard_id}/status", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_change_status_placard",
                pathParameters(
                        parameterWithName("placard_id").description("플랜카드 ID")
                ),
                requestFields(
                        fieldWithPath("bool").type(JsonFieldType.BOOLEAN).description("공개 여부")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("플랜카드 ID")
                )
        ));
    }

    @Transactional
    @Test
    void changeOrderOfPlacards() throws Exception {
        SortOrderDto request = new SortOrderDto(List.of(3L, 4L, 5L));

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/admin/placard/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_change_order_placard",
                requestFields(
                        fieldWithPath("sorted_ids").type(JsonFieldType.ARRAY).description("정렬된 플랜카드 ID 리스트")
                ),
                responseFields(
                        fieldWithPath("sorted_ids").type(JsonFieldType.ARRAY).description("정렬된 플랜카드 ID 리스트")
                )
        ));
    }


}
