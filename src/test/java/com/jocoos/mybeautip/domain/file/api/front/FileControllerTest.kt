package com.jocoos.mybeautip.domain.file.api.front

import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart
import org.springframework.restdocs.payload.JsonFieldType.ARRAY
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.partWithName
import org.springframework.restdocs.request.RequestDocumentation.requestParts
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class FileControllerTest(

) : RestDocsIntegrationTestSupport() {

    @Test
    fun uploadFiles() {
        val file = MockMultipartFile("files", "file", "image/jpeg", "mock".toByteArray())

        val result: ResultActions = mockMvc
            .perform(
            multipart("/api/1/file")
                .file(file)
                .header(AUTHORIZATION, requestUserToken)
        )
            .andExpect(status().isOk)
            .andDo(print())
        result.andDo(
            document(
                "upload_file",
                requestParts(
                    partWithName("files").description("업로드할 파일 목록")
                ),
                responseFields(
                    fieldWithPath("[]").type(ARRAY).description("UPLOAD 된 파일 URL")
                )
            )
        )
    }
}
