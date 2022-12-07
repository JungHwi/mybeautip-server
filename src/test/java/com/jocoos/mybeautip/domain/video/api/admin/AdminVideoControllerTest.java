package com.jocoos.mybeautip.domain.video.api.admin;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import com.jocoos.mybeautip.global.dto.single.SortOrderDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.*;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.MEMBER_STATUS;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminVideoControllerTest extends RestDocsTestSupport {


    @Test
    void getVideos() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/video"))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_videos",
                requestParameters(
                        parameterWithName("category_id").description("카테고리 아이디").optional(),
                        parameterWithName("page").description("페이지 넘버").optional().attributes(getDefault(1)),
                        parameterWithName("size").description("페이지 사이즈").optional().attributes(getDefault(10)),
                        parameterWithName("sort").description("정렬 필드").optional().attributes(getDefault("createdAt")),
                        parameterWithName("order").description("정렬 방향").optional().attributes(getDefault("DESC")),
                        parameterWithName("search").description("검색 - 검색필드,검색어").optional(),
                        parameterWithName("start_at").description("검색 시작일자").attributes(getLocalDateFormat()).optional(),
                        parameterWithName("end_at").description("검색 종료일자").attributes(getLocalDateFormat()).optional(),
                        parameterWithName("is_reported").description("신고 여부 (boolean)").optional(),
                        parameterWithName("is_top_fix").description("상단 고정 여부 (boolean)").optional(),
                        parameterWithName("is_recommended").description("추천 여부 (boolean)").optional()
                ),
                responseFields(
                        fieldWithPath("total").type(JsonFieldType.NUMBER).description("총 비디오 개수"),
                        fieldWithPath("content").type(JsonFieldType.ARRAY).description("비디오 목록"),
                        fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("비디오 ID"),
                        fieldWithPath("content.[].video_key").type(JsonFieldType.STRING).description("비디오키"),
                        fieldWithPath("content.[].url").type(JsonFieldType.STRING).description("비디오 Url"),
                        fieldWithPath("content.[].visibility").type(JsonFieldType.STRING).description("공개 여부"),
                        fieldWithPath("content.[].thumbnail_url").type(JsonFieldType.STRING).description("썸네일 URL"),
                        fieldWithPath("content.[].title").type(JsonFieldType.STRING).description("제목"),
                        fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("내용").optional(),
                        fieldWithPath("content.[].is_top_fix").type(JsonFieldType.BOOLEAN).description("상단 고정 여부").optional(),
                        fieldWithPath("content.[].is_recommended").type(JsonFieldType.BOOLEAN).description("추천 여부").optional(),
                        fieldWithPath("content.[].view_count").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("content.[].like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                        fieldWithPath("content.[].comment_count").type(JsonFieldType.NUMBER).description("댓글/대댓글수"),
                        fieldWithPath("content.[].report_count").type(JsonFieldType.NUMBER).description("신고수"),
                        fieldWithPath("content.[].created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                        fieldWithPath("content.[].duration").type(JsonFieldType.NUMBER).description("비디오 길이"),
                        fieldWithPath("content.[].member").type(JsonFieldType.OBJECT).description("작성자 정보."),
                        fieldWithPath("content.[].member.id").type(JsonFieldType.NUMBER).description("작성자 아이디"),
                        fieldWithPath("content.[].member.status").type(JsonFieldType.STRING).description(generateLinkCode(MEMBER_STATUS)),
                        fieldWithPath("content.[].member.username").type(JsonFieldType.STRING).description("작성자 이름"),
                        fieldWithPath("content.[].member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL"),
                        fieldWithPath("content.[].category").type(JsonFieldType.ARRAY).description("카테고리 정보"),
                        fieldWithPath("content.[].category.[].id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                        fieldWithPath("content.[].category.[].title").type(JsonFieldType.STRING).description("카테고리 제목")
                )
        ));
    }

    @Test
    void getVideo() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/video/{video_id}", 3))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_video",
                pathParameters(
                        parameterWithName("video_id").description("비디오 ID")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("비디오 ID"),
                        fieldWithPath("video_key").type(JsonFieldType.STRING).description("비디오키"),
                        fieldWithPath("url").type(JsonFieldType.STRING).description("비디오 Url"),
                        fieldWithPath("visibility").type(JsonFieldType.STRING).description("공개 여부"),
                        fieldWithPath("thumbnail_url").type(JsonFieldType.STRING).description("썸네일 URL"),
                        fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("내용").optional(),
                        fieldWithPath("is_top_fix").type(JsonFieldType.BOOLEAN).description("상단 고정 여부").optional(),
                        fieldWithPath("is_recommended").type(JsonFieldType.BOOLEAN).description("추천 여부").optional(),
                        fieldWithPath("view_count").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                        fieldWithPath("comment_count").type(JsonFieldType.NUMBER).description("댓글/대댓글수"),
                        fieldWithPath("report_count").type(JsonFieldType.NUMBER).description("신고수"),
                        fieldWithPath("created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                        fieldWithPath("duration").type(JsonFieldType.NUMBER).description("비디오 길이"),
                        fieldWithPath("member").type(JsonFieldType.OBJECT).description("작성자 정보."),
                        fieldWithPath("member.id").type(JsonFieldType.NUMBER).description("작성자 아이디"),
                        fieldWithPath("member.status").type(JsonFieldType.STRING).description(generateLinkCode(MEMBER_STATUS)),
                        fieldWithPath("member.username").type(JsonFieldType.STRING).description("작성자 이름"),
                        fieldWithPath("member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL"),
                        fieldWithPath("category").type(JsonFieldType.ARRAY).description("카테고리 정보"),
                        fieldWithPath("category.[].id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                        fieldWithPath("category.[].title").type(JsonFieldType.STRING).description("카테고리 제목")
                )));
    }

    @Test
    @Transactional
    void hideVideo() throws Exception {
        BooleanDto request = new BooleanDto(true);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/admin/video/{video_id}/hide", 3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_hide_video",
                pathParameters(
                        parameterWithName("video_id").description("비디오 ID")
                ),
                requestFields(
                        fieldWithPath("bool").type(JsonFieldType.BOOLEAN).description("숨김 여부")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("비디오 ID")
                )));
    }

    @Test
    @Transactional
    void deleteVideo() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .delete("/admin/video/{video_id}", 3))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_delete_video",
                pathParameters(
                        parameterWithName("video_id").description("비디오 ID")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("비디오 ID")
                )));
    }

    @Test
    @Transactional
    void topFixVideo() throws Exception {
        BooleanDto request = new BooleanDto(true);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/admin/video/{video_id}/fix", 3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_fix_video",
                pathParameters(
                        parameterWithName("video_id").description("비디오 ID")
                ),
                requestFields(
                        fieldWithPath("bool").type(JsonFieldType.BOOLEAN).description("상단 고정 여부")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("비디오 ID")
                )));
    }

    @Test
    @Transactional
    void changeOrderVideo() throws Exception {
        List<Long> ids = List.of(5L, 4L, 3L);
        SortOrderDto request = new SortOrderDto(ids);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/admin/video/order", 3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_change_order_video",
                requestFields(
                        fieldWithPath("sorted_ids").type(JsonFieldType.ARRAY).description("상단 고정된 비디오 순서")
                ),
                responseFields(
                        fieldWithPath("sorted_ids").type(JsonFieldType.ARRAY).description("상단 고정된 비디오 순서")
                )));
    }
}
