package com.jocoos.mybeautip.domain.notice.api.front

import com.jocoos.mybeautip.domain.notice.dto.NoticeResponse
import com.jocoos.mybeautip.domain.notice.dto.WriteNoticeRequest
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.FILE_TYPE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.NOTICE_STATUS
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class NoticeControllerTest(

) : RestDocsIntegrationTestSupport() {

    @Test
    fun list() {

        // given
        makeNotice()

        //
        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/notice")
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_notices",
                requestParameters(
                    parameterWithName("cursor").description("커서").optional(),
                    parameterWithName("size").description("페이지 사이즈")
                        .attributes(getDefault(20)).optional()
                ),
                responseFields(
                    fieldWithPath("next_cursor").type(STRING).description("커서 정보"),
                    fieldWithPath("content").type(ARRAY).description("조회 결과 목록"),
                    fieldWithPath("content.[].id").type(NUMBER).description("글 ID"),
                    fieldWithPath("content.[].status").type(STRING).description(generateLinkCode(NOTICE_STATUS)),
                    fieldWithPath("content.[].is_visible").type(BOOLEAN).description("노출 여부").optional(),
                    fieldWithPath("content.[].is_important").type(BOOLEAN).description("중요 공지 여부").optional(),
                    fieldWithPath("content.[].view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("content.[].title").type(STRING).description("제목"),
                    fieldWithPath("content.[].file").type(OBJECT).description("파일 List").optional(),
                    fieldWithPath("content.[].file.type").type(STRING).description(generateLinkCode(FILE_TYPE)),
                    fieldWithPath("content.[].file.url").type(STRING).description("파일 URL"),
                    fieldWithPath("content.[].created_at").type(STRING).description("작성 일시")
                        .attributes(getZonedDateFormat()),
                    fieldWithPath("content.[].created_by").type(OBJECT).description("작성자 정보"),
                    fieldWithPath("content.[].created_by.id").type(NUMBER).description("작성자 아이디"),
                    fieldWithPath("content.[].created_by.email").type(STRING).description("작성자 이메일"),
                    fieldWithPath("content.[].created_by.username").type(STRING).description("작성자 닉네임"),
                    fieldWithPath("content.[].created_by.avatar_url").type(STRING).description("작성자 아바타 URL")
                )
            )
        )
    }

    @Test
    fun get() {

        // given
        val id: Long = makeNotice()

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/notice/{noticeId}", id)
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_notice",
                pathParameters(
                    parameterWithName("noticeId").description("공지 ID")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("글 ID"),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(NOTICE_STATUS)),
                    fieldWithPath("is_visible").type(BOOLEAN).description("노출 여부").optional(),
                    fieldWithPath("is_important").type(BOOLEAN).description("중요 공지 여부").optional(),
                    fieldWithPath("view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("title").type(STRING).description("제목"),
                    fieldWithPath("description").type(STRING).description("내용"),
                    fieldWithPath("files").type(ARRAY).description("파일 List").optional(),
                    fieldWithPath("files.[].type").type(STRING).description(generateLinkCode(FILE_TYPE)),
                    fieldWithPath("files.[].url").type(STRING).description("파일 URL"),
                    fieldWithPath("modified_at").type(STRING).description("수정 일시").attributes(getZonedDateFormat()),
                    fieldWithPath("modified_by").type(OBJECT).description("수정자 정보"),
                    fieldWithPath("modified_by.id").type(NUMBER).description("수정자 아이디"),
                    fieldWithPath("modified_by.email").type(STRING).description("수정자 이메일"),
                    fieldWithPath("modified_by.username").type(STRING).description("수정자 닉네임"),
                    fieldWithPath("modified_by.avatar_url").type(STRING).description("수정자 아바타 URL"),
                    fieldWithPath("created_at").type(STRING).description("작성 일시").attributes(getZonedDateFormat()),
                    fieldWithPath("created_by").type(OBJECT).description("작성자 정보"),
                    fieldWithPath("created_by.id").type(NUMBER).description("작성자 아이디"),
                    fieldWithPath("created_by.email").type(STRING).description("작성자 이메일"),
                    fieldWithPath("created_by.username").type(STRING).description("작성자 닉네임"),
                    fieldWithPath("created_by.avatar_url").type(STRING).description("작성자 아바타 URL")
                )
            )
        )
    }

    private fun makeNotice(): Long {
        val request = WriteNoticeRequest.builder()
            .isVisible(true)
            .title("TEST TITLE")
            .description("TEST DESCRIPTION")
            .build()

        val result: ResultActions = mockMvc
            .perform(
                post("/admin/notice/")
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )

        val contentAsString: String = result.andReturn().response.contentAsString
        val response: NoticeResponse = objectMapper.readValue(contentAsString, NoticeResponse::class.java)
        return response.id
    }
}
