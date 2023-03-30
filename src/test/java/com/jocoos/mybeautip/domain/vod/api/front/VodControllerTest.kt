package com.jocoos.mybeautip.domain.vod.api.front

import com.jocoos.mybeautip.domain.broadcast.BroadcastTestSupport
import com.jocoos.mybeautip.domain.vod.persistence.domain.Vod
import com.jocoos.mybeautip.domain.vod.persistence.repository.VodRepository
import com.jocoos.mybeautip.domain.community.dto.ReportRequest
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.VOD_SORT_FIELD
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.global.dto.single.BooleanDto
import com.jocoos.mybeautip.global.dto.single.IntegerDto
import com.jocoos.mybeautip.testutil.fixture.makeVod
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
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


@TestInstance(PER_CLASS)
class VodControllerTest(
    private val vodRepository: VodRepository,
) : BroadcastTestSupport() {

    private lateinit var vod: Vod

    @BeforeAll
    fun beforeAll() {
        vod = vodRepository.save(makeVod(defaultBroadcastCategory, defaultInfluencer.id))
    }

    @AfterAll
    fun afterAll() {
        vodRepository.delete(vod);
    }

    @Test
    fun `VOD 목록 조회`() {
        vodRepository.saveAll(
            listOf(
                makeVod(defaultBroadcastCategory, defaultInfluencer.id, title = "vod title"),
                makeVod(defaultBroadcastCategory, defaultInfluencer.id, title = "this is title")
            )
        )

        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/vod")
                    .header(AUTHORIZATION, requestUserToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_vod_list",
                requestParameters(
                    parameterWithName("cursor").description("커서. Vod 아이디").optional(),
                    parameterWithName("category_id").description("카테고리 ID").optional(),
                    parameterWithName("sort").description(generateLinkCode(VOD_SORT_FIELD))
                        .attributes(getDefault("CREATED_AT")).optional(),
                    parameterWithName("order").description("정렬 방향. ASC, DESC").attributes(getDefault("DESC"))
                        .optional(),
                    parameterWithName("size").description("페이지 사이즈").attributes(getDefault(5)).optional()
                ),
                responseFields(
                    fieldWithPath("next_cursor").type(STRING).description("다음 커서. VOD 아이디"),
                    fieldWithPath("content").type(ARRAY).description("VOD 목록"),
                    fieldWithPath("content.[].id").type(NUMBER).description("VOD 아이디"),
                    fieldWithPath("content.[].video_key").type(NUMBER).description("Flip Flop Lite 비디오 아이디"),
                    fieldWithPath("content.[].url").type(STRING).description("VOD URL"),
                    fieldWithPath("content.[].title").type(STRING).description("타이틀"),
                    fieldWithPath("content.[].thumbnail_url").type(STRING).description("썸네일 URL"),
                    fieldWithPath("content.[].view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("content.[].heart_count").type(NUMBER).description("하트수"),
                    fieldWithPath("content.[].category").type(OBJECT).description("카테고리 정보"),
                    fieldWithPath("content.[].category.id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("content.[].category.title").type(STRING).description("카테고리 타이틀"),
                    fieldWithPath("content.[].member").type(OBJECT).description("회원 정보"),
                    fieldWithPath("content.[].member.id").type(NUMBER).description("회원 아이디"),
                    fieldWithPath("content.[].member.email").type(STRING).description("회원 이메일").optional(),
                    fieldWithPath("content.[].member.username").type(STRING).description("회원 닉네임"),
                    fieldWithPath("content.[].member.avatar_url").type(STRING).description("회원 아바타 URL"),
                )
            )
        )
    }

    @Test
    fun `VOD 상세 조회`() {
        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/vod/{vod_id}", vod.id)
                    .header(AUTHORIZATION, requestUserToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_vod",
                pathParameters(
                    parameterWithName("vod_id").description("VOD ID")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("VOD 아이디"),
                    fieldWithPath("video_key").type(NUMBER).description("Flip Flop Lite 비디오 아이디"),
                    fieldWithPath("url").type(STRING).description("VOD URL"),
                    fieldWithPath("title").type(STRING).description("타이틀"),
                    fieldWithPath("thumbnail_url").type(STRING).description("썸네일 URL"),
                    fieldWithPath("view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("heart_count").type(NUMBER).description("하트수"),
                    fieldWithPath("member").type(OBJECT).description("회원 정보"),
                    fieldWithPath("member.id").type(NUMBER).description("회원 아이디"),
                    fieldWithPath("member.email").type(STRING).description("회원 이메일").optional(),
                    fieldWithPath("member.username").type(STRING).description("회원 닉네임"),
                    fieldWithPath("member.avatar_url").type(STRING).description("회원 아바타 URL"),
                )
            )
        )
    }

    @Test
    fun `VOD 신고`() {
        val request = ReportRequest.builder()
            .description("신고 사유")
            .build();


        val result: ResultActions = mockMvc
            .perform(
                post("/api/1/vod/{vod_id}/report", vod.id)
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "report_vod",
                pathParameters(
                    parameterWithName("vod_id").description("VOD ID")
                ),
                requestFields(
                    fieldWithPath("description").type(STRING).description("신고 사유")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("VOD 아이디"),
                    fieldWithPath("report_count").type(NUMBER).description("신고수"),
                )
            )
        )
    }

    @Test
    fun `VOD 좋아요 (연속)`() {

        val request = IntegerDto(10)

        val result: ResultActions = mockMvc
            .perform(
                post("/api/1/vod/{vod_id}/heart", vod.id)
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "heart_vod",
                pathParameters(
                    parameterWithName("vod_id").description("VOD ID")
                ),
                requestFields(
                    fieldWithPath("number").type(NUMBER).description("유저가 누른 하트수")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("VOD 아이디"),
                    fieldWithPath("heart_count").type(NUMBER).description("하트수"),
                )
            )
        )
    }

    @Test
    fun `VOD Scrap API`() {

        val request = BooleanDto(true)

        val result: ResultActions = mockMvc
            .perform(
                patch("/api/1/vod/{vod_id}/scrap", vod.id)
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "scrap_vod",
                pathParameters(
                    parameterWithName("vod_id").description("VOD ID")
                ),
                requestFields(
                    fieldWithPath("bool").type(BOOLEAN).description("스크랩 여부")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("스크랩 아이디"),
                    fieldWithPath("relation_id").type(NUMBER).description("VOD ID"),
                    fieldWithPath("is_scrap").type(BOOLEAN).description("스크랩 여부"),
                    fieldWithPath("created_at").type(STRING).description("생성 시간").attributes(getZonedDateFormat())
                )
            )
        )
    }
}
