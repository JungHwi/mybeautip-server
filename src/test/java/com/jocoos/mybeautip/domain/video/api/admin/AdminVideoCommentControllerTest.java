package com.jocoos.mybeautip.domain.video.api.admin;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminVideoCommentControllerTest extends RestDocsTestSupport {

    @Test
    void getVideoComments() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/video/{video_id}/comment", 3))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_video_comments",
                pathParameters(
                        parameterWithName("video_id").description("비디오 ID")
                ),
                requestParameters(
                        parameterWithName("page").description("페이지 넘버").optional().attributes(getDefault(1)),
                        parameterWithName("size").description("페이지 사이즈").optional().attributes(getDefault(10))
                ),
                responseFields(
                        fieldWithPath("total").type(JsonFieldType.NUMBER).description("총 비디오 댓글 개수"),
                        fieldWithPath("content").type(JsonFieldType.ARRAY).description("비디오 댓글 목록"),
                        fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("비디오 댓글 ID"),
                        fieldWithPath("content.[].status").type(JsonFieldType.STRING).description(generateLinkCode(VIDEO_COMMENT_STATUS)),
                        fieldWithPath("content.[].contents").type(JsonFieldType.STRING).description("내용"),
                        fieldWithPath("content.[].like_count").type(JsonFieldType.NUMBER).description("좋아요수"),
                        fieldWithPath("content.[].report_count").type(JsonFieldType.NUMBER).description("신고수"),
                        fieldWithPath("content.[].member").type(JsonFieldType.OBJECT).description("작성자 정보."),
                        fieldWithPath("content.[].member.id").type(JsonFieldType.NUMBER).description("작성자 아이디"),
                        fieldWithPath("content.[].member.status").type(JsonFieldType.STRING).description(generateLinkCode(MEMBER_STATUS)),
                        fieldWithPath("content.[].member.username").type(JsonFieldType.STRING).description("작성자 이름"),
                        fieldWithPath("content.[].member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL"),
                        fieldWithPath("content.[].created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),

                        fieldWithPath("content.[].children").type(JsonFieldType.ARRAY).description("대댓글 목록, children 필드가 없는 것을 제외하고 본 응답과 동일").optional(),
                        fieldWithPath("content.[].children.[].id").type(JsonFieldType.NUMBER).description("비디오 댓글 ID").optional().ignored(),
                        fieldWithPath("content.[].children.[].status").type(JsonFieldType.STRING).description(generateLinkCode(VIDEO_COMMENT_STATUS)).optional().ignored(),
                        fieldWithPath("content.[].children.[].contents").type(JsonFieldType.STRING).description("내용").optional().ignored(),
                        fieldWithPath("content.[].children.[].like_count").type(JsonFieldType.NUMBER).description("좋아요수").optional().ignored(),
                        fieldWithPath("content.[].children.[].report_count").type(JsonFieldType.NUMBER).description("신고수").optional().ignored(),
                        fieldWithPath("content.[].children.[].member").type(JsonFieldType.OBJECT).description("작성자 정보.").optional().ignored(),
                        fieldWithPath("content.[].children.[].member.id").type(JsonFieldType.NUMBER).description("작성자 아이디").optional().ignored(),
                        fieldWithPath("content.[].children.[].member.status").type(JsonFieldType.STRING).description(generateLinkCode(MEMBER_STATUS)).optional().ignored(),
                        fieldWithPath("content.[].children.[].member.username").type(JsonFieldType.STRING).description("작성자 이름").optional().ignored(),
                        fieldWithPath("content.[].children.[].member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional().ignored(),
                        fieldWithPath("content.[].children.[].created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()).optional().ignored()
                )
        ));
    }

    @Test
    @Transactional
    void hideVideoComment() throws Exception {
        BooleanDto request = new BooleanDto(true);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .patch("/admin/video/comment/{comment_id}/hide", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_hide_video_comment",
                pathParameters(
                        parameterWithName("comment_id").description("비디오 댓글 ID")
                ),
                requestFields(
                        fieldWithPath("bool").type(JsonFieldType.BOOLEAN).description("숨김 여부")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("비디오 댓글 ID")
                )));
    }


}