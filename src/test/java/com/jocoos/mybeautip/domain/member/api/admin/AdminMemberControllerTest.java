package com.jocoos.mybeautip.domain.member.api.admin;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.MEMBER_STATUS;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminMemberControllerTest extends RestDocsTestSupport {


    @Test
    void getMemberStatuses() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/admin/member/status"))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("admin_get_member_status",
                responseFields(
                        fieldWithPath("[].status").type(JsonFieldType.STRING).description(generateLinkCode(MEMBER_STATUS)).optional(),
                        fieldWithPath("[].status_name").type(JsonFieldType.STRING).description("회원 상태 이름"),
                        fieldWithPath("[].count").type(JsonFieldType.NUMBER).description("회원수")
                )));
    }

}
