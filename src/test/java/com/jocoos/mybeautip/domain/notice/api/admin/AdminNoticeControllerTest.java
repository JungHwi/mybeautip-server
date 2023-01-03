package com.jocoos.mybeautip.domain.notice.api.admin;

import com.jocoos.mybeautip.domain.notice.dto.EditNoticeRequest;
import com.jocoos.mybeautip.domain.notice.dto.WriteNoticeRequest;
import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;
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

class AdminNoticeControllerTest extends RestDocsTestSupport {

    @Test
    @WithUserDetails(value = "1", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Transactional
    void write() throws Exception {
        WriteNoticeRequest request = WriteNoticeRequest.builder()
                .isVisible(true)
                .title("TEST TITLE")
                .description("TEST DESCRIPTION")
                .build();

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/admin/notice/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(print());

        result.andDo(document("admin_write_notice",
                requestFields(
                        fieldWithPath("is_visible").type(JsonFieldType.BOOLEAN).description("노출 여부"),
                        fieldWithPath("title").type(JsonFieldType.STRING).description("제목").optional(),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("상세 내용"),
                        fieldWithPath("files").type(JsonFieldType.ARRAY).description("파일 List").optional(),
                        fieldWithPath("files.[].url").type(JsonFieldType.STRING).description("파일 URL")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("글 ID"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(NOTICE_STATUS)),
                        fieldWithPath("is_visible").type(JsonFieldType.BOOLEAN).description("노출 여부").optional(),
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

    @Test
    @WithUserDetails(value = "1", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Transactional
    void edit() throws Exception {
        EditNoticeRequest request = new EditNoticeRequest(null, true, "EDIT TEST TITLE", "EDIT TEST DESCRIPTION", null);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .put("/admin/notice/{noticeId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_edit_notice",
                        pathParameters(
                                parameterWithName("noticeId").description("공지 ID")
                        ),
                        requestFields(
                                fieldWithPath("is_visible").type(JsonFieldType.BOOLEAN).description("노출 여부"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목").optional(),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("상세 내용"),
                                fieldWithPath("files").type(JsonFieldType.ARRAY).description("파일 List").optional(),
                                fieldWithPath("files.[].operation").type(JsonFieldType.STRING).description(generateLinkCode(FILE_OPERATION_TYPE)),
                                fieldWithPath("files.[].url").type(JsonFieldType.STRING).description("파일 URL")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("글 ID"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(NOTICE_STATUS)),
                                fieldWithPath("is_visible").type(JsonFieldType.BOOLEAN).description("노출 여부").optional(),
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

    @Test
    @WithUserDetails(value = "1", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Transactional
    void delete() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .delete("/admin/notice/{noticeId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_delete_notice",
                        pathParameters(
                                parameterWithName("noticeId").description("공지 ID")
                        )
                )
        );
    }

    @Test
    @WithUserDetails(value = "1", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Transactional
    void search() throws Exception {

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/notice/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_search_notice",
                requestParameters(
                        parameterWithName("page").description("페이지").attributes(getDefault(1)).optional(),
                        parameterWithName("size").description("페이지 사이즈").attributes(getDefault(20)).optional(),
                        parameterWithName("sort").description(generateLinkCode(NOTICE_SORT)).attributes(getDefault("ID")).optional(),
                        parameterWithName("order").description("정령 방식").attributes(getDefault("DESC")).optional(),
                        parameterWithName("search").description("제목").optional(),
                        parameterWithName("start_at").description("작성일 조회조건 - 시작").optional(),
                        parameterWithName("end_At").description("작성일 조회조건 - 끝").optional()
                ),
                responseFields(
                        fieldWithPath("total").type(JsonFieldType.NUMBER).description("총 갯수"),
                        fieldWithPath("content").type(JsonFieldType.ARRAY).description("조회 결과 목록"),
                        fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("글 ID"),
                        fieldWithPath("content.[].status").type(JsonFieldType.STRING).description(generateLinkCode(NOTICE_STATUS)),
                        fieldWithPath("content.[].is_visible").type(JsonFieldType.BOOLEAN).description("노출 여부").optional(),
                        fieldWithPath("content.[].is_important").type(JsonFieldType.BOOLEAN).description("중요 공지 여부").optional(),
                        fieldWithPath("content.[].view_count").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("content.[].title").type(JsonFieldType.STRING).description("제목"),
                        fieldWithPath("content.[].file").type(JsonFieldType.OBJECT).description("파일 List").optional(),
                        fieldWithPath("content.[].file.type").type(JsonFieldType.STRING).description(generateLinkCode(FILE_TYPE)),
                        fieldWithPath("content.[].file.url").type(JsonFieldType.STRING).description("파일 URL"),
                        fieldWithPath("content.[].created_at").type(JsonFieldType.STRING).description("작성 일시").attributes(getZonedDateFormat()),
                        fieldWithPath("content.[].created_by").type(JsonFieldType.OBJECT).description("작성자 정보"),
                        fieldWithPath("content.[].created_by.id").type(JsonFieldType.NUMBER).description("작성자 아이디"),
                        fieldWithPath("content.[].created_by.email").type(JsonFieldType.STRING).description("작성자 이메일"),
                        fieldWithPath("content.[].created_by.username").type(JsonFieldType.STRING).description("작성자 닉네임"),
                        fieldWithPath("content.[].created_by.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL")
                )
            )
        );
    }

    @Test
    @WithUserDetails(value = "1", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Transactional
    void get() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/notice/{noticeId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_notice",
                pathParameters(
                        parameterWithName("noticeId").description("공지 ID")
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("글 ID"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description(generateLinkCode(NOTICE_STATUS)),
                        fieldWithPath("is_visible").type(JsonFieldType.BOOLEAN).description("노출 여부").optional(),
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
