package com.jocoos.mybeautip.domain.community.api.admin;

import com.jocoos.mybeautip.domain.community.dto.WriteCommunityCommentRequest;
import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminCommunityCommentControllerTest extends RestDocsTestSupport {

    @Test
    @WithUserDetails(value = "1", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Transactional
    void writeComment() throws Exception {
        WriteCommunityCommentRequest request = WriteCommunityCommentRequest.builder()
                .contents("Mock Comment Contents")
                .build();

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/admin/community/{community_id}/comment", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(print());

        result.andDo(document("admin_write_community_comment",
                        pathParameters(
                                parameterWithName("community_id").description("글 ID")
                        ),
                        requestFields(
                                fieldWithPath("parent_id").type(JsonFieldType.NUMBER).description("부모 댓글 아이디").optional(),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("댓글 아이디"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                                fieldWithPath("report_count").type(JsonFieldType.NUMBER).description("신고수"),
                                fieldWithPath("created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                                fieldWithPath("member").type(JsonFieldType.OBJECT).description("작성자 정보"),
                                fieldWithPath("member.id").type(JsonFieldType.NUMBER).description("작성자 아이디").optional(),
                                fieldWithPath("member.status").type(JsonFieldType.STRING).description(generateLinkCode(MEMBER_STATUS)),
                                fieldWithPath("member.username").type(JsonFieldType.STRING).description("작성자 이름").optional(),
                                fieldWithPath("member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional(),
                                fieldWithPath("member.role").type(JsonFieldType.STRING).description(generateLinkCode(ROLE)).optional()
                        )
                )
        );
    }

    @Test
    @WithUserDetails(value = "1", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Transactional
    void editComment() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("contents", "Test Contents");

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/admin/community/{community_id}/comment/{comment_id}", 1, 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_edit_community_comment",
                        pathParameters(
                                parameterWithName("community_id").description("글 ID"),
                                parameterWithName("comment_id").description("댓글 ID")
                        ),
                        requestFields(
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("내용")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("댓글 ID")
                        )
                )
        );
    }

    @Test
    void getCommunityComments() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/community/{community_id}/comment", 1))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_community_comments",
                pathParameters(
                        parameterWithName("community_id").description("글 ID")
                ),
                requestParameters(
                        parameterWithName("page").description("페이지 넘버").optional().attributes(getDefault(1)),
                        parameterWithName("size").description("페이지 사이즈").optional().attributes(getDefault(10))
                ),
                responseFields(
                        fieldWithPath("total").type(JsonFieldType.NUMBER).description("총 커뮤니티 댓글 개수"),
                        fieldWithPath("content").type(JsonFieldType.ARRAY).description("커뮤니티 댓글 목록"),
                        fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("커뮤니티 댓글 ID"),
                        fieldWithPath("content.[].status").type(JsonFieldType.STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                        fieldWithPath("content.[].contents").type(JsonFieldType.STRING).description("내용"),
                        fieldWithPath("content.[].like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                        fieldWithPath("content.[].report_count").type(JsonFieldType.NUMBER).description("신고수"),
                        fieldWithPath("content.[].member").type(JsonFieldType.OBJECT).description("작성자 정보.").optional(),
                        fieldWithPath("content.[].member.id").type(JsonFieldType.NUMBER).description("작성자 아이디").optional(),
                        fieldWithPath("content.[].member.status").type(JsonFieldType.STRING).description(generateLinkCode(MEMBER_STATUS)).optional(),
                        fieldWithPath("content.[].member.username").type(JsonFieldType.STRING).description("작성자 이름").optional(),
                        fieldWithPath("content.[].member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional(),
                        fieldWithPath("content.[].member.role").type(JsonFieldType.STRING).description(generateLinkCode(ROLE)).optional(),
                        fieldWithPath("content.[].created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),

                        fieldWithPath("content.[].children").type(JsonFieldType.ARRAY).description("대댓글 목록, children 필드가 없는 것을 제외하고 본 응답과 동일").optional(),
                        fieldWithPath("content.[].children.[].id").type(JsonFieldType.NUMBER).description("커뮤니티 댓글 ID").optional().ignored(),
                        fieldWithPath("content.[].children.[].status").type(JsonFieldType.STRING).description(generateLinkCode(COMMUNITY_STATUS)).optional().ignored(),
                        fieldWithPath("content.[].children.[].contents").type(JsonFieldType.STRING).description("내용").optional().ignored(),
                        fieldWithPath("content.[].children.[].like_count").type(JsonFieldType.NUMBER).description("좋아요수").optional().ignored(),
                        fieldWithPath("content.[].children.[].report_count").type(JsonFieldType.NUMBER).description("신고수").optional().ignored(),
                        fieldWithPath("content.[].children.[].member").type(JsonFieldType.OBJECT).description("작성자 정보.").optional().ignored(),
                        fieldWithPath("content.[].children.[].member.id").type(JsonFieldType.NUMBER).description("작성자 아이디").optional().ignored(),
                        fieldWithPath("content.[].children.[].member.status").type(JsonFieldType.STRING).description(generateLinkCode(MEMBER_STATUS)).optional().ignored(),
                        fieldWithPath("content.[].children.[].member.username").type(JsonFieldType.STRING).description("작성자 이름").optional().ignored(),
                        fieldWithPath("content.[].children.[].member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional().ignored(),
                        fieldWithPath("content.[].children.[].member.role").type(JsonFieldType.STRING).description(generateLinkCode(ROLE)).optional().ignored(),
                        fieldWithPath("content.[].children.[].created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()).optional().ignored()
                )
        ));
    }


    @Transactional
    @Test
    void hideCommunity() throws Exception {
        BooleanDto request = new BooleanDto(true);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/admin/community/comment/{comment_id}/hide", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_hide_community_comment",
                pathParameters(
                        parameterWithName("comment_id").description("커뮤니티 댓글 ID")
                ),
                requestFields(
                        fieldWithPath("bool").type(JsonFieldType.BOOLEAN).description("숨김 처리 여부")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("커뮤니티 댓글 ID")
                )
        ));
    }

}
