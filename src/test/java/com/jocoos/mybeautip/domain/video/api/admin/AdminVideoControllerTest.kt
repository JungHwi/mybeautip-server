package com.jocoos.mybeautip.domain.video.api.admin

import com.jocoos.mybeautip.domain.video.persistence.domain.VideoCategory
import com.jocoos.mybeautip.domain.video.persistence.repository.VideoCategoryRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.MEMBER_STATUS
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.VIDEO_STATUS
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.global.dto.single.BooleanDto
import com.jocoos.mybeautip.global.dto.single.SortOrderDto
import com.jocoos.mybeautip.testutil.fixture.makeVideo
import com.jocoos.mybeautip.testutil.fixture.makeVideoCategory
import com.jocoos.mybeautip.testutil.fixture.makeVideos
import com.jocoos.mybeautip.video.Video
import com.jocoos.mybeautip.video.VideoRepository
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
class AdminVideoControllerTest(
    private val videoRepository: VideoRepository,
    private val videoCategoryRepository: VideoCategoryRepository
) : RestDocsIntegrationTestSupport() {

    private lateinit var category: VideoCategory

    @BeforeAll
    fun beforeAll() {
        category = videoCategoryRepository.save(makeVideoCategory())
    }

    @AfterAll
    fun afterAll() {
        videoCategoryRepository.delete(category)
    }

    @Test
    fun getVideos() {

        // given
        videoRepository.save(makeVideo(defaultAdmin, category))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/admin/video")
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_videos",
                requestParameters(
                    parameterWithName("status").description(generateLinkCode(VIDEO_STATUS)).optional(),
                    parameterWithName("category_id").description("카테고리 아이디").optional(),
                    parameterWithName("page").description("페이지 넘버").optional().attributes(getDefault(1)),
                    parameterWithName("size").description("페이지 사이즈").optional().attributes(getDefault(10)),
                    parameterWithName("sort").description("정렬 필드 (createdAt, viewCount, likeCount, commentCount, reportCount)")
                        .optional().attributes(getDefault("startedAt")),
                    parameterWithName("order").description("정렬 방향").optional().attributes(getDefault("DESC")),
                    parameterWithName("search").description("검색 - 검색필드,검색어 (title, comment)").optional(),
                    parameterWithName("start_at").description("검색 시작일자")
                        .attributes(getLocalDateFormat()).optional(),
                    parameterWithName("end_at").description("검색 종료일자")
                        .attributes(getLocalDateFormat()).optional(),
                    parameterWithName("is_reported").description("신고 여부 (boolean)").optional(),
                    parameterWithName("is_top_fix").description("상단 고정 여부 (boolean)").optional(),
                    parameterWithName("is_recommended").description("추천 여부 (boolean)").optional()
                ),
                responseFields(
                    fieldWithPath("total").type(NUMBER).description("총 비디오 개수"),
                    fieldWithPath("content").type(ARRAY).description("비디오 목록"),
                    fieldWithPath("content.[].id").type(NUMBER).description("비디오 ID"),
                    fieldWithPath("content.[].video_key").type(STRING).description("비디오키"),
                    fieldWithPath("content.[].url").type(STRING).description("비디오 Url"),
                    fieldWithPath("content.[].visibility").type(STRING).description("공개 여부"),
                    fieldWithPath("content.[].status").type(STRING).description(generateLinkCode(VIDEO_STATUS)),
                    fieldWithPath("content.[].thumbnail_url").type(STRING).description("썸네일 URL"),
                    fieldWithPath("content.[].title").type(STRING).description("제목"),
                    fieldWithPath("content.[].content").type(STRING).description("내용").optional(),
                    fieldWithPath("content.[].is_top_fix").type(BOOLEAN).description("상단 고정 여부").optional(),
                    fieldWithPath("content.[].is_recommended").type(BOOLEAN).description("추천 여부").optional(),
                    fieldWithPath("content.[].view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("content.[].like_count").type(NUMBER).description("좋아요수"),
                    fieldWithPath("content.[].comment_count").type(NUMBER).description("댓글/대댓글수"),
                    fieldWithPath("content.[].report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("content.[].created_at").type(STRING).description("작성일")
                        .attributes(getZonedDateFormat()),
                    fieldWithPath("content.[].started_at").type(STRING).description("게시일")
                        .attributes(getZonedDateFormat()),
                    fieldWithPath("content.[].duration").type(NUMBER).description("비디오 길이"),
                    fieldWithPath("content.[].data").type(STRING).description("비디오 상품 등 부가 정보"),
                    fieldWithPath("content.[].member").type(OBJECT).description("작성자 정보."),
                    fieldWithPath("content.[].member.id").type(NUMBER).description("작성자 아이디"),
                    fieldWithPath("content.[].member.status").type(STRING).description(generateLinkCode(MEMBER_STATUS)),
                    fieldWithPath("content.[].member.username").type(STRING).description("작성자 이름"),
                    fieldWithPath("content.[].member.avatar_url").type(STRING).description("작성자 아바타 URL"),
                    fieldWithPath("content.[].category").type(ARRAY).description("카테고리 정보"),
                    fieldWithPath("content.[].category.[].id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("content.[].category.[].title").type(STRING).description("카테고리 제목")
                )
            )
        )
    }

    @Test
    fun getVideo() {

        // given
        val video: Video = videoRepository.save(makeVideo(defaultAdmin, category))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/admin/video/{video_id}", video.id)
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_video",
                pathParameters(
                    parameterWithName("video_id").description("비디오 ID")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("비디오 ID"),
                    fieldWithPath("video_key").type(STRING).description("비디오키"),
                    fieldWithPath("url").type(STRING).description("비디오 Url"),
                    fieldWithPath("visibility").type(STRING).description("공개 여부"),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(VIDEO_STATUS)),
                    fieldWithPath("thumbnail_url").type(STRING).description("썸네일 URL"),
                    fieldWithPath("title").type(STRING).description("제목"),
                    fieldWithPath("content").type(STRING).description("내용").optional(),
                    fieldWithPath("is_top_fix").type(BOOLEAN).description("상단 고정 여부").optional(),
                    fieldWithPath("is_recommended").type(BOOLEAN).description("추천 여부").optional(),
                    fieldWithPath("view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("like_count").type(NUMBER).description("좋아요수"),
                    fieldWithPath("comment_count").type(NUMBER).description("댓글/대댓글수"),
                    fieldWithPath("report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("created_at").type(STRING).description("작성일").attributes(getZonedDateFormat()),
                    fieldWithPath("started_at").type(STRING).description("게시일").attributes(getZonedDateFormat()),
                    fieldWithPath("duration").type(NUMBER).description("비디오 길이"),
                    fieldWithPath("data").type(STRING).description("비디오 상품 등 부가 정보"),
                    fieldWithPath("member").type(OBJECT).description("작성자 정보."),
                    fieldWithPath("member.id").type(NUMBER).description("작성자 아이디"),
                    fieldWithPath("member.status").type(STRING).description(generateLinkCode(MEMBER_STATUS)),
                    fieldWithPath("member.username").type(STRING).description("작성자 이름"),
                    fieldWithPath("member.avatar_url").type(STRING).description("작성자 아바타 URL"),
                    fieldWithPath("category").type(ARRAY).description("카테고리 정보"),
                    fieldWithPath("category.[].id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("category.[].title").type(STRING).description("카테고리 제목")
                )
            )
        )
    }

    @Test
    fun hideVideo() {

        // given
        val video: Video = videoRepository.save(makeVideo(defaultAdmin, category))
        val request = BooleanDto(true)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                patch("/admin/video/{video_id}/hide", video.id)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_hide_video",
                pathParameters(
                    parameterWithName("video_id").description("비디오 ID")
                ),
                requestFields(
                    fieldWithPath("bool").type(BOOLEAN).description("숨김 여부")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("비디오 ID")
                )
            )
        )
    }

    @Test
    fun deleteVideo() {

        // given
        val video: Video = videoRepository.save(makeVideo(defaultAdmin, category))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                delete("/admin/video/{video_id}", video.id)
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_delete_video",
                pathParameters(
                    parameterWithName("video_id").description("비디오 ID")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("비디오 ID")
                )
            )
        )
    }

    @Test
    fun topFixVideo() {

        // given
        val video: Video = videoRepository.save(makeVideo(defaultAdmin, category))
        val request = BooleanDto(true)

        val result: ResultActions = mockMvc
            .perform(
                patch("/admin/video/{video_id}/fix", video.id)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_fix_video",
                pathParameters(
                    parameterWithName("video_id").description("비디오 ID")
                ),
                requestFields(
                    fieldWithPath("bool").type(BOOLEAN).description("상단 고정 여부")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("비디오 ID")
                )
            )
        )
    }

    @Test
    fun recommendVideo() {


        // given
        val video: Video = videoRepository.save(makeVideo(defaultAdmin, category))
        val request = BooleanDto(true)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                patch("/admin/video/{video_id}/recommend", video.id)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_recommend_video",
                pathParameters(
                    parameterWithName("video_id").description("비디오 ID")
                ),
                requestFields(
                    fieldWithPath("bool").type(BOOLEAN).description("추천 여부")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("비디오 ID")
                )
            )
        )
    }

    @Test
    fun changeOrderVideo() {

        // given
        val videos: List<Video> = videoRepository.saveAll(makeVideos(3, category, defaultAdmin))

        val ids: List<Long> = videos
            .map { video -> video.id }
            .shuffled()

        val request = SortOrderDto(ids)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                patch("/admin/video/order")
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_change_order_video",
                requestFields(
                    fieldWithPath("sorted_ids").type(ARRAY)
                        .description("상단 고정된 비디오 순서")
                ),
                responseFields(
                    fieldWithPath("sorted_ids").type(ARRAY)
                        .description("상단 고정된 비디오 순서")
                )
            )
        )
    }
}
