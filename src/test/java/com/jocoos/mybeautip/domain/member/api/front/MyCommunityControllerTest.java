package com.jocoos.mybeautip.domain.member.api.front;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;

import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.FILE_TYPE;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode;
import static com.jocoos.mybeautip.global.constant.MybeautipConstant.MAX_LONG_STRING;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MyCommunityControllerTest extends RestDocsTestSupport {


    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void getMyCommunities() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/my/community")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("get_my_communities",
                        requestParameters(
                                parameterWithName("cursor").description("커서").optional().attributes(getDefault(MAX_LONG_STRING)),
                                parameterWithName("size").description("페이지 사이즈").optional().attributes(getDefault(20))
                        ),
                        responseFields(
                                fieldWithPath("next_cursor").type(JsonFieldType.STRING).description("커서 정보"),
                                fieldWithPath("content").type(JsonFieldType.ARRAY).description("커뮤니티 글 목록"),
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("글 ID"),
                                fieldWithPath("content.[].is_win").type(JsonFieldType.BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                                fieldWithPath("content.[].status").type(JsonFieldType.STRING).description(generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_STATUS)),
                                fieldWithPath("content.[].title").type(JsonFieldType.STRING).description("제목").optional(),
                                fieldWithPath("content.[].contents").type(JsonFieldType.STRING).description("내용").optional(),
                                fieldWithPath("content.[].file").type(JsonFieldType.OBJECT).description("메인 파일").optional(),
                                fieldWithPath("content.[].file.type").type(JsonFieldType.STRING).description(generateLinkCode(FILE_TYPE)),
                                fieldWithPath("content.[].file.thumbnail_url").type(JsonFieldType.STRING).description("메인 파일 썸네일 URL").optional(),
                                fieldWithPath("content.[].file.url").type(JsonFieldType.STRING).description("메인 파일 URL").optional(),
                                fieldWithPath("content.[].comment_count").type(JsonFieldType.NUMBER).description("댓글/대댓글수"),
                                fieldWithPath("content.[].report_count").type(JsonFieldType.NUMBER).description("신고수"),
                                fieldWithPath("content.[].created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat()),
                                fieldWithPath("content.[].category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                                fieldWithPath("content.[].category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("content.[].category.type").type(JsonFieldType.STRING).description("카테고리 구분").description(generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_CATEGORY_TYPE)),
                                fieldWithPath("content.[].category.title").type(JsonFieldType.STRING).description("카테고리 제목"),
                                fieldWithPath("content.[].category.hint").type(JsonFieldType.STRING).description("카테고리 힌트")
                        )
                )
        );
    }

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void getMyCommunityComments() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/my/community/comment")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("get_my_community_comments",
                        requestParameters(
                                parameterWithName("cursor").description("커서").optional().attributes(getDefault(MAX_LONG_STRING)),
                                parameterWithName("size").description("페이지 사이즈").optional().attributes(getDefault(20))
                        ),
                        responseFields(
                                fieldWithPath("next_cursor").type(JsonFieldType.STRING).description("커서 정보"),
                                fieldWithPath("content").type(JsonFieldType.ARRAY).description("커뮤니티 댓글 목록"),
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("댓글 아이디"),
                                fieldWithPath("content.[].category_id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("content.[].community_id").type(JsonFieldType.NUMBER).description("커뮤니티 아이디"),
                                fieldWithPath("content.[].parent_id").type(JsonFieldType.NUMBER).description("부모 댓글 아이디").optional(),
                                fieldWithPath("content.[].status").type(JsonFieldType.STRING).description(generateLinkCode(DocumentLinkGenerator.DocUrl.COMMUNITY_STATUS)),
                                fieldWithPath("content.[].contents").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("content.[].file_url").type(JsonFieldType.STRING).description("이미지 URL").optional(),
                                fieldWithPath("content.[].report_count").type(JsonFieldType.NUMBER).description("대댓글수"),
                                fieldWithPath("content.[].created_at").type(JsonFieldType.STRING).description("작성일").attributes(getZonedDateFormat())
                        )
                )
        );
    }
}
