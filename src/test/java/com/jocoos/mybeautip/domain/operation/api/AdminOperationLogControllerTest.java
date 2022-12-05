package com.jocoos.mybeautip.domain.operation.api;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.OPERATION_TYPE;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminOperationLogControllerTest extends RestDocsTestSupport {

    @Test
    void getOperationLogs() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/operation/log?target_id=5&types=MEMBER_EXILE,MEMBER_SUSPENDED"))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_operation_logs",
                requestParameters(
                        parameterWithName("types").description("운영 구분 배열 - " + generateLinkCode(OPERATION_TYPE)),
                        parameterWithName("target_id").description("타겟 아이디"),
                        parameterWithName("page").description("페이지 넘버").optional().attributes(getDefault(1)),
                        parameterWithName("size").description("페이지 사이즈").optional().attributes(getDefault(10))
                ),
                responseFields(
                        fieldWithPath("total").type(JsonFieldType.NUMBER).description("총 운영 로그 개수"),
                        fieldWithPath("content").type(JsonFieldType.ARRAY).description("운영 로그 목록"),
                        fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("운영 로그 ID"),
                        fieldWithPath("content.[].operation_type").type(JsonFieldType.STRING).description(generateLinkCode(OPERATION_TYPE)),
                        fieldWithPath("content.[].created_at").type(JsonFieldType.STRING).description("운영 작업한 일시").attributes(getZonedDateFormat()),
                        fieldWithPath("content.[].cursor").ignored(),
                        fieldWithPath("content.[].admin_member").type(JsonFieldType.OBJECT).description("운영자 정보"),
                        fieldWithPath("content.[].admin_member.id").type(JsonFieldType.NUMBER).description("운영자 아이디"),
                        fieldWithPath("content.[].admin_member.username").type(JsonFieldType.STRING).description("작성자 이름"),
                        fieldWithPath("content.[].admin_member.email").type(JsonFieldType.STRING).description("운영자 이메일").optional(),
                        fieldWithPath("content.[].admin_member.avatar_url").type(JsonFieldType.STRING).description("작성자 아바타 URL").optional()
                )
        ));
    }
}