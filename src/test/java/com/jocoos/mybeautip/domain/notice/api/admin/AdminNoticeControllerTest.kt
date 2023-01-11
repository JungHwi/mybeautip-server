package com.jocoos.mybeautip.domain.notice.api.admin

import com.jocoos.mybeautip.domain.notice.dto.EditNoticeRequest
import com.jocoos.mybeautip.domain.notice.dto.NoticeResponse
import com.jocoos.mybeautip.domain.notice.dto.WriteNoticeRequest
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AdminNoticeControllerTest(

) : RestDocsIntegrationTestSupport() {

    @Test
    fun write() {

        // given
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
            .andExpect(status().isCreated)
            .andDo(print())

        // when & then
        result.andDo(
            document(
                "admin_write_notice",
                requestFields(
                    fieldWithPath("is_visible").type(BOOLEAN).description("노출 여부"),
                    fieldWithPath("title").type(STRING).description("제목").optional(),
                    fieldWithPath("description").type(STRING).description("상세 내용"),
                    fieldWithPath("files").type(ARRAY).description("파일 List")
                        .optional(),
                    fieldWithPath("files.[].url").type(STRING).description("파일 URL")
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

    @Test
    fun edit() {

        // given
        val id: Long = makeNotice()
        val request = EditNoticeRequest.builder()
            .isVisible(true)
            .isImportant(true)
            .title("EDIT TEST TITLE")
            .description("EDIT TEST DESCRIPTION")
            .build();

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                put("/admin/notice/{noticeId}", id)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_edit_notice",
                pathParameters(
                    parameterWithName("noticeId").description("공지 ID")
                ),
                requestFields(
                    fieldWithPath("is_visible").type(BOOLEAN).description("노출 여부"),
                    fieldWithPath("is_important").type(BOOLEAN).description("중요 공지 여부"),
                    fieldWithPath("title").type(STRING).description("제목").optional(),
                    fieldWithPath("description").type(STRING).description("상세 내용"),
                    fieldWithPath("files").type(ARRAY).description("파일 List").optional(),
                    fieldWithPath("files.[].operation").type(STRING).description(generateLinkCode(FILE_OPERATION_TYPE)),
                    fieldWithPath("files.[].url").type(STRING).description("파일 URL")
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

    @Test
    fun delete() {

        // given
        val id: Long = makeNotice()

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                delete("/admin/notice/{noticeId}", id)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result
            .andDo(
                document(
                    "admin_delete_notice",
                    pathParameters(
                        parameterWithName("noticeId").description("공지 ID")
                    )
                )
            )
    }

    @Test
    fun search() {

        // given
        makeNotice()

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/admin/notice/")
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result
            .andDo(
                document(
                    "admin_search_notice",
                    requestParameters(
                        parameterWithName("page").description("페이지").attributes(getDefault(1)).optional(),
                        parameterWithName("size").description("페이지 사이즈").attributes(getDefault(20)).optional(),
                        parameterWithName("sort").description(generateLinkCode(NOTICE_SORT))
                            .attributes(getDefault("ID")).optional(),
                        parameterWithName("order").description("정령 방식").attributes(getDefault("DESC")).optional(),
                        parameterWithName("search").description("제목").optional(),
                        parameterWithName("start_at").description("작성일 조회조건 - 시작").optional(),
                        parameterWithName("end_At").description("작성일 조회조건 - 끝").optional()
                    ),
                    responseFields(
                        fieldWithPath("total").type(NUMBER).description("총 갯수"),
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
                get("/admin/notice/{noticeId}", id)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result
            .andDo(
                document(
                    "admin_get_notice",
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

    //    private fun makeNotice(): Long {
//        val writeNoticeRequest = WriteNoticeRequest.builder()
//            .isVisible(true)
//            .title("TEST TITLE")
//            .description("TEST DESCRIPTION")
//            .build()
//
//        val id: Long = Given {
//            port(port)
//            log().all()
//            contentType(APPLICATION_JSON_VALUE)
//            header(AUTHORIZATION, defaultAdminToken)
//            body(writeNoticeRequest)
//        } When {
//            post("/admin/notice/")
//        } Then {
//            statusCode(HttpStatus.CREATED.value())
//            log().all()
//        } Extract {
//            path<Long?>("id").toLong()
//        }
//        return id
//    }

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
