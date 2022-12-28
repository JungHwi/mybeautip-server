package com.jocoos.mybeautip.domain.file.api.front;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FileControllerTest extends RestDocsTestSupport {


    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void uploadFiles() throws Exception {
        MockMultipartFile file = new MockMultipartFile("files", "file", "image/jpeg", "mock".getBytes());
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .multipart("/api/1/file")
                        .file(file))
                .andExpect(status().isOk())
                .andDo(print());

        result.andDo(document("upload_file",
                        requestParts(
                                partWithName("files").description("업로드할 파일 목록")
                        ),
                        responseFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("UPLOAD 된 파일 URL")
                        )
                )
        );
    }
}
