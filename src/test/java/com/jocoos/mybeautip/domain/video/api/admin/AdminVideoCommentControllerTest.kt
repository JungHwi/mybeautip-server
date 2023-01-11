package com.jocoos.mybeautip.domain.video.api.admin

import com.jocoos.mybeautip.domain.video.dto.WriteVideoCommentRequest
import com.jocoos.mybeautip.domain.video.persistence.repository.VideoCategoryRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.global.dto.single.BooleanDto
import com.jocoos.mybeautip.testutil.fixture.makeVideo
import com.jocoos.mybeautip.testutil.fixture.makeVideoCategory
import com.jocoos.mybeautip.member.comment.CommentRepository
import com.jocoos.mybeautip.video.Video
import com.jocoos.mybeautip.video.VideoRepository
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@TestInstance(PER_CLASS)
class AdminVideoCommentControllerTest(
    private val videoRepository: VideoRepository,
    private val videoCategoryRepository: VideoCategoryRepository,
    private val videoCommentRepository: CommentRepository,
    @LocalServerPort private val port: Int
) : RestDocsIntegrationTestSupport() {

    private lateinit var video: Video;

    @BeforeAll
    fun beforeAll() {
        val videoCategory = videoCategoryRepository.save(makeVideoCategory())
        video = videoRepository.save(makeVideo(member = defaultAdmin, category = videoCategory))
    }

    @AfterAll
    fun afterAll() {
        videoCategoryRepository.deleteAll()
        videoCommentRepository.deleteAll()
        videoRepository.deleteAll()
    }


    @Test
    fun writeVideoComment() {

        // given
        val request = WriteVideoCommentRequest("content", null, null)

        // when & then
        val result = mockMvc.perform(
            post("/admin/video/{video_id}/comment", video.id)
                .header(AUTHORIZATION, defaultAdminToken)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andDo(print())

        result.andDo(
            document(
                "admin_write_video_comment",
                pathParameters(
                    parameterWithName("video_id").description("비디오 ID")
                ),
                requestFields(
                    fieldWithPath("parent_id").type(NUMBER).description("부모 댓글 아이디").optional(),
                    fieldWithPath("contents").type(STRING).description("내용")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("댓글 아이디"),
                    fieldWithPath("status").type(STRING).description(generateLinkCode(VIDEO_COMMENT_STATUS)),
                    fieldWithPath("contents").type(STRING).description("내용"),
                    fieldWithPath("file_url").type(STRING).description("이미지 URL").optional(),
                    fieldWithPath("like_count").type(NUMBER).description("좋아요수"),
                    fieldWithPath("report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("created_at").type(STRING).description("작성일").attributes(getZonedDateFormat()),
                    fieldWithPath("member").type(OBJECT).description("작성자 정보"),
                    fieldWithPath("member.id").type(NUMBER).description("작성자 아이디").optional(),
                    fieldWithPath("member.status").type(STRING).description(generateLinkCode(MEMBER_STATUS)),
                    fieldWithPath("member.username").type(STRING).description("작성자 이름").optional(),
                    fieldWithPath("member.avatar_url").type(STRING).description("작성자 아바타 URL").optional(),
                    fieldWithPath("member.role").type(STRING).description(generateLinkCode(ROLE))
                )
            )
        )
    }

    @Test
    fun editVideoComment() {

        val commentId: Long = makeVideoComment()
        val request = mapOf("contents" to "Test Contents")


        val result = mockMvc
            .perform(
                patch("/admin/video/{video_id}/comment/{comment_id}", video.id, commentId)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_edit_video_comment",
                pathParameters(
                    parameterWithName("video_id").description("비디오 ID"),
                    parameterWithName("comment_id").description("댓글 ID")
                ),
                requestFields(
                    fieldWithPath("contents").type(STRING).description("내용")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("댓글 ID")
                )
            )
        )
    }

    @Test
    fun getVideoComments() {

        // given
        makeVideoComment()

        val result = mockMvc
            .perform(
                get("/admin/video/{video_id}/comment", video.id)
                    .header(AUTHORIZATION, defaultAdminToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_video_comments",
                pathParameters(
                    parameterWithName("video_id").description("비디오 ID")
                ),
                requestParameters(
                    parameterWithName("page").description("페이지 넘버").optional()
                        .attributes(getDefault(1)),
                    parameterWithName("size").description("페이지 사이즈").optional()
                        .attributes(getDefault(10))
                ),
                responseFields(
                    fieldWithPath("total").type(NUMBER).description("총 비디오 댓글 개수"),
                    fieldWithPath("content").type(ARRAY).description("비디오 댓글 목록"),
                    fieldWithPath("content.[].id").type(NUMBER).description("비디오 댓글 ID"),
                    fieldWithPath("content.[].status").type(STRING).description(generateLinkCode(VIDEO_COMMENT_STATUS)),
                    fieldWithPath("content.[].contents").type(STRING).description("내용"),
                    fieldWithPath("content.[].file_url").type(STRING).description("이미지 URL").optional(),
                    fieldWithPath("content.[].like_count").type(NUMBER).description("좋아요수"),
                    fieldWithPath("content.[].report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("content.[].member").type(OBJECT).description("작성자 정보."),
                    fieldWithPath("content.[].member.id").type(NUMBER).description("작성자 아이디"),
                    fieldWithPath("content.[].member.status").type(STRING).description(generateLinkCode(MEMBER_STATUS)),
                    fieldWithPath("content.[].member.username").type(STRING).description("작성자 이름"),
                    fieldWithPath("content.[].member.avatar_url").type(STRING).description("작성자 아바타 URL"),
                    fieldWithPath("content.[].member.role").type(STRING).description(generateLinkCode(ROLE)),
                    fieldWithPath("content.[].created_at").type(STRING).description("작성일")
                        .attributes(getZonedDateFormat()),
                    fieldWithPath("content.[].children").type(ARRAY)
                        .description("대댓글 목록, children 필드가 없는 것을 제외하고 본 응답과 동일").optional(),
                    fieldWithPath("content.[].children.[].id").type(NUMBER).description("비디오 댓글 ID").optional()
                        .ignored(),
                    fieldWithPath("content.[].children.[].status").type(STRING)
                        .description(generateLinkCode(VIDEO_COMMENT_STATUS)).optional().ignored(),
                    fieldWithPath("content.[].children.[].contents").type(STRING).description("내용").optional()
                        .ignored(),
                    fieldWithPath("content.[].children.[].file_url").type(STRING).description("이미지 URL").optional()
                        .ignored(),
                    fieldWithPath("content.[].children.[].like_count").type(NUMBER).description("좋아요수").optional()
                        .ignored(),
                    fieldWithPath("content.[].children.[].report_count").type(NUMBER).description("신고수").optional()
                        .ignored(),
                    fieldWithPath("content.[].children.[].member").type(OBJECT).description("작성자 정보.").optional()
                        .ignored(),
                    fieldWithPath("content.[].children.[].member.id").type(NUMBER).description("작성자 아이디").optional()
                        .ignored(),
                    fieldWithPath("content.[].children.[].member.status").type(STRING)
                        .description(generateLinkCode(MEMBER_STATUS)).optional().ignored(),
                    fieldWithPath("content.[].children.[].member.username").type(STRING).description("작성자 이름")
                        .optional().ignored(),
                    fieldWithPath("content.[].children.[].member.avatar_url").type(STRING).description("작성자 아바타 URL")
                        .optional().ignored(),
                    fieldWithPath("content.[].children.[].member.role").type(STRING).description(generateLinkCode(ROLE))
                        .optional().ignored(),
                    fieldWithPath("content.[].children.[].created_at").type(STRING).description("작성일")
                        .attributes(getZonedDateFormat()).optional().ignored()
                )
            )
        )
    }

    @Test
    fun hideVideoComment() {

        // given
        val commentId: Long = makeVideoComment()
        val request = BooleanDto(true)

        // when & then
        val result = mockMvc
            .perform(
                patch("/admin/video/{video_id}/comment/{comment_id}/hide", video.id, commentId)
                    .header(AUTHORIZATION, defaultAdminToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_hide_video_comment",
                pathParameters(
                    parameterWithName("video_id").description("비디오 ID"),
                    parameterWithName("comment_id").description("비디오 댓글 ID")
                ),
                requestFields(
                    fieldWithPath("bool").type(BOOLEAN).description("숨김 여부")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("비디오 댓글 ID")
                )
            )
        )
    }

    private fun makeVideoComment(): Long {
        val request = WriteVideoCommentRequest("content", null, null)

        val id: Long = Given {
            port(port)
            log().all()
            contentType(APPLICATION_JSON_VALUE)
            header(AUTHORIZATION, defaultAdminToken)
            body(request)
        } When {
            post("/admin/video/{video_id}/comment", video.id)
        } Then {
            status().isCreated
            log().all()
        } Extract {
            path<Long?>("id").toLong()
        }

        return id
    }

//    private fun makeVideoComment(): Long {
//
//        // given
//        val request = WriteVideoCommentRequest("content", null, null)
//
//        // when & then
//        val result = mockMvc
//            .perform(
//                post("/admin/video/{video_id}/comment", video.id)
//                    .header(AUTHORIZATION, defaultAdminToken)
//                    .contentType(APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(request))
//            )
//
//        val contentAsString: String = result.andReturn().response.contentAsString
//        val response: AdminVideoCommentResponse =
//            objectMapper.readValue(contentAsString, AdminVideoCommentResponse::class.java)
//        return response.id
//    }
}
