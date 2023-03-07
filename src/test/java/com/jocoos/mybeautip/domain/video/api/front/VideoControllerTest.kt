package com.jocoos.mybeautip.domain.video.api.front

import com.jocoos.mybeautip.domain.video.persistence.domain.VideoCategory
import com.jocoos.mybeautip.domain.video.persistence.repository.VideoCategoryRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateMilliFormat
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.GRANT_TYPE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.VIDEO_MASK_TYPE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.testutil.fixture.makeVideo
import com.jocoos.mybeautip.testutil.fixture.makeVideoCategory
import com.jocoos.mybeautip.video.Video
import com.jocoos.mybeautip.video.VideoLikeRepository
import com.jocoos.mybeautip.video.VideoRepository
import org.hamcrest.CoreMatchers.notNullValue
import org.json.JSONObject
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
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@TestInstance(PER_CLASS)
class VideoControllerTest(
    private val videoRepository: VideoRepository,
    private val videoCategoryRepository: VideoCategoryRepository,
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
                get("/api/1/videos")
                    .param("category_id", category.id.toString())
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_videos",
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
                get("/api/1/videos/{video_id}", video.id)
                    .header(AUTHORIZATION, requestUserToken)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_video",
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
                    fieldWithPath("owner").type(OBJECT).description("비디오 작성자 정보"),
                    fieldWithPath("owner.id").type(NUMBER).description("아이디"),
                    fieldWithPath("owner.tag").type(STRING).description("태그"),
                    fieldWithPath("owner.status").type(STRING).description("상태"),
                    fieldWithPath("owner.grant_type").type(STRING).description(generateLinkCode(GRANT_TYPE)).optional(),
                    fieldWithPath("owner.username").type(STRING).description("유저명"),
                    fieldWithPath("owner.email").type(STRING).description("이메일"),
                    fieldWithPath("owner.phone_number").type(STRING).description("전화번호"),
                    fieldWithPath("owner.avatar_url").type(STRING).description("아바타 URL"),
                    fieldWithPath("owner.follower_count").type(NUMBER).description("팔로워 수"),
                    fieldWithPath("owner.following_count").type(NUMBER).description("팔로잉 수"),
                    fieldWithPath("owner.video_count").type(NUMBER).description("비디오 수"),
                    fieldWithPath("owner.created_at").type(NUMBER).description("회원가입일"),
                    fieldWithPath("owner.modified_at").type(NUMBER).description("정보수정일"),
                    fieldWithPath("owner.permission").type(OBJECT).description("권한"),
                    fieldWithPath("owner.permission.chat_post").type(BOOLEAN).description("post 권한"),
                    fieldWithPath("owner.permission.comment_post").type(BOOLEAN).description("댓글 권한"),
                    fieldWithPath("owner.permission.live_post").type(BOOLEAN).description("라이브 권한"),
                    fieldWithPath("owner.permission.motd_post").type(BOOLEAN).description("motd 권한"),
                    fieldWithPath("owner.permission.revenue_return").type(BOOLEAN).description("수익배분 권한"),
                    fieldWithPath("created_at").type(STRING).description("생성 일자").attributes(getZonedDateMilliFormat())
                )
            )
        )
    }

    @Test
    fun getRecommendedVideo() {

        // given
        videoRepository.save(makeVideo(member = defaultAdmin, category = category, isRecommended = true))

        // when & then
        val result: ResultActions = mockMvc.perform(
            get("/api/1/videos/recommend")
                .header(AUTHORIZATION, requestUserToken)
                .contentType(APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "get_recommend_videos",
                responseFields(
                    fieldWithPath("[]").type(ARRAY).description("비디오 글 목록"),
                    fieldWithPath("[].id").type(NUMBER).description("비디오 ID"),
                    fieldWithPath("[].video_key").type(STRING).description("비디오 키"),
                    fieldWithPath("[].live_key").type(STRING).description("라이브 키").optional(),
                    fieldWithPath("[].output_type").type(STRING).description("").optional(),
                    fieldWithPath("[].type").type(STRING).description("방송 타입. UPLOADED, BROADCASTED"),
                    fieldWithPath("[].state").type(STRING).description("방송 상태. VOD 뿐."),
                    fieldWithPath("[].locked").type(BOOLEAN).description("잠금 여부"),
                    fieldWithPath("[].muted").type(BOOLEAN).description("음소거 여부"),
                    fieldWithPath("[].visibility").type(STRING).description("노출 여부"),
                    fieldWithPath("[].category").type(ARRAY).description("카테고리 정보").optional(),
                    fieldWithPath("[].category.[].id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("[].category.[].type").type(STRING).description("카테고리 구분"),
                    fieldWithPath("[].category.[].title").type(STRING).description("카테고리 타이틀"),
                    fieldWithPath("[].category.[].shape_url").type(STRING).description("카테고리 쉐입 URL").optional(),
                    fieldWithPath("[].category.[].mask_type").type(STRING)
                        .description(generateLinkCode(VIDEO_MASK_TYPE)).optional(),
                    fieldWithPath("[].title").type(STRING).description("제목").optional(),
                    fieldWithPath("[].content").type(STRING).description("컨텐츠").optional(),
                    fieldWithPath("[].url").type(STRING).description("비디오 파일 주소"),
                    fieldWithPath("[].original_filename").type(STRING).description("비디오 파일명").optional(),
                    fieldWithPath("[].thumbnail_path").type(STRING).description("썸네일 경로").optional(),
                    fieldWithPath("[].thumbnail_url").type(STRING).description("썸네일 URL").optional(),
                    fieldWithPath("[].chat_room_id").type(STRING).description("채팅방 아이디").optional(),
                    fieldWithPath("[].duration").type(NUMBER).description("방송 길이. mm 초 단위"),
                    fieldWithPath("[].total_watch_count").type(NUMBER).description("총 시청").optional(),
                    fieldWithPath("[].real_watch_count").type(NUMBER).description("실시청자수").optional(),
                    fieldWithPath("[].watch_count").type(NUMBER).description("실시간 시청자수"),
                    fieldWithPath("[].view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("[].heart_count").type(NUMBER).description("하트수"),
                    fieldWithPath("[].like_count").type(NUMBER).description("좋아요 수"),
                    fieldWithPath("[].comment_count").type(NUMBER).description("댓글수"),
                    fieldWithPath("[].order_count").type(NUMBER).description("주문수"),
                    fieldWithPath("[].report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("[].data").type(STRING).description("상품 정보등").optional(),
                    fieldWithPath("[].related_goods_count").type(NUMBER).description("관련 상품 갯수").optional(),
                    fieldWithPath("[].related_goods_thumbnail_url").type(STRING).description("상품 대표 URL").optional(),
                    fieldWithPath("[].like_id").type(NUMBER).description("좋아요 아이디").optional(),
                    fieldWithPath("[].scrap_id").type(NUMBER).description("스크랩 아이디").optional(),
                    fieldWithPath("[].blocked").type(BOOLEAN).description("차단 여부").optional(),
                    fieldWithPath("[].owner").type(OBJECT).description("비디오 작성자 정보"),
                    fieldWithPath("[].owner.id").type(NUMBER).description("아이디"),
                    fieldWithPath("[].owner.tag").type(STRING).description("태그"),
                    fieldWithPath("[].owner.status").type(STRING).description("상태"),
                    fieldWithPath("[].owner.grant_type").type(STRING).description(generateLinkCode(GRANT_TYPE))
                        .optional(),
                    fieldWithPath("[].owner.username").type(STRING).description("유저명"),
                    fieldWithPath("[].owner.email").type(STRING).description("이메일"),
                    fieldWithPath("[].owner.phone_number").type(STRING).description("전화번호"),
                    fieldWithPath("[].owner.avatar_url").type(STRING).description("아바타 URL"),
                    fieldWithPath("[].owner.follower_count").type(NUMBER).description("팔로워 수"),
                    fieldWithPath("[].owner.following_count").type(NUMBER).description("팔로잉 수"),
                    fieldWithPath("[].owner.video_count").type(NUMBER).description("비디오 수"),
                    fieldWithPath("[].owner.created_at").type(NUMBER).description("회원가입일"),
                    fieldWithPath("[].owner.modified_at").type(NUMBER).description("정보수정일"),
                    fieldWithPath("[].owner.permission").type(OBJECT).description("권한").optional(),
                    fieldWithPath("[].owner.permission.chat_post").type(BOOLEAN).description("post 권한").optional(),
                    fieldWithPath("[].owner.permission.comment_post").type(BOOLEAN).description("댓글 권한").optional(),
                    fieldWithPath("[].owner.permission.live_post").type(BOOLEAN).description("라이브 권한").optional(),
                    fieldWithPath("[].owner.permission.motd_post").type(BOOLEAN).description("motd 권한").optional(),
                    fieldWithPath("[].owner.permission.revenue_return").type(BOOLEAN).description("수익배분 권한").optional(),
                    fieldWithPath("[].created_at").type(STRING).description("생성 일자")
                        .attributes(getZonedDateMilliFormat())
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
                patch("/api/1/video/{video_id}/view-count", video.id)
                    .header(AUTHORIZATION, requestUserToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "add_view_count_video",
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
                post("/api/1/videos/{video_id}/likes", video.id)
                    .header(AUTHORIZATION, requestUserToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "add_video_like",
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
                    fieldWithPath("video.owner").type(OBJECT).description("비디오 작성자 정보"),
                    fieldWithPath("video.owner.id").type(NUMBER).description("아이디"),
                    fieldWithPath("video.owner.tag").type(STRING).description("태그"),
                    fieldWithPath("video.owner.status").type(STRING).description("상태"),
                    fieldWithPath("video.owner.grant_type").type(STRING).description(generateLinkCode(GRANT_TYPE))
                        .optional(),
                    fieldWithPath("video.owner.username").type(STRING).description("유저명"),
                    fieldWithPath("video.owner.email").type(STRING).description("이메일"),
                    fieldWithPath("video.owner.phone_number").type(STRING).description("전화번호"),
                    fieldWithPath("video.owner.avatar_url").type(STRING).description("아바타 URL"),
                    fieldWithPath("video.owner.follower_count").type(NUMBER).description("팔로워 수"),
                    fieldWithPath("video.owner.following_count").type(NUMBER).description("팔로잉 수"),
                    fieldWithPath("video.owner.video_count").type(NUMBER).description("비디오 수"),
                    fieldWithPath("video.owner.created_at").type(NUMBER).description("회원가입일"),
                    fieldWithPath("video.owner.modified_at").type(NUMBER).description("정보수정일"),
                    fieldWithPath("video.owner.permission").type(OBJECT).description("권한").optional(),
                    fieldWithPath("video.owner.permission.chat_post").type(BOOLEAN).description("post 권한").optional(),
                    fieldWithPath("video.owner.permission.comment_post").type(BOOLEAN).description("댓글 권한").optional(),
                    fieldWithPath("video.owner.permission.live_post").type(BOOLEAN).description("라이브 권한").optional(),
                    fieldWithPath("video.owner.permission.motd_post").type(BOOLEAN).description("motd 권한").optional(),
                    fieldWithPath("video.owner.permission.revenue_return").type(BOOLEAN).description("수익배분 권한").optional(),
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
                post("/api/1/videos/{video_id}/likes", video.id)
                    .header(AUTHORIZATION, requestUserToken)
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
                delete("/api/1/videos/{video_id}/likes/{like_id}", video.id, likeId)
                    .header(AUTHORIZATION, requestUserToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "delete_video_like",
                pathParameters(
                    parameterWithName("video_id").description("비디오 아이디"),
                    parameterWithName("like_id").description("좋아요 아이디")
                )
            )
        )
    }
}
