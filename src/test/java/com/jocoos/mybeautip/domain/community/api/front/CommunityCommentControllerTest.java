package com.jocoos.mybeautip.domain.community.api.front;

import com.jocoos.mybeautip.domain.community.dto.ReportRequest;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityCommentRequest;
import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;

import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat;
import static com.jocoos.mybeautip.global.constant.MybeautipConstant.MAX_LONG_STRING;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommunityCommentControllerTest extends RestDocsTestSupport {

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void getComments() throws Exception {

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/community/{community_id}/comment", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("get_community_comments",
                        pathParameters(
                                parameterWithName("community_id").description("글 ID")
                        ),
                        requestParameters(
                                parameterWithName("parent_id").description("부모 댓글 아이디").optional(),
                                parameterWithName("cursor").description("커서").optional().attributes(getDefault(MAX_LONG_STRING)),
                                parameterWithName("size").description("페이지 사이즈").optional().attributes(getDefault(20)),
                                parameterWithName("direction").description("정렬 방향").optional().attributes(getDefault("DESC"))
                        ),
                        responseFields(
                                fieldWithPath("next_cursor").type(JsonFieldType.STRING).description("커서 정보"),
                                fieldWithPath("content").type(JsonFieldType.ARRAY).description("커뮤니티 댓글 목록"),
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("댓글 아이디"),
                                fieldWithPath("content.[].category_id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("content.[].community_id").type(JsonFieldType.NUMBER).description("커뮤니티 아이디"),
                                fieldWithPath("content.[].parent_id").type(JsonFieldType.NUMBER).description("부모 댓글 아이디").optional(),
                                fieldWithPath("content.[].status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_STATUS)),
                                fieldWithPath("content.[].contents").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("content.[].like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                                fieldWithPath("content.[].comment_count").type(JsonFieldType.NUMBER).description("대댓글수"),
                                fieldWithPath("content.[].report_count").type(JsonFieldType.NUMBER).description("대댓글수"),
                                fieldWithPath("content.[].created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                                fieldWithPath("content.[].relation_info").type(JsonFieldType.OBJECT).description("유저와의 관계 정보"),
                                fieldWithPath("content.[].relation_info.is_like").type(JsonFieldType.BOOLEAN).description("글 좋아요 여부"),
                                fieldWithPath("content.[].relation_info.is_block").type(JsonFieldType.BOOLEAN).description("작성자 차단 여부"),
                                fieldWithPath("content.[].relation_info.is_report").type(JsonFieldType.BOOLEAN).description("글 신고 여부"),
                                fieldWithPath("content.[].member").type(JsonFieldType.OBJECT).description("작성자 정보").optional(),
                                fieldWithPath("content.[].member.id").type(JsonFieldType.NUMBER).description("작성자 아이디"),
                                fieldWithPath("content.[].member.status").type(JsonFieldType.STRING).description("작성자 상태"),
                                fieldWithPath("content.[].member.username").type(JsonFieldType.STRING).description("작성자 이름"),
                                fieldWithPath("content.[].member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL")
                        )
                )
        );
    }

    @Test
    void getComment() throws Exception {

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/community/{community_id}/comment/{comment_id}", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("get_community_comment",
                        pathParameters(
                                parameterWithName("community_id").description("글 ID"),
                                parameterWithName("comment_id").description("댓글 ID")
                        ),
                        requestParameters(
                                parameterWithName("parent_id").description("부모 댓글 아이디").optional()
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("댓글 아이디"),
                                fieldWithPath("category_id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("community_id").type(JsonFieldType.NUMBER).description("커뮤니티 아이디"),
                                fieldWithPath("parent_id").type(JsonFieldType.NUMBER).description("부모 댓글 아이디").optional(),
                                fieldWithPath("status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_STATUS)),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("is_like").type(JsonFieldType.BOOLEAN).description("좋아요 여부").optional(),
                                fieldWithPath("like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                                fieldWithPath("comment_count").type(JsonFieldType.NUMBER).description("대댓글수"),
                                fieldWithPath("report_count").type(JsonFieldType.NUMBER).description("신고수"),
                                fieldWithPath("created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                                fieldWithPath("relation_info").type(JsonFieldType.OBJECT).description("유저와의 관계 정보"),
                                fieldWithPath("relation_info.is_like").type(JsonFieldType.BOOLEAN).description("글 좋아요 여부"),
                                fieldWithPath("relation_info.is_block").type(JsonFieldType.BOOLEAN).description("작성자 차단 여부"),
                                fieldWithPath("relation_info.is_report").type(JsonFieldType.BOOLEAN).description("글 신고 여부"),
                                fieldWithPath("member").type(JsonFieldType.OBJECT).description("작성자 정보").optional(),
                                fieldWithPath("member.id").type(JsonFieldType.NUMBER).description("작성자 아이디"),
                                fieldWithPath("member.status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.MEMBER_STATUS)),
                                fieldWithPath("member.username").type(JsonFieldType.STRING).description("작성자 이름"),
                                fieldWithPath("member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL")
                        )
                )
        );
    }

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void writeComment() throws Exception {
        WriteCommunityCommentRequest request = WriteCommunityCommentRequest.builder()
                .contents("Mock Comment Contents")
                .build();

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/api/1/community/{community_id}/comment", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("write_community_comment",
                        pathParameters(
                                parameterWithName("community_id").description("글 ID")
                        ),
                        requestFields(
                                fieldWithPath("parent_id").type(JsonFieldType.NUMBER).description("부모 댓글 아이디").optional(),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("댓글 아이디"),
                                fieldWithPath("category_id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("community_id").type(JsonFieldType.NUMBER).description("커뮤니티 아이디"),
                                fieldWithPath("parent_id").type(JsonFieldType.NUMBER).description("부모 댓글 아이디").optional(),
                                fieldWithPath("status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_STATUS)),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("is_like").type(JsonFieldType.BOOLEAN).description("좋아요 여부").optional(),
                                fieldWithPath("like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                                fieldWithPath("comment_count").type(JsonFieldType.NUMBER).description("대댓글수"),
                                fieldWithPath("report_count").type(JsonFieldType.NUMBER).description("신고수"),
                                fieldWithPath("created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                                fieldWithPath("relation_info").type(JsonFieldType.OBJECT).description("유저와의 관계 정보"),
                                fieldWithPath("relation_info.is_like").type(JsonFieldType.BOOLEAN).description("글 좋아요 여부"),
                                fieldWithPath("relation_info.is_block").type(JsonFieldType.BOOLEAN).description("작성자 차단 여부"),
                                fieldWithPath("relation_info.is_report").type(JsonFieldType.BOOLEAN).description("글 신고 여부"),
                                fieldWithPath("member").type(JsonFieldType.OBJECT).description("작성자 정보").optional(),
                                fieldWithPath("member.id").type(JsonFieldType.NUMBER).description("작성자 아이디"),
                                fieldWithPath("member.status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.MEMBER_STATUS)),
                                fieldWithPath("member.username").type(JsonFieldType.STRING).description("작성자 이름"),
                                fieldWithPath("member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL")
                        )
                )
        );
    }

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void editComment() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("contents", "Test Contents");

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .put("/api/1/community/{community_id}/comment/{comment_id}", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("edit_community_comment",
                        pathParameters(
                                parameterWithName("community_id").description("글 ID"),
                                parameterWithName("comment_id").description("댓글 ID")
                        ),
                        requestFields(
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                                fieldWithPath("category_id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("community_id").type(JsonFieldType.NUMBER).description("커뮤니티 아이디"),
                                fieldWithPath("parent_id").type(JsonFieldType.NUMBER).description("부모 댓글 아이디").optional(),
                                fieldWithPath("status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_STATUS)),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("is_like").type(JsonFieldType.BOOLEAN).description("좋아요 여부").optional(),
                                fieldWithPath("like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                                fieldWithPath("comment_count").type(JsonFieldType.NUMBER).description("대댓글수"),
                                fieldWithPath("report_count").type(JsonFieldType.NUMBER).description("신고수"),
                                fieldWithPath("created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                                fieldWithPath("relation_info").type(JsonFieldType.OBJECT).description("유저와의 관계 정보"),
                                fieldWithPath("relation_info.is_like").type(JsonFieldType.BOOLEAN).description("글 좋아요 여부"),
                                fieldWithPath("relation_info.is_block").type(JsonFieldType.BOOLEAN).description("작성자 차단 여부"),
                                fieldWithPath("relation_info.is_report").type(JsonFieldType.BOOLEAN).description("글 신고 여부"),
                                fieldWithPath("member").type(JsonFieldType.OBJECT).description("작성자 정보").optional(),
                                fieldWithPath("member.id").type(JsonFieldType.NUMBER).description("작성자 아이디"),
                                fieldWithPath("member.status").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.MEMBER_STATUS)),
                                fieldWithPath("member.username").type(JsonFieldType.STRING).description("작성자 이름"),
                                fieldWithPath("member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL")

                        )
                )
        );
    }

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void deleteComment() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .delete("/api/1/community/{community_id}/comment/{comment_id}", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("delete_community_comment",
                        pathParameters(
                                parameterWithName("community_id").description("글 ID"),
                                parameterWithName("comment_id").description("댓글 ID")
                        )
                )
        );
    }

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void likeComment() throws Exception {
        BooleanDto bool = new BooleanDto(true);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/api/1/community/{community_id}/comment/{comment_id}/like", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bool)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("like_community_comment",
                pathParameters(
                        parameterWithName("community_id").description("글 ID"),
                        parameterWithName("comment_id").description("댓글 ID")
                ),
                requestFields(
                        fieldWithPath("bool").type(JsonFieldType.BOOLEAN).description("좋아요 여부")
                ),
                responseFields(
                        fieldWithPath("is_like").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                        fieldWithPath("like_count").type(JsonFieldType.NUMBER).description("좋아요 수")
                ))
        );
    }

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void reportComment() throws Exception {
        ReportRequest report = ReportRequest.builder()
                .isReport(true)
                .description("신고사유")
                .build();

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/api/1/community/{community_id}/comment/{comment_id}/report", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("report_community_comment",
                pathParameters(
                        parameterWithName("community_id").description("글 ID"),
                        parameterWithName("comment_id").description("댓글 ID")
                ),
                requestFields(
                        fieldWithPath("is_report").type(JsonFieldType.BOOLEAN).description("신고 여부"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("신고 사유. 신고여부가 true 일때만 필수").optional()
                ),
                responseFields(
                        fieldWithPath("is_report").type(JsonFieldType.BOOLEAN).description("신고 여부"),
                        fieldWithPath("report_count").type(JsonFieldType.NUMBER).description("신고수")
                ))
        );
    }

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void isReportComment() throws Exception {
        ReportRequest report = ReportRequest.builder()
                .isReport(true)
                .description("신고사유")
                .build();

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/community/{community_id}/comment/{comment_id}/report", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("check_report_community_comment",
                pathParameters(
                        parameterWithName("community_id").description("글 ID"),
                        parameterWithName("comment_id").description("댓글 ID")
                ),
                requestFields(
                        fieldWithPath("is_report").type(JsonFieldType.BOOLEAN).description("신고 여부"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("신고 사유. 신고여부가 true 일때만 필수").optional()
                ),
                responseFields(
                        fieldWithPath("is_report").type(JsonFieldType.BOOLEAN).description("신고 여부"),
                        fieldWithPath("report_count").type(JsonFieldType.NUMBER).description("신고수")
                ))
        );
    }
}