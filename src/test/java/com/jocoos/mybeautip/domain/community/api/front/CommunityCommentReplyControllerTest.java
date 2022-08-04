package com.jocoos.mybeautip.domain.community.api.front;

import com.jocoos.mybeautip.domain.community.dto.ReportRequest;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityCommentRequest;
import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;

import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommunityCommentReplyControllerTest extends RestDocsTestSupport {

    @Test
    void getReplies() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/community/{community_id}/comment/{comment_id}/reply", 3, 2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("get_community_replies",
                        pathParameters(
                                parameterWithName("community_id").description("글 ID"),
                                parameterWithName("comment_id").description("댓글 ID")
                        ),
                        responseFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("대댓글 목록"),
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("대댓글 아이디"),
                                fieldWithPath("[].contents").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("[].is_like").type(JsonFieldType.BOOLEAN).description("좋아요 여부").optional(),
                                fieldWithPath("[].like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                                fieldWithPath("[].created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                                fieldWithPath("[].member").type(JsonFieldType.OBJECT).description("작성자 정보").optional(),
                                fieldWithPath("[].member.id").type(JsonFieldType.NUMBER).description("작성자 아이디"),
                                fieldWithPath("[].member.username").type(JsonFieldType.STRING).description("작성자 이름"),
                                fieldWithPath("[].member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL")
                        )
                )
        );
    }

    @Test
    void getReply() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/community/{community_id}/comment/{comment_id}/reply/{reply_id}", 3, 2, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("get_community_reply",
                        pathParameters(
                                parameterWithName("community_id").description("글 ID"),
                                parameterWithName("comment_id").description("댓글 ID"),
                                parameterWithName("reply_id").description("대댓글 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("대댓글 아이디"),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("is_like").type(JsonFieldType.BOOLEAN).description("좋아요 여부").optional(),
                                fieldWithPath("like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                                fieldWithPath("created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                                fieldWithPath("member").type(JsonFieldType.OBJECT).description("작성자 정보").optional(),
                                fieldWithPath("member.id").type(JsonFieldType.NUMBER).description("작성자 아이디"),
                                fieldWithPath("member.username").type(JsonFieldType.STRING).description("작성자 이름"),
                                fieldWithPath("member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL")
                        )
                )
        );
    }

    @Test
    void writeReply() throws Exception {
        WriteCommunityCommentRequest request = WriteCommunityCommentRequest.builder()
                .contents("Mock Reply Contents")
                .build();

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/api/1/community/{community_id}/comment/{comment_id}/reply", 3, 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("write_community_reply",
                        pathParameters(
                                parameterWithName("community_id").description("글 ID"),
                                parameterWithName("comment_id").description("댓글 ID")
                        ),
                        requestFields(
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("대댓글 아이디"),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("is_like").type(JsonFieldType.BOOLEAN).description("좋아요 여부").optional(),
                                fieldWithPath("like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                                fieldWithPath("created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                                fieldWithPath("member").type(JsonFieldType.OBJECT).description("작성자 정보").optional(),
                                fieldWithPath("member.id").type(JsonFieldType.NUMBER).description("작성자 아이디"),
                                fieldWithPath("member.username").type(JsonFieldType.STRING).description("작성자 이름"),
                                fieldWithPath("member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL")
                        )
                )
        );
    }

    @Test
    void editReply() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("contents", "Test Contents");

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .put("/api/1/community/{community_id}/comment/{comment_id}/reply/{reply_id}", 3, 2, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("edit_community_reply",
                        pathParameters(
                                parameterWithName("community_id").description("글 ID"),
                                parameterWithName("comment_id").description("댓글 ID"),
                                parameterWithName("reply_id").description("대댓글 ID")
                        ),
                        requestFields(
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("대댓글 ID"),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("is_like").type(JsonFieldType.BOOLEAN).description("좋아요 여부").optional(),
                                fieldWithPath("like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                                fieldWithPath("created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                                fieldWithPath("member").type(JsonFieldType.OBJECT).description("작성자 정보").optional(),
                                fieldWithPath("member.id").type(JsonFieldType.NUMBER).description("작성자 아이디"),
                                fieldWithPath("member.username").type(JsonFieldType.STRING).description("작성자 이름"),
                                fieldWithPath("member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL")

                        )
                )
        );
    }

    @Test
    void deleteReply() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .delete("/api/1/community/{community_id}/comment/{comment_id}/reply/{reply_id}", 3, 2, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("delete_community_reply",
                        pathParameters(
                                parameterWithName("community_id").description("글 ID"),
                                parameterWithName("comment_id").description("댓글 ID"),
                                parameterWithName("reply_id").description("대댓글 ID")
                        )
                )
        );
    }

    @Test
    void likeReply() throws Exception {
        BooleanDto bool = new BooleanDto(true);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/api/1/community/{community_id}/comment/{comment_id}/reply/{reply_id}/like", 3, 2, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bool)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("like_community_reply",
                pathParameters(
                        parameterWithName("community_id").description("글 ID"),
                        parameterWithName("comment_id").description("댓글 ID"),
                        parameterWithName("reply_id").description("대댓글 ID")
                ),
                requestFields(
                        fieldWithPath("bool").type(JsonFieldType.BOOLEAN).description("좋아요 여부")
                ))
        );
    }

    @Test
    void reportReply() throws Exception {
        ReportRequest report = ReportRequest.builder()
                .isReport(true)
                .description("신고사유")
                .build();

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/api/1/community/{community_id}/comment/{comment_id}/reply/{reply_id}/like", 3, 2, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("report_community_reply",
                pathParameters(
                        parameterWithName("community_id").description("글 ID"),
                        parameterWithName("comment_id").description("댓글 ID"),
                        parameterWithName("reply_id").description("대댓글 ID")
                ),
                requestFields(
                        fieldWithPath("is_report").type(JsonFieldType.BOOLEAN).description("신고 여부"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("신고 사유. 신고여부가 true 일때만 필수").optional()
                ))
        );
    }
}