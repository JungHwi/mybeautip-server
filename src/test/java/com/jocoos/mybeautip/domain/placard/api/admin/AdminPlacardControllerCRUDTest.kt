package com.jocoos.mybeautip.domain.placard.api.admin

import com.jocoos.mybeautip.testutil.fixture.PlacardFixture.makePlacard
import com.jocoos.mybeautip.domain.placard.code.PlacardLinkType.EVENT
import com.jocoos.mybeautip.domain.placard.code.PlacardStatus
import com.jocoos.mybeautip.domain.placard.dto.PatchPlacardRequest
import com.jocoos.mybeautip.domain.placard.dto.PlacardRequest
import com.jocoos.mybeautip.domain.placard.persistence.repository.PlacardRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.PLACARD_LINK_TYPE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.PLACARD_STATUS
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.openapitools.jackson.nullable.JsonNullable
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.ZonedDateTime
import java.util.*

@TestInstance(PER_CLASS)
class AdminPlacardControllerCRUDTest(
    private val placardRepository: PlacardRepository
) : RestDocsIntegrationTestSupport() {

    private var placardFixtureId: Long = 0

    @BeforeAll
    fun init() {
        placardFixtureId = placardRepository.save(makePlacard()).id
    }

    @AfterAll
    fun after() {
        placardRepository.deleteAll()
    }

    @Test
    fun getPlacard() {
        val result: ResultActions = mockMvc
            .perform(
                get("/admin/placard/{placard_id}", placardFixtureId)
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_placard",
                pathParameters(
                    parameterWithName("placard_id").description("플랜카드 ID")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("플랜카드 ID"),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(PLACARD_STATUS)),
                    fieldWithPath("title").type(STRING).description("백 타이틀"),
                    fieldWithPath("link_type").type(STRING).description(generateLinkCode(PLACARD_LINK_TYPE)),
                    fieldWithPath("link_argument").type(STRING).description("링크 연결 파라미터").optional(),
                    fieldWithPath("image_url").type(STRING).description("이미지 Url"),
                    fieldWithPath("description").type(STRING).description("배너명 (플랜카드 설명)").optional(),
                    fieldWithPath("color").type(STRING).description("색깔"),
                    fieldWithPath("is_top_fix").type(BOOLEAN).description("고정 여부 (true 일 때만 응답)").optional(),
                    fieldWithPath("start_at").type(STRING).description("시작일시").attributes(getZonedDateFormat()),
                    fieldWithPath("end_at").type(STRING).description("종료일시").attributes(getZonedDateFormat()),
                    fieldWithPath("created_at").type(STRING).description("생성일시").attributes(getZonedDateFormat())
                )
            )
        )
    }

    @Test
    fun getPlacards() {

        val result: ResultActions = mockMvc
            .perform(
                get("/admin/placard")
                    .header(AUTHORIZATION, defaultAdminToken)

            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_placards",
                requestParameters(
                    parameterWithName("status").description(generateLinkCode(PLACARD_STATUS)).optional(),
                    parameterWithName("page").description("페이지 넘버").optional().attributes(getDefault(1)),
                    parameterWithName("size").description("페이지 사이즈").optional().attributes(getDefault(10)),
                    parameterWithName("search").description("검색 - 검색 필드,검색어 / 가능한 검색 필드 - description").optional(),
                    parameterWithName("start_at").description("검색 시작일자").optional().attributes(getZonedDateFormat()),
                    parameterWithName("end_at").description("검색 종료일자").optional().attributes(getZonedDateFormat()),
                    parameterWithName("is_top_fix").description("상단 고정 여부 (boolean)").optional()
                ),
                responseFields(
                    fieldWithPath("total").type(NUMBER).description("총 플랜카드 개수"),
                    fieldWithPath("content").type(ARRAY).description("플랜카드 목록"),
                    fieldWithPath("content.[].id").type(NUMBER).description("플랜카드 ID"),
                    fieldWithPath("content.[].status").type(STRING).description(generateLinkCode(PLACARD_STATUS)),
                    fieldWithPath("content.[].link_type").type(STRING).description(generateLinkCode(PLACARD_LINK_TYPE)),
                    fieldWithPath("content.[].image_url").type(STRING).description("이미지 Url"),
                    fieldWithPath("content.[].description").type(STRING).description("배너명 (플랜카드 설명)").optional(),
                    fieldWithPath("content.[].is_top_fix").type(BOOLEAN).description("상단 고정 여부 (true/null)").optional(),
                    fieldWithPath("content.[].start_at").type(STRING).description("시작일시")
                        .attributes(getZonedDateFormat()),
                    fieldWithPath("content.[].end_at").type(STRING).description("종료일시")
                        .attributes(getZonedDateFormat()),
                    fieldWithPath("content.[].created_at").type(STRING).description("생성일시")
                        .attributes(getZonedDateFormat())
                )
            )
        )
    }

    @Test
    fun createPlacard() {

        val request = PlacardRequest(
            PlacardStatus.ACTIVE,
            "https://static-dev.mybeautip.com/image/placard_image",
            "title",
            EVENT,
            "2",
            "description",
            "#color",
            ZonedDateTime.now().minusDays(1),
            ZonedDateTime.now().plusDays(1)
        );

        val result: ResultActions = mockMvc
            .perform(
                post("/admin/placard")
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isCreated())
            .andDo(print());

        result.andDo(
            document(
                "admin_create_placard",
                requestFields(
                    fieldWithPath("status").type(STRING).description(generateLinkCode(PLACARD_STATUS)),
                    fieldWithPath("image_url").type(STRING).description("이미지 URL"),
                    fieldWithPath("title").type(STRING).description("백 타이틀"),
                    fieldWithPath("link_type").type(STRING).description(generateLinkCode(PLACARD_LINK_TYPE)),
                    fieldWithPath("link_argument").type(STRING).description("링크 파라미터").optional(),
                    fieldWithPath("description").type(STRING).description("배너명 (플랜카드 설명)").optional(),
                    fieldWithPath("color").type(STRING).description("색깔값 (RGB)"),
                    fieldWithPath("started_at").type(STRING).description("시작일시").attributes(getZonedDateFormat()),
                    fieldWithPath("ended_at").type(STRING).description("종료일시").attributes(getZonedDateFormat())
                ),
                responseHeaders(
                    headerWithName("Location").description("생성 자원 확인 가능 위치")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("플랜카드 ID"),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(PLACARD_STATUS)),
                    fieldWithPath("link_type").type(STRING).description(generateLinkCode(PLACARD_LINK_TYPE)),
                    fieldWithPath("image_url").type(STRING).description("이미지 Url"),
                    fieldWithPath("description").type(STRING).description("배너명 (플랜카드 설명)").optional(),
                    fieldWithPath("start_at").type(STRING).description("시작일시").attributes(getZonedDateFormat()),
                    fieldWithPath("end_at").type(STRING).description("종료일시").attributes(getZonedDateFormat()),
                    fieldWithPath("created_at").type(STRING).description("생성일시").attributes(getZonedDateFormat())
                )
            )
        );
    }

    @Test
    fun editPlacard() {
        val request = PatchPlacardRequest
            .builder()
            .title(JsonNullable.of("수정"))
            .build()

        val result: ResultActions = mockMvc
            .perform(
                patch("/admin/placard/{placard_id}", placardFixtureId)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_edit_placard",
                pathParameters(
                    parameterWithName("placard_id").description("플랜카드 ID")
                ),
                requestFields(
                    fieldWithPath("status").type(STRING).description(generateLinkCode(PLACARD_STATUS)).optional(),
                    fieldWithPath("image_url").type(STRING).description("이미지 URL").optional(),
                    fieldWithPath("title").type(STRING).description("백 타이틀").optional(),
                    fieldWithPath("link_type").type(STRING).description(generateLinkCode(PLACARD_LINK_TYPE)).optional(),
                    fieldWithPath("link_argument").type(STRING).description("링크 파라미터").optional(),
                    fieldWithPath("description").type(STRING).description("배너명 (플랜카드 설명)").optional(),
                    fieldWithPath("color").type(STRING).description("색깔값 (RGB)").optional(),
                    fieldWithPath("started_at").type(STRING).description("시작일시").attributes(getZonedDateFormat())
                        .optional(),
                    fieldWithPath("ended_at").type(STRING).description("종료일시").attributes(getZonedDateFormat())
                        .optional()
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("플랜카드 ID")
                )
            )
        )
    }

    @Test
    fun deletePlacard() {
        val result: ResultActions = mockMvc
            .perform(
                delete("/admin/placard/{placard_id}", placardFixtureId)
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_delete_placard",
                pathParameters(
                    parameterWithName("placard_id").description("플랜카드 ID")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("플랜카드 ID")
                )
            )
        )
    }
}
