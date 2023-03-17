package com.jocoos.mybeautip.domain.video.api.internal

import com.jocoos.mybeautip.comment.CreateCommentRequest
import com.jocoos.mybeautip.comment.UpdateCommentRequest
import com.jocoos.mybeautip.domain.video.persistence.domain.VideoCategory
import com.jocoos.mybeautip.domain.video.persistence.repository.VideoCategoryRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateMilliFormat
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.GRANT_TYPE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.VIDEO_MASK_TYPE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.member.LegacyMemberService
import com.jocoos.mybeautip.member.Member
import com.jocoos.mybeautip.member.MemberRepository
import com.jocoos.mybeautip.testutil.fixture.makeMember
import com.jocoos.mybeautip.testutil.fixture.makeVideo
import com.jocoos.mybeautip.testutil.fixture.makeVideoCategory
import com.jocoos.mybeautip.video.Video
import com.jocoos.mybeautip.video.VideoRepository
import org.hamcrest.CoreMatchers.notNullValue
import org.json.JSONObject
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.mockito.BDDMockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.payload.ResponseFieldsSnippet
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*


@TestInstance(PER_CLASS)
class InternalVideoControllerTest(
    private val videoRepository: VideoRepository,
    private val videoCategoryRepository: VideoCategoryRepository,
    private val memberRepository: MemberRepository
) : RestDocsIntegrationTestSupport() {

    private lateinit var category: VideoCategory
    private lateinit var member: Member
    val guestId = "guest:1182547632897"

    companion object {
        const val MEMBER_ID = "MEMBER-ID"

    }

    @MockBean
    private val legacyMemberService: LegacyMemberService? = null

    @BeforeAll
    fun beforeAll() {
        member = memberRepository.save(makeMember())
        category = videoCategoryRepository.save(makeVideoCategory())
    }

    @AfterAll
    fun afterAll() {
        videoCategoryRepository.delete(category)
        memberRepository.delete(member)
    }

    @BeforeEach
    fun setUp() {
        BDDMockito.given(legacyMemberService!!.currentMember()).willReturn(member)
        BDDMockito.given(legacyMemberService.currentMemberId()).willReturn(member.id)
        BDDMockito.given(legacyMemberService.hasCommentPostPermission(member)).willReturn(true)
    }

    @Test
    fun getVideos() {
        // given
        videoRepository.save(makeVideo(defaultAdmin, category))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/internal/1/videos")
                    .param("category_id", category.id.toString())
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, member.id)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_get_videos",
                requestParameters(
                    parameterWithName("category_id").description("비디오 카테고리 아이디").optional(),
                    parameterWithName("cursor").description("커서").optional().attributes(
                        getZonedDateMilliFormat(),
                        getDefault("현재 시간")
                    ),
                    parameterWithName("count").description("조회갯수").optional()
                        .attributes(getDefault(50))
                ),
                responseFields(
                    fieldWithPath("next_cursor").type(STRING).description("커서 정보")
                        .attributes(getZonedDateMilliFormat()),
                    fieldWithPath("content").type(ARRAY).description("비디오 글 목록"),
                    fieldWithPath("content.[].id").type(NUMBER).description("비디오 ID"),
                    fieldWithPath("content.[].video_key").type(STRING).description("비디오 키"),
                    fieldWithPath("content.[].live_key").type(STRING).description("라이브 키").optional(),
                    fieldWithPath("content.[].output_type").type(STRING).description("").optional(),
                    fieldWithPath("content.[].type").type(STRING).description("방송 타입. UPLOADED, BROADCASTED"),
                    fieldWithPath("content.[].state").type(STRING).description("방송 상태. VOD 뿐."),
                    fieldWithPath("content.[].locked").type(BOOLEAN).description("잠금 여부"),
                    fieldWithPath("content.[].muted").type(BOOLEAN).description("음소거 여부"),
                    fieldWithPath("content.[].visibility").type(STRING).description("노출 여부"),
                    fieldWithPath("content.[].category").type(ARRAY).description("카테고리 정보").optional(),
                    fieldWithPath("content.[].category.[].id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("content.[].category.[].type").type(STRING).description("카테고리 구분"),
                    fieldWithPath("content.[].category.[].title").type(STRING).description("카테고리 타이틀"),
                    fieldWithPath("content.[].category.[].shape_url").type(STRING).description("카테고리 쉐입 URL")
                        .optional(),
                    fieldWithPath("content.[].category.[].mask_type").type(STRING)
                        .description(generateLinkCode(VIDEO_MASK_TYPE)).optional(),
                    fieldWithPath("content.[].title").type(STRING).description("제목").optional(),
                    fieldWithPath("content.[].content").type(STRING).description("컨텐츠").optional(),
                    fieldWithPath("content.[].url").type(STRING).description("비디오 파일 주소"),
                    fieldWithPath("content.[].original_filename").type(STRING).description("비디오 파일명").optional(),
                    fieldWithPath("content.[].thumbnail_path").type(STRING).description("썸네일 경로").optional(),
                    fieldWithPath("content.[].thumbnail_url").type(STRING).description("썸네일 URL").optional(),
                    fieldWithPath("content.[].chat_room_id").type(STRING).description("채팅방 아이디").optional(),
                    fieldWithPath("content.[].duration").type(NUMBER).description("방송 길이. mm 초 단위"),
                    fieldWithPath("content.[].total_watch_count").type(NUMBER).description("총 시청").optional(),
                    fieldWithPath("content.[].real_watch_count").type(NUMBER).description("실시청자수").optional(),
                    fieldWithPath("content.[].watch_count").type(NUMBER).description("실시간 시청자수"),
                    fieldWithPath("content.[].view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("content.[].heart_count").type(NUMBER).description("하트수"),
                    fieldWithPath("content.[].like_count").type(NUMBER).description("좋아요 수"),
                    fieldWithPath("content.[].comment_count").type(NUMBER).description("댓글수"),
                    fieldWithPath("content.[].order_count").type(NUMBER).description("주문수"),
                    fieldWithPath("content.[].report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("content.[].data").type(STRING).description("상품 정보등").optional(),
                    fieldWithPath("content.[].related_goods_count").type(NUMBER).description("관련 상품 갯수").optional(),
                    fieldWithPath("content.[].related_goods_thumbnail_url").type(STRING).description("상품 대표 URL")
                        .optional(),
                    fieldWithPath("content.[].like_id").type(NUMBER).description("좋아요 아이디").optional(),
                    fieldWithPath("content.[].scrap_id").type(NUMBER).description("스크랩 아이디").optional(),
                    fieldWithPath("content.[].blocked").type(BOOLEAN).description("차단 여부").optional(),
                    fieldWithPath("content.[].owner").type(OBJECT).description("비디오 작성자 정보"),
                    fieldWithPath("content.[].owner.id").type(NUMBER).description("아이디"),
                    fieldWithPath("content.[].owner.tag").type(STRING).description("태그"),
                    fieldWithPath("content.[].owner.status").type(STRING).description("상태"),
                    fieldWithPath("content.[].owner.grant_type").type(STRING).description(generateLinkCode(GRANT_TYPE))
                        .optional(),
                    fieldWithPath("content.[].owner.username").type(STRING).description("유저명"),
                    fieldWithPath("content.[].owner.email").type(STRING).description("이메일"),
                    fieldWithPath("content.[].owner.phone_number").type(STRING).description("전화번호"),
                    fieldWithPath("content.[].owner.avatar_url").type(STRING).description("아바타 URL"),
                    fieldWithPath("content.[].owner.follower_count").type(NUMBER).description("팔로워 수"),
                    fieldWithPath("content.[].owner.following_count").type(NUMBER).description("팔로잉 수"),
                    fieldWithPath("content.[].owner.video_count").type(NUMBER).description("비디오 수"),
                    fieldWithPath("content.[].owner.created_at").type(NUMBER).description("회원가입일"),
                    fieldWithPath("content.[].owner.modified_at").type(NUMBER).description("정보수정일"),
                    fieldWithPath("content.[].owner.permission").type(OBJECT).description("권한").optional(),
                    fieldWithPath("content.[].owner.permission.chat_post").type(BOOLEAN).description("post 권한")
                        .optional(),
                    fieldWithPath("content.[].owner.permission.comment_post").type(BOOLEAN).description("댓글 권한")
                        .optional(),
                    fieldWithPath("content.[].owner.permission.live_post").type(BOOLEAN).description("라이브 권한")
                        .optional(),
                    fieldWithPath("content.[].owner.permission.motd_post").type(BOOLEAN).description("motd 권한")
                        .optional(),
                    fieldWithPath("content.[].owner.permission.revenue_return").type(BOOLEAN).description("수익배분 권한")
                        .optional(),
                    fieldWithPath("content.[].created_at").type(STRING).description("생성 일자")
                        .attributes(getZonedDateMilliFormat())
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
                get("/internal/1/videos/{video_id}", video.id)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, member.id)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_get_video",
                pathParameters(
                    parameterWithName("video_id").description("비디오 아이디")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("비디오 ID"),
                    fieldWithPath("video_key").type(STRING).description("비디오 키"),
                    fieldWithPath("live_key").type(STRING).description("라이브 키").optional(),
                    fieldWithPath("output_type").type(STRING).description("").optional(),
                    fieldWithPath("type").type(STRING).description("방송 타입. UPLOADED, BROADCASTED"),
                    fieldWithPath("state").type(STRING).description("방송 상태. VOD 뿐."),
                    fieldWithPath("locked").type(BOOLEAN).description("잠금 여부"),
                    fieldWithPath("muted").type(BOOLEAN).description("음소거 여부").optional(),
                    fieldWithPath("visibility").type(STRING).description("노출 여부"),
                    fieldWithPath("category").type(ARRAY).description("카테고리 정보"),
                    fieldWithPath("category.[].id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("category.[].type").type(STRING).description("카테고리 구분"),
                    fieldWithPath("category.[].title").type(STRING).description("카테고리 타이틀"),
                    fieldWithPath("category.[].shape_url").type(STRING).description("카테고리 쉐입 URL"),
                    fieldWithPath("category.[].mask_type").type(STRING).description(generateLinkCode(VIDEO_MASK_TYPE)),
                    fieldWithPath("title").type(STRING).description("제목").optional(),
                    fieldWithPath("content").type(STRING).description("컨텐츠").optional(),
                    fieldWithPath("url").type(STRING).description("비디오 파일 주소").optional(),
                    fieldWithPath("original_filename").type(STRING).description("비디오 파일명").optional(),
                    fieldWithPath("thumbnail_path").type(STRING).description("썸네일 경로").optional(),
                    fieldWithPath("thumbnail_url").type(STRING).description("썸네일 URL").optional(),
                    fieldWithPath("chat_room_id").type(STRING).description("채팅방 아이디").optional(),
                    fieldWithPath("duration").type(NUMBER).description("방송 길이. mm 초 단위"),
                    fieldWithPath("total_watch_count").type(NUMBER).description("총 시청").optional(),
                    fieldWithPath("real_watch_count").type(NUMBER).description("실시청자수").optional(),
                    fieldWithPath("watch_count").type(NUMBER).description("실시간 시청자수"),
                    fieldWithPath("view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("heart_count").type(NUMBER).description("하트수"),
                    fieldWithPath("like_count").type(NUMBER).description("좋아요 수"),
                    fieldWithPath("comment_count").type(NUMBER).description("댓글수"),
                    fieldWithPath("order_count").type(NUMBER).description("주문수"),
                    fieldWithPath("report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("data").type(STRING).description("상품 정보등").optional(),
                    fieldWithPath("related_goods_count").type(NUMBER).description("관련 상품 갯수").optional(),
                    fieldWithPath("related_goods_thumbnail_url").type(STRING).description("상품 대표 URL").optional(),
                    fieldWithPath("like_id").type(NUMBER).description("좋아요 아이디").optional(),
                    fieldWithPath("scrap_id").type(NUMBER).description("스크랩 아이디").optional(),
                    fieldWithPath("blocked").type(BOOLEAN).description("차단 여부").optional(),
                    fieldWithPath("created_at").type(STRING).description("생성 일자").attributes(getZonedDateMilliFormat())
                )
            )
        )
    }

    @Test
    fun addViewCount() {

        // given
        val video: Video = videoRepository.save(makeVideo(defaultAdmin, category))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                patch("/internal/1/video/{video_id}/view-count", video.id)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, member.id)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_add_view_count_video",
                pathParameters(
                    parameterWithName("video_id").description("비디오 아이디")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("비디오 ID"),
                    fieldWithPath("view_count").type(NUMBER).description("비디오 조회수")
                )
            )
        )
    }

    @Test
    fun videoLike() {
        // given
        val video: Video = videoRepository.save(makeVideo(defaultAdmin, category))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                post("/internal/1/videos/{video_id}/likes", video.id)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, member.id)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_add_video_like",
                pathParameters(
                    parameterWithName("video_id").description("비디오 아이디")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("좋아요 ID"),
                    fieldWithPath("created_at").type(NUMBER).description("생성 일자"),
                    fieldWithPath("video.id").type(NUMBER).description("비디오 ID"),
                    fieldWithPath("video.video_key").type(STRING).description("비디오 키"),
                    fieldWithPath("video.live_key").type(STRING).description("라이브 키").optional(),
                    fieldWithPath("video.output_type").type(STRING).description("").optional(),
                    fieldWithPath("video.type").type(STRING).description("방송 타입. UPLOADED, BROADCASTED"),
                    fieldWithPath("video.state").type(STRING).description("방송 상태. VOD 뿐."),
                    fieldWithPath("video.locked").type(BOOLEAN).description("잠금 여부"),
                    fieldWithPath("video.muted").type(BOOLEAN).description("음소거 여부"),
                    fieldWithPath("video.visibility").type(STRING).description("노출 여부"),
                    fieldWithPath("video.category_names").type(STRING).description("카테고리 타이틀 묶음 정보").optional(),
                    fieldWithPath("video.category").type(ARRAY).description("카테고리 정보").optional(),
                    fieldWithPath("video.category.[].id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("video.category.[].type").type(STRING).description("카테고리 구분"),
                    fieldWithPath("video.category.[].title").type(STRING).description("카테고리 타이틀"),
                    fieldWithPath("video.category.[].shape_url").type(STRING).description("카테고리 쉐입 URL").optional(),
                    fieldWithPath("video.category.[].mask_type").type(STRING)
                        .description(generateLinkCode(VIDEO_MASK_TYPE)).optional(),
                    fieldWithPath("video.title").type(STRING).description("제목").optional(),
                    fieldWithPath("video.content").type(STRING).description("컨텐츠").optional(),
                    fieldWithPath("video.url").type(STRING).description("비디오 파일 주소"),
                    fieldWithPath("video.original_filename").type(STRING).description("비디오 파일명").optional(),
                    fieldWithPath("video.thumbnail_path").type(STRING).description("썸네일 경로").optional(),
                    fieldWithPath("video.thumbnail_url").type(STRING).description("썸네일 URL").optional(),
                    fieldWithPath("video.chat_room_id").type(STRING).description("채팅방 아이디").optional(),
                    fieldWithPath("video.duration").type(NUMBER).description("방송 길이. mm 초 단위"),
                    fieldWithPath("video.total_watch_count").type(NUMBER).description("총 시청").optional(),
                    fieldWithPath("video.real_watch_count").type(NUMBER).description("실시청자수").optional(),
                    fieldWithPath("video.watch_count").type(NUMBER).description("실시간 시청자수"),
                    fieldWithPath("video.view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("video.heart_count").type(NUMBER).description("하트수"),
                    fieldWithPath("video.like_count").type(NUMBER).description("좋아요 수"),
                    fieldWithPath("video.comment_count").type(NUMBER).description("댓글수"),
                    fieldWithPath("video.order_count").type(NUMBER).description("주문수"),
                    fieldWithPath("video.report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("video.data").type(STRING).description("상품 정보등").optional(),
                    fieldWithPath("video.related_goods_count").type(NUMBER).description("관련 상품 갯수").optional(),
                    fieldWithPath("video.related_goods_thumbnail_url").type(STRING).description("상품 대표 URL").optional(),
                    fieldWithPath("video.like_id").type(NUMBER).description("좋아요 아이디").optional(),
                    fieldWithPath("video.scrap_id").type(NUMBER).description("스크랩 아이디").optional(),
                    fieldWithPath("video.blocked").type(BOOLEAN).description("차단 여부").optional(),
                    fieldWithPath("video.created_at").type(NUMBER).description("생성 일자")
                )
            )
        )
    }

    @Test
    fun deleteVideoLike() {
        // given
        val video: Video = videoRepository.save(makeVideo(defaultAdmin, category))
        val likeResult: MvcResult = mockMvc
            .perform(
                post("/internal/1/videos/{video_id}/likes", video.id)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, member.id)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", notNullValue()))
            .andReturn();

        println(likeResult.response.contentAsString);
        val jsonObject = JSONObject(likeResult.response.contentAsString)
        val likeId : Long = jsonObject.getLong("id");

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                delete("/internal/1/videos/{video_id}/likes/{like_id}", video.id, likeId)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, member.id)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_delete_video_like",
                pathParameters(
                    parameterWithName("video_id").description("비디오 아이디"),
                    parameterWithName("like_id").description("좋아요 아이디")
                )
            )
        )
    }

    class VideoReportRequest(val reason: String, val reasonCode: Int)

    @Test
    fun reportVideo() {
        // given
        val video: Video = videoRepository.save(makeVideo(defaultAdmin, category))
        val req = VideoReportRequest("혐오", 0)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                post("/internal/1/videos/{video_id}/report", video.id)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, member.id)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_report_video",
                pathParameters(
                    parameterWithName("video_id").description("비디오 아이디")
                ),
                PayloadDocumentation.requestFields(
                    fieldWithPath("reason").type(STRING).description("신고 사유"),
                    fieldWithPath("reason_code").type(NUMBER).description("신고 코드").optional()
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("비디오 ID"),
                    fieldWithPath("video_key").type(STRING).description("비디오 키"),
                    fieldWithPath("live_key").type(STRING).description("라이브 키").optional(),
                    fieldWithPath("output_type").type(STRING).description("").optional(),
                    fieldWithPath("type").type(STRING).description("방송 타입. UPLOADED, BROADCASTED"),
                    fieldWithPath("state").type(STRING).description("방송 상태. VOD 뿐."),
                    fieldWithPath("locked").type(BOOLEAN).description("잠금 여부"),
                    fieldWithPath("muted").type(BOOLEAN).description("음소거 여부"),
                    fieldWithPath("visibility").type(STRING).description("노출 여부"),
                    fieldWithPath("category_names").type(STRING).description("카테고리 타이틀 묶음 정보").optional(),
                    fieldWithPath("category").type(ARRAY).description("카테고리 정보").optional(),
                    fieldWithPath("category.[].id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("category.[].type").type(STRING).description("카테고리 구분"),
                    fieldWithPath("category.[].title").type(STRING).description("카테고리 타이틀"),
                    fieldWithPath("category.[].shape_url").type(STRING).description("카테고리 쉐입 URL").optional(),
                    fieldWithPath("category.[].mask_type").type(STRING)
                        .description(generateLinkCode(VIDEO_MASK_TYPE)).optional(),
                    fieldWithPath("title").type(STRING).description("제목").optional(),
                    fieldWithPath("content").type(STRING).description("컨텐츠").optional(),
                    fieldWithPath("url").type(STRING).description("비디오 파일 주소"),
                    fieldWithPath("original_filename").type(STRING).description("비디오 파일명").optional(),
                    fieldWithPath("thumbnail_path").type(STRING).description("썸네일 경로").optional(),
                    fieldWithPath("thumbnail_url").type(STRING).description("썸네일 URL").optional(),
                    fieldWithPath("chat_room_id").type(STRING).description("채팅방 아이디").optional(),
                    fieldWithPath("duration").type(NUMBER).description("방송 길이. mm 초 단위"),
                    fieldWithPath("total_watch_count").type(NUMBER).description("총 시청").optional(),
                    fieldWithPath("real_watch_count").type(NUMBER).description("실시청자수").optional(),
                    fieldWithPath("watch_count").type(NUMBER).description("실시간 시청자수"),
                    fieldWithPath("view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("heart_count").type(NUMBER).description("하트수"),
                    fieldWithPath("like_count").type(NUMBER).description("좋아요 수"),
                    fieldWithPath("comment_count").type(NUMBER).description("댓글수"),
                    fieldWithPath("order_count").type(NUMBER).description("주문수"),
                    fieldWithPath("report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("data").type(STRING).description("상품 정보등").optional(),
                    fieldWithPath("related_goods_count").type(NUMBER).description("관련 상품 갯수").optional(),
                    fieldWithPath("related_goods_thumbnail_url").type(STRING).description("상품 대표 URL").optional(),
                    fieldWithPath("like_id").type(NUMBER).description("좋아요 아이디").optional(),
                    fieldWithPath("scrap_id").type(NUMBER).description("스크랩 아이디").optional(),
                    fieldWithPath("blocked").type(BOOLEAN).description("차단 여부").optional(),
                    fieldWithPath("created_at").type(NUMBER).description("생성 일자")
                )
            )
        )
    }

    @Test
    fun reportVideoComment() {
        // given
        val video: Video = videoRepository.save(makeVideo(defaultAdmin, category))
        val commentReq = CreateCommentRequest.builder().comment("댓글").build()

        val commentResult: MvcResult = mockMvc
            .perform(
                post("/internal/1/videos/{video_id}/comments", video.id)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, member.id)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(commentReq))
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", notNullValue()))
            .andReturn();

        val jsonObject = JSONObject(commentResult.response.contentAsString)
        val commentId : Long = jsonObject.getLong("id");
        val reportReq = VideoReportRequest("혐오", 0)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                post("/internal/2/videos/{video_id}/comments/{comment_id}/report", video.id, commentId)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, member.id)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reportReq))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_report_video_comment",
                pathParameters(
                    parameterWithName("video_id").description("비디오 아이디"),
                    parameterWithName("comment_id").description("댓글 아이디")
                ),
                PayloadDocumentation.requestFields(
                    fieldWithPath("reason").type(STRING).description("신고 사유"),
                    fieldWithPath("reason_code").type(NUMBER).description("신고 코드").optional()
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("신고 ID"),
                    fieldWithPath("comment_id").type(NUMBER).description("댓글 ID"),
                    fieldWithPath("reason_code").type(NUMBER).description("신고 코드"),
                    fieldWithPath("reason").type(STRING).description("신고 사유"),
                    fieldWithPath("created_by").type(OBJECT).description("신고자 정보"),
                    fieldWithPath("created_by.id").type(NUMBER).description("신고자 ID"),
                    fieldWithPath("created_by.username").type(STRING).description("신고자 닉네임"),
                    fieldWithPath("created_by.created_at").type(NUMBER).description("신고자 등록일자").ignored()
                )
            )
        )
    }

    @Test
    fun getVideoComment() {
        // given
        val request = createCommentRequest()
        val video: Video = videoRepository.save(makeVideo(defaultAdmin, category))

        // when & then
        mockMvc
            .perform(
                post("/internal/1/videos/{video_id}/comments", video.id)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, member.id)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        mockMvc
            .perform(
                post("/internal/1/videos/{video_id}/comments", video.id)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, member.id)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/internal/1/videos/{video_id}/comments", video.id)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, member.id)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_get_video_comment",
                pathParameters(
                    parameterWithName("video_id").description("비디오 ID")
                ),
                responseFields(
                    fieldWithPath("content").type(ARRAY).description("댓글 목록"),
                    fieldWithPath("content.[].id").type(NUMBER).description("댓글 ID"),
                    fieldWithPath("content.[].video_id").type(NUMBER).description("비디오 ID"),
                    fieldWithPath("content.[].locked").type(BOOLEAN).description("잠금 여부"),
                    fieldWithPath("content.[].comment").type(STRING).description("댓글 내용").optional(),
                    fieldWithPath("content.[].file_url").type(STRING).description("댓글 파일 URL").optional(),
                    fieldWithPath("content.[].parent_id").type(NUMBER).description("부모 댓글 ID").optional(),
                    fieldWithPath("content.[].comment_count").type(NUMBER).description("대댓글수"),
                    fieldWithPath("content.[].created_at").type(NUMBER).description("댓글 생성일"),
                    fieldWithPath("content.[].comment_ref").type(STRING).description("댓글 ref").optional(),
                    fieldWithPath("content.[].like_count").type(NUMBER).description("댓글 좋아요 수"),
                    fieldWithPath("content.[].report_count").type(NUMBER).description("댓글 신고 수"),
                    fieldWithPath("content.[].state").type(NUMBER).description("댓글 상태"),
                    fieldWithPath("content.[].mention_info").type(ARRAY).description("댓글 멘션 정보").optional(),
                    fieldWithPath("content.[].mention_info.[].username").type(STRING).description("댓글 멘션 닉네임"),
                    fieldWithPath("content.[].mention_info.[].member_id").type(NUMBER).description("댓글 멘션 아이디"),
                    fieldWithPath("total_count").type(NUMBER).description("댓글 개수"),
                    fieldWithPath("next_ref").type(STRING).description("다음 댓글 주소").optional(),
                    fieldWithPath("next_cursor").type(STRING).description("다음 댓글 커서").optional(),
                )
            )
        )
    }

    @Test
    fun writeVideoComment() {

        // given
        val request = createCommentRequest()
        val video: Video = videoRepository.save(makeVideo(defaultAdmin, category))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                post("/internal/1/videos/{video_id}/comments", video.id)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, member.id)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_write_video_comment",
                pathParameters(
                    parameterWithName("video_id").description("비디오 ID")
                ),
                PayloadDocumentation.requestFields(
                    fieldWithPath("comment").type(STRING).description("댓글 내용").optional(),
                    fieldWithPath("parent_id").type(NUMBER).description("댓글 내용").optional(),
                    fieldWithPath("mention_tags").type(ARRAY).description("댓글 내용").optional(),
                    fieldWithPath("mention_tags.[].username").type(STRING).description("댓글 작성자 아이디"),
                    fieldWithPath("mention_tags.[].member_id").type(NUMBER).description("댓글 작성자 아이디"),
                    fieldWithPath("file").type(OBJECT).description("파일 객체").optional().optional(),
                    fieldWithPath("file.type").type(STRING).description("파일 타입").ignored(),
                    fieldWithPath("file.operation").type(STRING)
                        .description(generateLinkCode(DocumentLinkGenerator.DocUrl.FILE_OPERATION_TYPE)),
                    fieldWithPath("file.url").type(STRING).description("파일 URL"),
                ),
                commentInfoResponse()
            )
        )
    }

    @Test
    fun editVideoComment() {

        // given
        val video: Video = videoRepository.save(makeVideo(defaultAdmin, category))
        val commentId = makeVideoComment(video)
        val request = UpdateCommentRequest("comment", listOf())

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                patch("/internal/1/videos/{video_id}/comments/{comment_id}", video.id, commentId)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, member.id)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_edit_video_comment",
                pathParameters(
                    parameterWithName("video_id").description("비디오 ID"),
                    parameterWithName("comment_id").description("댓글 ID"),
                ),
                PayloadDocumentation.requestFields(
                    fieldWithPath("comment").type(STRING).description("댓글 내용").optional(),
                    fieldWithPath("files").type(ARRAY).description("파일 객체 리스트").optional().optional(),
                    fieldWithPath("files.[].type").type(STRING).description("파일 타입").ignored(),
                    fieldWithPath("files.[].operation").type(STRING)
                        .description(generateLinkCode(DocumentLinkGenerator.DocUrl.FILE_OPERATION_TYPE)),
                    fieldWithPath("files.[].url").type(STRING).description("파일 URL"),
                ),
                commentInfoResponse()
            )
        )
    }

    @Test
    fun deleteVideoComment() {

        // given
        val video: Video = videoRepository.save(makeVideo(defaultAdmin, category))
        val commentId = makeVideoComment(video)

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                delete("/internal/1/videos/{video_id}/comments/{comment_id}", video.id, commentId)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, member.id)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_delete_video_comment",
                pathParameters(
                    parameterWithName("video_id").description("비디오 ID"),
                    parameterWithName("comment_id").description("댓글 ID"),
                ),
                responseFields(
                    fieldWithPath("state").type(NUMBER).description("댓글 상태 (0: FAILED, 1: DELETED)")
                )
            )
        )
    }

    private fun makeVideoComment(video: Video): Long {
        // given
        val result: ResultActions = mockMvc
            .perform(
                post("/internal/1/videos/{video_id}/comments", video.id)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, member.id)
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


    @Test
    fun getVideosAsGuest() {
        // given
        videoRepository.save(makeVideo(defaultAdmin, category))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/internal/1/videos")
                    .param("category_id", category.id.toString())
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, guestId)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_get_videos_as_guest",
                requestParameters(
                    parameterWithName("category_id").description("비디오 카테고리 아이디").optional(),
                    parameterWithName("cursor").description("커서").optional().attributes(
                        getZonedDateMilliFormat(),
                        getDefault("현재 시간")
                    ),
                    parameterWithName("count").description("조회갯수").optional()
                        .attributes(getDefault(50))
                ),
                responseFields(
                    fieldWithPath("next_cursor").type(STRING).description("커서 정보")
                        .attributes(getZonedDateMilliFormat()),
                    fieldWithPath("content").type(ARRAY).description("비디오 글 목록"),
                    fieldWithPath("content.[].id").type(NUMBER).description("비디오 ID"),
                    fieldWithPath("content.[].video_key").type(STRING).description("비디오 키"),
                    fieldWithPath("content.[].live_key").type(STRING).description("라이브 키").optional(),
                    fieldWithPath("content.[].output_type").type(STRING).description("").optional(),
                    fieldWithPath("content.[].type").type(STRING).description("방송 타입. UPLOADED, BROADCASTED"),
                    fieldWithPath("content.[].state").type(STRING).description("방송 상태. VOD 뿐."),
                    fieldWithPath("content.[].locked").type(BOOLEAN).description("잠금 여부"),
                    fieldWithPath("content.[].muted").type(BOOLEAN).description("음소거 여부"),
                    fieldWithPath("content.[].visibility").type(STRING).description("노출 여부"),
                    fieldWithPath("content.[].category").type(ARRAY).description("카테고리 정보").optional(),
                    fieldWithPath("content.[].category.[].id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("content.[].category.[].type").type(STRING).description("카테고리 구분"),
                    fieldWithPath("content.[].category.[].title").type(STRING).description("카테고리 타이틀"),
                    fieldWithPath("content.[].category.[].shape_url").type(STRING).description("카테고리 쉐입 URL")
                        .optional(),
                    fieldWithPath("content.[].category.[].mask_type").type(STRING)
                        .description(generateLinkCode(VIDEO_MASK_TYPE)).optional(),
                    fieldWithPath("content.[].title").type(STRING).description("제목").optional(),
                    fieldWithPath("content.[].content").type(STRING).description("컨텐츠").optional(),
                    fieldWithPath("content.[].url").type(STRING).description("비디오 파일 주소"),
                    fieldWithPath("content.[].original_filename").type(STRING).description("비디오 파일명").optional(),
                    fieldWithPath("content.[].thumbnail_path").type(STRING).description("썸네일 경로").optional(),
                    fieldWithPath("content.[].thumbnail_url").type(STRING).description("썸네일 URL").optional(),
                    fieldWithPath("content.[].chat_room_id").type(STRING).description("채팅방 아이디").optional(),
                    fieldWithPath("content.[].duration").type(NUMBER).description("방송 길이. mm 초 단위"),
                    fieldWithPath("content.[].total_watch_count").type(NUMBER).description("총 시청").optional(),
                    fieldWithPath("content.[].real_watch_count").type(NUMBER).description("실시청자수").optional(),
                    fieldWithPath("content.[].watch_count").type(NUMBER).description("실시간 시청자수"),
                    fieldWithPath("content.[].view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("content.[].heart_count").type(NUMBER).description("하트수"),
                    fieldWithPath("content.[].like_count").type(NUMBER).description("좋아요 수"),
                    fieldWithPath("content.[].comment_count").type(NUMBER).description("댓글수"),
                    fieldWithPath("content.[].order_count").type(NUMBER).description("주문수"),
                    fieldWithPath("content.[].report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("content.[].data").type(STRING).description("상품 정보등").optional(),
                    fieldWithPath("content.[].related_goods_count").type(NUMBER).description("관련 상품 갯수").optional(),
                    fieldWithPath("content.[].related_goods_thumbnail_url").type(STRING).description("상품 대표 URL")
                        .optional(),
                    fieldWithPath("content.[].like_id").type(NUMBER).description("좋아요 아이디").optional(),
                    fieldWithPath("content.[].scrap_id").type(NUMBER).description("스크랩 아이디").optional(),
                    fieldWithPath("content.[].blocked").type(BOOLEAN).description("차단 여부").optional(),
                    fieldWithPath("content.[].owner").type(OBJECT).description("비디오 작성자 정보"),
                    fieldWithPath("content.[].owner.id").type(NUMBER).description("아이디"),
                    fieldWithPath("content.[].owner.tag").type(STRING).description("태그"),
                    fieldWithPath("content.[].owner.status").type(STRING).description("상태"),
                    fieldWithPath("content.[].owner.grant_type").type(STRING).description(generateLinkCode(GRANT_TYPE))
                        .optional(),
                    fieldWithPath("content.[].owner.username").type(STRING).description("유저명"),
                    fieldWithPath("content.[].owner.email").type(STRING).description("이메일"),
                    fieldWithPath("content.[].owner.phone_number").type(STRING).description("전화번호"),
                    fieldWithPath("content.[].owner.avatar_url").type(STRING).description("아바타 URL"),
                    fieldWithPath("content.[].owner.follower_count").type(NUMBER).description("팔로워 수"),
                    fieldWithPath("content.[].owner.following_count").type(NUMBER).description("팔로잉 수"),
                    fieldWithPath("content.[].owner.video_count").type(NUMBER).description("비디오 수"),
                    fieldWithPath("content.[].owner.created_at").type(NUMBER).description("회원가입일"),
                    fieldWithPath("content.[].owner.modified_at").type(NUMBER).description("정보수정일"),
                    fieldWithPath("content.[].owner.permission").type(OBJECT).description("권한").optional(),
                    fieldWithPath("content.[].owner.permission.chat_post").type(BOOLEAN).description("post 권한")
                        .optional(),
                    fieldWithPath("content.[].owner.permission.comment_post").type(BOOLEAN).description("댓글 권한")
                        .optional(),
                    fieldWithPath("content.[].owner.permission.live_post").type(BOOLEAN).description("라이브 권한")
                        .optional(),
                    fieldWithPath("content.[].owner.permission.motd_post").type(BOOLEAN).description("motd 권한")
                        .optional(),
                    fieldWithPath("content.[].owner.permission.revenue_return").type(BOOLEAN).description("수익배분 권한")
                        .optional(),
                    fieldWithPath("content.[].created_at").type(STRING).description("생성 일자")
                        .attributes(getZonedDateMilliFormat())
                )
            )
        )
    }

    @Test
    fun getVideoAsGuest() {

        // given
        val video: Video = videoRepository.save(makeVideo(defaultAdmin, category))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/internal/1/videos/{video_id}", video.id)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, member.id)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_get_video_as_guest",
                pathParameters(
                    parameterWithName("video_id").description("비디오 아이디")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("비디오 ID"),
                    fieldWithPath("video_key").type(STRING).description("비디오 키"),
                    fieldWithPath("live_key").type(STRING).description("라이브 키").optional(),
                    fieldWithPath("output_type").type(STRING).description("").optional(),
                    fieldWithPath("type").type(STRING).description("방송 타입. UPLOADED, BROADCASTED"),
                    fieldWithPath("state").type(STRING).description("방송 상태. VOD 뿐."),
                    fieldWithPath("locked").type(BOOLEAN).description("잠금 여부"),
                    fieldWithPath("muted").type(BOOLEAN).description("음소거 여부").optional(),
                    fieldWithPath("visibility").type(STRING).description("노출 여부"),
                    fieldWithPath("category").type(ARRAY).description("카테고리 정보"),
                    fieldWithPath("category.[].id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("category.[].type").type(STRING).description("카테고리 구분"),
                    fieldWithPath("category.[].title").type(STRING).description("카테고리 타이틀"),
                    fieldWithPath("category.[].shape_url").type(STRING).description("카테고리 쉐입 URL"),
                    fieldWithPath("category.[].mask_type").type(STRING).description(generateLinkCode(VIDEO_MASK_TYPE)),
                    fieldWithPath("title").type(STRING).description("제목").optional(),
                    fieldWithPath("content").type(STRING).description("컨텐츠").optional(),
                    fieldWithPath("url").type(STRING).description("비디오 파일 주소").optional(),
                    fieldWithPath("original_filename").type(STRING).description("비디오 파일명").optional(),
                    fieldWithPath("thumbnail_path").type(STRING).description("썸네일 경로").optional(),
                    fieldWithPath("thumbnail_url").type(STRING).description("썸네일 URL").optional(),
                    fieldWithPath("chat_room_id").type(STRING).description("채팅방 아이디").optional(),
                    fieldWithPath("duration").type(NUMBER).description("방송 길이. mm 초 단위"),
                    fieldWithPath("total_watch_count").type(NUMBER).description("총 시청").optional(),
                    fieldWithPath("real_watch_count").type(NUMBER).description("실시청자수").optional(),
                    fieldWithPath("watch_count").type(NUMBER).description("실시간 시청자수"),
                    fieldWithPath("view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("heart_count").type(NUMBER).description("하트수"),
                    fieldWithPath("like_count").type(NUMBER).description("좋아요 수"),
                    fieldWithPath("comment_count").type(NUMBER).description("댓글수"),
                    fieldWithPath("order_count").type(NUMBER).description("주문수"),
                    fieldWithPath("report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("data").type(STRING).description("상품 정보등").optional(),
                    fieldWithPath("related_goods_count").type(NUMBER).description("관련 상품 갯수").optional(),
                    fieldWithPath("related_goods_thumbnail_url").type(STRING).description("상품 대표 URL").optional(),
                    fieldWithPath("like_id").type(NUMBER).description("좋아요 아이디").optional(),
                    fieldWithPath("scrap_id").type(NUMBER).description("스크랩 아이디").optional(),
                    fieldWithPath("blocked").type(BOOLEAN).description("차단 여부").optional(),
                    fieldWithPath("created_at").type(STRING).description("생성 일자").attributes(getZonedDateMilliFormat())
                )
            )
        )
    }

    @Test
    fun addViewCountAsGuest() {

        // given
        val video: Video = videoRepository.save(makeVideo(defaultAdmin, category))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                patch("/internal/1/video/{video_id}/view-count", video.id)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, guestId)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_add_view_count_video_as_guest",
                pathParameters(
                    parameterWithName("video_id").description("비디오 아이디")
                ),
                responseFields(
                    fieldWithPath("id").type(NUMBER).description("비디오 ID"),
                    fieldWithPath("view_count").type(NUMBER).description("비디오 조회수")
                )
            )
        )
    }

    @Test
    fun getVideoCommentAsGuest() {
        // given
        val request = createCommentRequest()
        val video: Video = videoRepository.save(makeVideo(defaultAdmin, category))

        // when & then
        mockMvc
            .perform(
                post("/internal/1/videos/{video_id}/comments", video.id)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, member.id)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        mockMvc
            .perform(
                post("/internal/1/videos/{video_id}/comments", video.id)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, member.id)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk)
            .andDo(print())

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/internal/1/videos/{video_id}/comments", video.id)
                    .header(AUTHORIZATION, requestInternalToken)
                    .header(MEMBER_ID, guestId)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "internal_get_video_comment_as_guest",
                pathParameters(
                    parameterWithName("video_id").description("비디오 ID")
                ),
                responseFields(
                    fieldWithPath("content").type(ARRAY).description("댓글 목록"),
                    fieldWithPath("content.[].id").type(NUMBER).description("댓글 ID"),
                    fieldWithPath("content.[].video_id").type(NUMBER).description("비디오 ID"),
                    fieldWithPath("content.[].locked").type(BOOLEAN).description("잠금 여부"),
                    fieldWithPath("content.[].comment").type(STRING).description("댓글 내용").optional(),
                    fieldWithPath("content.[].file_url").type(STRING).description("댓글 파일 URL").optional(),
                    fieldWithPath("content.[].parent_id").type(NUMBER).description("부모 댓글 ID").optional(),
                    fieldWithPath("content.[].comment_count").type(NUMBER).description("대댓글수"),
                    fieldWithPath("content.[].created_at").type(NUMBER).description("댓글 생성일"),
                    fieldWithPath("content.[].comment_ref").type(STRING).description("댓글 ref").optional(),
                    fieldWithPath("content.[].like_count").type(NUMBER).description("댓글 좋아요 수"),
                    fieldWithPath("content.[].report_count").type(NUMBER).description("댓글 신고 수"),
                    fieldWithPath("content.[].state").type(NUMBER).description("댓글 상태"),
                    fieldWithPath("content.[].mention_info").type(ARRAY).description("댓글 멘션 정보").optional(),
                    fieldWithPath("content.[].mention_info.[].username").type(STRING).description("댓글 멘션 닉네임"),
                    fieldWithPath("content.[].mention_info.[].member_id").type(NUMBER).description("댓글 멘션 아이디"),
                    fieldWithPath("total_count").type(NUMBER).description("댓글 개수"),
                    fieldWithPath("next_ref").type(STRING).description("다음 댓글 주소").optional(),
                    fieldWithPath("next_cursor").type(STRING).description("다음 댓글 커서").optional(),
                )
            )
        )
    }
}
