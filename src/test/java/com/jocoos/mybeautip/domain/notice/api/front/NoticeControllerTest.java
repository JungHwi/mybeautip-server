package com.jocoos.mybeautip.domain.notice.api.front;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.FILE_TYPE;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.NOTICE_STATUS;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NoticeControllerTest extends RestDocsTestSupport {

    @Test
    void list() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/notice")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("get_notices",
                        requestParameters(
                                parameterWithName("cursor").description("커서").optional(),
                                parameterWithName("size").description("페이지 사이즈").attributes(getDefault(20)).optional()
                        ),
                        responseFields(
                                fieldWithPath("next_cursor").type(JsonFieldType.STRING).description("커서 정보"),
                                fieldWithPath("content").type(JsonFieldType.ARRAY).description("조회 결과 목록"),
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("글 ID"),
                                fieldWithPath("content.[].status").type(JsonFieldType.STRING).description(generateLinkCode(NOTICE_STATUS)),
                                fieldWithPath("content.[].is_important").type(JsonFieldType.BOOLEAN).description("중요 공지 여부").optional(),
                                fieldWithPath("content.[].view_count").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("content.[].title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("content.[].files").type(JsonFieldType.ARRAY).description("파일 List").optional(),
                                fieldWithPath("content.[].files.[].type").type(JsonFieldType.STRING).description(generateLinkCode(FILE_TYPE)),
                                fieldWithPath("content.[].files.[].url").type(JsonFieldType.STRING).description("파일 URL"),
                                fieldWithPath("content.[].created_at").type(JsonFieldType.STRING).description("작성 일시").attributes(getZonedDateFormat()),
                                fieldWithPath("content.[].created_by").type(JsonFieldType.OBJECT).description("작성자 정보"),
                                fieldWithPath("content.[].created_by.id").type(JsonFieldType.NUMBER).description("작성자 아이디"),
                                fieldWithPath("content.[].created_by.email").type(JsonFieldType.STRING).description("작성자 이메일"),
                                fieldWithPath("content.[].created_by.username").type(JsonFieldType.STRING).description("작성자 닉네임"),
                                fieldWithPath("content.[].created_by.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL"),
                                fieldWithPath("content.[].cursor").ignored()
                        )
                )
        );
    }

    @Test
    void get() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/notice/{noticeId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("get_notice",
                        pathParameters(
                                parameterWithName("noticeId").description("공지 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("글 ID"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(NOTICE_STATUS)),
                                fieldWithPath("is_important").type(JsonFieldType.BOOLEAN).description("중요 공지 여부").optional(),
                                fieldWithPath("view_count").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("files").type(JsonFieldType.ARRAY).description("파일 List").optional(),
                                fieldWithPath("files.[].type").type(JsonFieldType.STRING).description(generateLinkCode(FILE_TYPE)),
                                fieldWithPath("files.[].url").type(JsonFieldType.STRING).description("파일 URL"),
                                fieldWithPath("modified_at").type(JsonFieldType.STRING).description("수정 일시").attributes(getZonedDateFormat()),
                                fieldWithPath("modified_by").type(JsonFieldType.OBJECT).description("수정자 정보"),
                                fieldWithPath("modified_by.id").type(JsonFieldType.NUMBER).description("수정자 아이디"),
                                fieldWithPath("modified_by.email").type(JsonFieldType.STRING).description("수정자 이메일"),
                                fieldWithPath("modified_by.username").type(JsonFieldType.STRING).description("수정자 닉네임"),
                                fieldWithPath("modified_by.avatar_url").type(JsonFieldType.STRING).description("수정자 아바타 URL"),
                                fieldWithPath("created_at").type(JsonFieldType.STRING).description("작성 일시").attributes(getZonedDateFormat()),
                                fieldWithPath("created_by").type(JsonFieldType.OBJECT).description("작성자 정보"),
                                fieldWithPath("created_by.id").type(JsonFieldType.NUMBER).description("작성자 아이디"),
                                fieldWithPath("created_by.email").type(JsonFieldType.STRING).description("작성자 이메일"),
                                fieldWithPath("created_by.username").type(JsonFieldType.STRING).description("작성자 닉네임"),
                                fieldWithPath("created_by.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL")
                        )
                )
        );
    }
}