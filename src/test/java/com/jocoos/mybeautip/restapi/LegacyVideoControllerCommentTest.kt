package com.jocoos.mybeautip.restapi

import com.jocoos.mybeautip.comment.CreateCommentRequest
import com.jocoos.mybeautip.comment.UpdateCommentRequest
import com.jocoos.mybeautip.domain.video.persistence.domain.VideoCategory
import com.jocoos.mybeautip.domain.video.persistence.repository.VideoCategoryRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.FILE_OPERATION_TYPE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.testutil.fixture.makeVideo
import com.jocoos.mybeautip.testutil.fixture.makeVideoCategory
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
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.payload.ResponseFieldsSnippet
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@TestInstance(PER_CLASS)
class LegacyVideoControllerCommentTest(
    private val videoCategoryRepository: VideoCategoryRepository,
    private val videoRepository: VideoRepository
) : RestDocsIntegrationTestSupport() {

    private lateinit var category: VideoCategory
    private lateinit var video: Video

    @BeforeAll
    fun beforeAll() {
        category = videoCategoryRepository.save(makeVideoCategory())
        video = videoRepository.save(makeVideo(member = defaultAdmin, category = category))
    }

    @AfterAll
    fun afterAll() {
        videoRepository.delete(video)
        videoCategoryRepository.delete(category)
    }

    @Test
    fun writeVideoComment() {

        // given
        val request = createCommentRequest()

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                post("/api/1/videos/{video_id}/comments", video.id)
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "write_video_comment",
                pathParameters(
                    parameterWithName("video_id").description("비디오 ID")
                ),
                requestFields(
                    fieldWithPath("comment").type(STRING).description("댓글 내용").optional(),
                    fieldWithPath("parent_id").type(NUMBER).description("댓글 내용").optional(),
                    fieldWithPath("mention_tags").type(ARRAY).description("댓글 내용").optional(),
                    fieldWithPath("mention_tags.[].username").type(STRING).description("댓글 작성자 아이디"),
                    fieldWithPath("mention_tags.[].member_id").type(NUMBER).description("댓글 작성자 아이디"),
                    fieldWithPath("file").type(OBJECT).description("파일 객체").optional().optional(),
                    fieldWithPath("file.type").type(STRING).description("파일 타입").ignored(),
                    fieldWithPath("file.operation").type(STRING).description(generateLinkCode(FILE_OPERATION_TYPE)),
                    fieldWithPath("file.url").type(STRING).description("파일 URL"),
                ),
                commentInfoResponse()
            )
        )
    }

    @Test
    fun editVideoComment() {

        // given
        val commentId = makeVideoComment()
        val request = UpdateCommentRequest("comment", listOf())

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                patch("/api/1/videos/{video_id}/comments/{comment_id}", video.id, commentId)
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "edit_video_comment",
                pathParameters(
                    parameterWithName("video_id").description("비디오 ID"),
                    parameterWithName("comment_id").description("댓글 ID"),
                ),
                requestFields(
                    fieldWithPath("comment").type(STRING).description("댓글 내용").optional(),
                    fieldWithPath("files").type(ARRAY).description("파일 객체 리스트").optional().optional(),
                    fieldWithPath("files.[].type").type(STRING).description("파일 타입").ignored(),
                    fieldWithPath("files.[].operation").type(STRING).description(generateLinkCode(FILE_OPERATION_TYPE)),
                    fieldWithPath("files.[].url").type(STRING).description("파일 URL"),
                ),
                commentInfoResponse()
            )
        )
    }

    private fun makeVideoComment(): Long {
        // given
        val result: ResultActions = mockMvc
            .perform(
                post("/api/1/videos/{video_id}/comments", video.id)
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createCommentRequest()))
            )

        val response: String = result.andReturn().response.contentAsString
        return objectMapper.readTree(response).path("id").asLong()
    }

    private fun createCommentRequest(): CreateCommentRequest =
        CreateCommentRequest.builder()
            .comment("comment")
            .build()

    private fun commentInfoResponse(): ResponseFieldsSnippet =
        responseFields(
            fieldWithPath("id").type(NUMBER).description("댓글 ID"),
            fieldWithPath("video_id").type(NUMBER).description("비디오 ID"),
            fieldWithPath("locked").type(BOOLEAN).description("잠금 여부"),
            fieldWithPath("comment").type(STRING).description("댓글 내용").optional(),
            fieldWithPath("file_url").type(STRING).description("댓글 파일 URL").optional(),
            fieldWithPath("parent_id").type(NUMBER).description("부모 댓글 ID").optional(),
            fieldWithPath("comment_count").type(NUMBER).description("대댓글수"),
            fieldWithPath("created_by").type(OBJECT).description("댓글 작성자 정보"),
            fieldWithPath("created_by.id").type(NUMBER).description("댓글 작성자 아이디"),
            fieldWithPath("created_by.tag").type(STRING).description("댓글 작성자 태그"),
            fieldWithPath("created_by.status").type(STRING).description("댓글 작성자 상태"),
            fieldWithPath("created_by.grant_type").type(STRING).description("댓글 작성자 가입 경로").optional(),
            fieldWithPath("created_by.username").type(STRING).description("댓글 작성자 닉네임"),
            fieldWithPath("created_by.email").type(STRING).description("댓글 작성자 이메일"),
            fieldWithPath("created_by.phone_number").type(STRING).description("댓글 작성자 전화번호"),
            fieldWithPath("created_by.avatar_url").type(STRING).description("댓글 작성자 프로필 이미지 URL"),
            fieldWithPath("created_by.permission").type(OBJECT).description("댓글 작성자 권한 정보"),
            fieldWithPath("created_by.permission.chat_post").type(BOOLEAN).description("댓글 작성자 채팅 권한"),
            fieldWithPath("created_by.permission.comment_post").type(BOOLEAN).description("댓글 작성자 댓글 작성 권한"),
            fieldWithPath("created_by.permission.live_post").type(BOOLEAN).description("댓글 작성자 라이브 권한"),
            fieldWithPath("created_by.permission.motd_post").type(BOOLEAN).description("댓글 작성자 motd 권한"),
            fieldWithPath("created_by.permission.revenue_return").type(BOOLEAN).description("댓글 작성자 매출 권한"),
            fieldWithPath("created_by.follower_count").type(NUMBER).description("댓글 작성자 팔로워 수"),
            fieldWithPath("created_by.following_count").type(NUMBER).description("댓글 작성자 팔로잉 수"),
            fieldWithPath("created_by.following_id").type(NUMBER).description("댓글 작성자 팔로잉 아이디").optional(),
            fieldWithPath("created_by.reported_id").type(NUMBER).description("댓글 작성자 신고자 아이디").optional(),
            fieldWithPath("created_by.blocked_id").type(NUMBER).description("댓글 작성자 차단 아이디").optional(),
            fieldWithPath("created_by.point").type(NUMBER).description("댓글 작성자 포인트").optional(),
            fieldWithPath("created_by.revenue").type(NUMBER).description("댓글 작성자 매출").optional(),
            fieldWithPath("created_by.point_ratio").type(NUMBER).description("댓글 작성자 포인트 비율").optional(),
            fieldWithPath("created_by.revenue_ratio").type(NUMBER).description("댓글 작성자 매출 비율").optional(),
            fieldWithPath("created_by.video_count").type(NUMBER).description("댓글 작성자 비디오 수"),
            fieldWithPath("created_by.revenue_modified_at").type(STRING).description("댓글 작성자 매출 수정일자").optional(),
            fieldWithPath("created_by.pushable").type(BOOLEAN).description("댓글 작성자 푸쉬알람 여부").optional(),
            fieldWithPath("created_by.option_term_accepts").type(ARRAY).description("댓글 작성자 선택 약관 정보").optional(),
            fieldWithPath("created_by.created_at").type(NUMBER).description("댓글 작성자 생성일자"),
            fieldWithPath("created_by.modified_at").type(NUMBER).description("댓글 작성자 수정일자"),
            fieldWithPath("created_by.deleted_at").type(NUMBER).description("댓글 작성자 삭제일자").optional(),
            fieldWithPath("created_at").type(NUMBER).description("댓글 생성일"),
            fieldWithPath("comment_ref").type(STRING).description("댓글 ref").optional(),
            fieldWithPath("like_count").type(NUMBER).description("댓글 좋아요 수"),
            fieldWithPath("report_count").type(NUMBER).description("댓글 신고 수"),
            fieldWithPath("state").type(NUMBER).description("댓글 상태"),
            fieldWithPath("mention_info").type(ARRAY).description("댓글 멘션 정보").optional(),
            fieldWithPath("mention_info.[].username").type(STRING).description("댓글 멘션 닉네임"),
            fieldWithPath("mention_info.[].member_id").type(NUMBER).description("댓글 멘션 아이디"),
        )
}
