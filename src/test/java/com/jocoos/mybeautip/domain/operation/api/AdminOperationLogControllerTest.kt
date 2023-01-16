package com.jocoos.mybeautip.domain.operation.api

import com.jocoos.mybeautip.domain.operation.persistence.repository.OperationLogRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.OPERATION_TYPE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.testutil.fixture.makeOperationLog
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.requestParameters
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AdminOperationLogControllerTest(
    private val operationLogRepository: OperationLogRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun getOperationLogs() {

        // given
        operationLogRepository.save(makeOperationLog(targetId = requestUser.id.toString(), createdBy = defaultAdmin))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/admin/operation/log?target_id={target_id}&types=MEMBER_EXILE,MEMBER_SUSPENDED", requestUser.id)
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_operation_logs",
                requestParameters(
                    parameterWithName("types").description("운영 구분 배열 - " + generateLinkCode(OPERATION_TYPE)),
                    parameterWithName("target_id").description("타겟 아이디"),
                    parameterWithName("page").description("페이지 넘버").optional().attributes(getDefault(1)),
                    parameterWithName("size").description("페이지 사이즈").optional().attributes(getDefault(10))
                ),
                responseFields(
                    fieldWithPath("total").type(NUMBER).description("총 운영 로그 개수"),
                    fieldWithPath("content").type(ARRAY).description("운영 로그 목록").optional(),
                    fieldWithPath("content.[].id").type(NUMBER).description("운영 로그 ID"),
                    fieldWithPath("content.[].operation_type").type(STRING)
                        .description(generateLinkCode(OPERATION_TYPE)),
                    fieldWithPath("content.[].created_at").type(STRING).description("운영 작업한 일시")
                        .attributes(getZonedDateFormat()),
                    fieldWithPath("content.[].admin_member").type(OBJECT).description("운영자 정보").optional(),
                    fieldWithPath("content.[].admin_member.id").type(NUMBER).description("운영자 아이디"),
                    fieldWithPath("content.[].admin_member.username").type(STRING).description("작성자 이름"),
                    fieldWithPath("content.[].admin_member.email").type(STRING).description("운영자 이메일").optional(),
                    fieldWithPath("content.[].admin_member.avatar_url").type(STRING).description("작성자 아바타 URL")
                        .optional()
                )
            )
        )
    }
}
