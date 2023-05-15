package com.jocoos.mybeautip.domain.store.api.admin

import com.jocoos.mybeautip.domain.store.code.StoreCategoryStatus
import com.jocoos.mybeautip.domain.store.dto.CreateStoreCategoryRequest
import com.jocoos.mybeautip.domain.store.dto.StoreCategoryDetailDto
import com.jocoos.mybeautip.domain.store.persistence.domain.StoreCategory
import com.jocoos.mybeautip.domain.store.persistence.repository.StoreCategoryRepository
import com.jocoos.mybeautip.global.code.CountryCode
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.COUNTRY_CODE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.STORE_CATEGORY_STATUS
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.testutil.fixture.makeStoreCategory
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.requestParameters
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AdminStoreCategoryControllerTest(
    private val repository: StoreCategoryRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun create() {
        val details = listOf(StoreCategoryDetailDto(CountryCode.KR, "한국 카테고리"), StoreCategoryDetailDto(CountryCode.TH, "태국 카테고리"));
        val request = CreateStoreCategoryRequest.builder()
            .name("대표 카테고리")
            .status(StoreCategoryStatus.ACTIVE)
            .categoryDetailList(details)
            .build();

        val result: ResultActions = mockMvc
            .perform(
                post("/admin/store/category")
                    .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_create_store_category",
                requestFields(
                    fieldWithPath("name").type(STRING).description("스토어 대표 카테고리명"),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(STORE_CATEGORY_STATUS)),
                    fieldWithPath("category_detail_list").type(ARRAY).description("카테고리 상세 정보(국가별)"),
                    fieldWithPath("category_detail_list.[].country").type(STRING).description(generateLinkCode(COUNTRY_CODE)),
                    fieldWithPath("category_detail_list.[].name").type(STRING).description("국가별 카테고리명")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("스토어 대표 카테고리 ID"),
                    fieldWithPath("code").type(STRING).description("스토어 대표 카테고리 코드"),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(STORE_CATEGORY_STATUS)),
                    fieldWithPath("name").type(STRING).description("스토어 대표 카테고리"),
                    fieldWithPath("category_detail_list").type(ARRAY).description("카테고리 상세 정보(국가별)"),
                    fieldWithPath("category_detail_list.[].country").type(STRING).description(generateLinkCode(COUNTRY_CODE)),
                    fieldWithPath("category_detail_list.[].name").type(STRING).description("국가별 카테고리명")
                )
            )
        )
    }

    @Test
    fun search() {
        saveStoreCategory()

        val result: ResultActions = mockMvc
            .perform(
                get("/admin/store/category")
                    .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_search_store_category",
                requestParameters(
                    parameterWithName("page").description("페이지").optional(),
                    parameterWithName("size").description("페이지 사이즈").optional()
                ),
                responseFields(
                    fieldWithPath("total").type(NUMBER).description("조회 Total Count"),
                    fieldWithPath("content").type(ARRAY).description("스토어 카테고리 목록"),
                    fieldWithPath("content.[].id").type(NUMBER).description("스토어 카테고리 ID"),
                    fieldWithPath("content.[].name").type(STRING).description("스토어 카테고리명"),
                    fieldWithPath("content.[].display_count").type(NUMBER).description("전시 상품수"),
                )
            )
        )
    }

    fun saveStoreCategory(): StoreCategory {
        return repository.save(makeStoreCategory())
    }
}