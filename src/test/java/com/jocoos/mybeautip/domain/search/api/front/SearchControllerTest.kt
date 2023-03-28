package com.jocoos.mybeautip.domain.search.api.front

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastCategoryRepository
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastRepository
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCategoryRepository
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityRepository
import com.jocoos.mybeautip.domain.search.code.SearchType.COMMUNITY
import com.jocoos.mybeautip.domain.video.persistence.domain.VideoCategory
import com.jocoos.mybeautip.domain.video.persistence.repository.VideoCategoryRepository
import com.jocoos.mybeautip.testutil.fixture.*
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.*
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.video.Video
import com.jocoos.mybeautip.video.VideoCategoryMappingRepository
import com.jocoos.mybeautip.video.VideoRepository
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.requestParameters
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class SearchControllerTest(
    private val communityCategoryRepository: CommunityCategoryRepository,
    private val communityRepository: CommunityRepository,
    private val videoRepository: VideoRepository,
    private val videoCategoryRepository: VideoCategoryRepository,
    private val videoCategoryMappingRepository: VideoCategoryMappingRepository,
    private val broadcastCategoryRepository: BroadcastCategoryRepository,
    private val broadcastRepository: BroadcastRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun searchCommunityTest() {

        // given
        val keyword = "key"
        val category: CommunityCategory = communityCategoryRepository.save(makeCommunityCategory(isInSummary = true))
        communityRepository.save(makeCommunity(category = category, member = defaultAdmin, contents = keyword))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/api/2/search")
                    .header(AUTHORIZATION, requestUserToken)
                    .param("type", "COMMUNITY")
                    .param("keyword", keyword)
                    .param("size", "20")
            )
            .andExpect(status().isOk)
            .andDo(print())
        result.andDo(
            document(
                "search_community",
                requestParameters(
                    parameterWithName("type").description("검색 타입").attributes(getDefault(COMMUNITY)).optional()
                        .description(generateLinkCode(SEARCH_TYPE)),
                    parameterWithName("keyword").description("검색어 (1자 이상 20자 이하)"),
                    parameterWithName("cursor").description("커서").optional()
                        .attributes(getZonedDateMilliFormat(), getDefault("현재 시간")),
                    parameterWithName("size").description("조회 개").optional().attributes(getDefault(20))
                ),
                responseFields(
                    fieldWithPath("next_cursor").type(STRING).description("커서 정보")
                        .attributes(getZonedDateMilliFormat()),
                    fieldWithPath("count").type(NUMBER).description("검색 결과 수"),
                    fieldWithPath("community").type(ARRAY).description("커뮤니티 글 목록"),
                    fieldWithPath("community.[].id").type(NUMBER).description("글 ID"),
                    fieldWithPath("community.[].is_win").type(BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부")
                        .optional(),
                    fieldWithPath("community.[].status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("community.[].event_id").type(NUMBER).description("이벤트 ID").optional(),
                    fieldWithPath("community.[].title").type(STRING).description("제목").optional(),
                    fieldWithPath("community.[].contents").type(STRING).description("내용").optional(),
                    fieldWithPath("community.[].files").type(ARRAY).description("파일 List").optional(),
                    fieldWithPath("community.[].files.[].type").type(STRING).description(generateLinkCode(FILE_TYPE)),
                    fieldWithPath("community.[].files.[].thumbnail_url").type(STRING).description("파일 썸네일 URL")
                        .optional(),
                    fieldWithPath("community.[].files.[].url").type(STRING).description("파일 URL"),
                    fieldWithPath("community.[].votes").type(ARRAY).description("투표 파일 List").optional(),
                    fieldWithPath("community.[].votes.[].id").type(NUMBER).description("투표 파일 아이디"),
                    fieldWithPath("community.[].votes.[].file_url").type(STRING).description("투표 파일 URL"),
                    fieldWithPath("community.[].votes.[].count").type(NUMBER).description("투표 수"),
                    fieldWithPath("community.[].votes.[].is_voted").type(BOOLEAN).description("투표 파일에 대한 유저 투표 여부"),
                    fieldWithPath("community.[].view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("community.[].like_count").type(NUMBER).description("좋아요수"),
                    fieldWithPath("community.[].comment_count").type(NUMBER).description("댓글/대댓글수"),
                    fieldWithPath("community.[].report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("community.[].created_at").type(STRING).description("작성일")
                        .attributes(getZonedDateFormat()),
                    fieldWithPath("community.[].relation_info").type(OBJECT).description("유저와의 관계 정보"),
                    fieldWithPath("community.[].relation_info.is_like").type(BOOLEAN).description("글 좋아요 여부"),
                    fieldWithPath("community.[].relation_info.is_block").type(BOOLEAN).description("작성자 차단 여부"),
                    fieldWithPath("community.[].relation_info.is_report").type(BOOLEAN).description("글 신고 여부"),
                    fieldWithPath("community.[].relation_info.is_scrap").type(BOOLEAN).description("스크랩 여부"),
                    fieldWithPath("community.[].member").type(OBJECT).description("작성자 정보."),
                    fieldWithPath("community.[].member.id").type(NUMBER).description("작성자 아이디").optional(),
                    fieldWithPath("community.[].member.status").type(STRING)
                        .description(generateLinkCode(DocUrl.MEMBER_STATUS)),
                    fieldWithPath("community.[].member.username").type(STRING).description("작성자 이름").optional(),
                    fieldWithPath("community.[].member.avatar_url").type(STRING).description("작성자 아바타 URL").optional(),
                    fieldWithPath("community.[].category").type(OBJECT).description("카테고리 정보"),
                    fieldWithPath("community.[].category.id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("community.[].category.type").type(STRING).description("카테고리 구분").description(
                        generateLinkCode(
                            COMMUNITY_CATEGORY_TYPE
                        )
                    ),
                    fieldWithPath("community.[].category.title").type(STRING).description("카테고리 제목"),
                    fieldWithPath("community.[].category.hint").type(STRING).description("카테고리 힌트")
                )
            )
        )
    }

    @Test
    fun searchVideoTest() {

        // given
        val keyword = "1"
        val category: VideoCategory = videoCategoryRepository.save(makeVideoCategory())
        val video: Video = videoRepository.save(makeVideo(member = defaultAdmin, category = category, title = keyword))
        videoCategoryMappingRepository.save(makeVideoCategoryMapping(video, category))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/api/2/search")
                    .header(AUTHORIZATION, requestUserToken)
                    .param("type", "VIDEO")
                    .param("keyword", "1")
                    .param("size", "20")
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "search_video",
                requestParameters(
                    parameterWithName("type").description("검색 타입").optional()
                        .description(generateLinkCode(SEARCH_TYPE)),
                    parameterWithName("keyword").description("검색어"),
                    parameterWithName("cursor").description("커서").optional()
                        .attributes(getZonedDateMilliFormat(), getDefault("현재 시간")),
                    parameterWithName("size").description("조회 개수").optional().attributes(getDefault(20))
                ),
                responseFields(
                    fieldWithPath("next_cursor").type(STRING).description("커서 정보")
                        .attributes(getZonedDateMilliFormat()),
                    fieldWithPath("count").type(NUMBER).description("검색 결과 수"),
                    fieldWithPath("video").type(ARRAY).description("비디오 글 목록"),
                    fieldWithPath("video.[].id").type(NUMBER).description("비디오 ID"),
                    fieldWithPath("video.[].video_key").type(STRING).description("비디오 키"),
                    fieldWithPath("video.[].live_key").type(STRING).description("라이브 키").optional(),
                    fieldWithPath("video.[].output_type").type(STRING).description("").optional(),
                    fieldWithPath("video.[].type").type(STRING).description("방송 타입. UPLOADED, BROADCASTED"),
                    fieldWithPath("video.[].state").type(STRING).description("방송 상태. VOD 뿐."),
                    fieldWithPath("video.[].locked").type(BOOLEAN).description("잠금 여부"),
                    fieldWithPath("video.[].muted").type(BOOLEAN).description("음소거 여부"),
                    fieldWithPath("video.[].visibility").type(STRING).description("노출 여부"),
                    fieldWithPath("video.[].category").type(ARRAY).description("카테고리 정보").optional(),
                    fieldWithPath("video.[].category.[].id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("video.[].category.[].type").type(STRING).description("카테고리 구분"),
                    fieldWithPath("video.[].category.[].title").type(STRING).description("카테고리 타이틀"),
                    fieldWithPath("video.[].category.[].shape_url").type(STRING).description("카테고리 쉐입 URL").optional(),
                    fieldWithPath("video.[].category.[].mask_type").type(STRING).description(
                        generateLinkCode(
                            VIDEO_MASK_TYPE
                        )
                    ).optional(),
                    fieldWithPath("video.[].title").type(STRING).description("제목").optional(),
                    fieldWithPath("video.[].content").type(STRING).description("컨텐츠").optional(),
                    fieldWithPath("video.[].url").type(STRING).description("비디오 파일 주소"),
                    fieldWithPath("video.[].original_filename").type(STRING).description("비디오 파일명").optional(),
                    fieldWithPath("video.[].thumbnail_path").type(STRING).description("썸네일 경로").optional(),
                    fieldWithPath("video.[].thumbnail_url").type(STRING).description("썸네일 URL").optional(),
                    fieldWithPath("video.[].chat_room_id").type(STRING).description("채팅방 아이디").optional(),
                    fieldWithPath("video.[].duration").type(NUMBER).description("방송 길이. mm 초 단위"),
                    fieldWithPath("video.[].total_watch_count").type(NUMBER).description("총 시청").optional(),
                    fieldWithPath("video.[].real_watch_count").type(NUMBER).description("실시청자수").optional(),
                    fieldWithPath("video.[].watch_count").type(NUMBER).description("실시간 시청자수"),
                    fieldWithPath("video.[].view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("video.[].heart_count").type(NUMBER).description("하트수"),
                    fieldWithPath("video.[].like_count").type(NUMBER).description("좋아요 수"),
                    fieldWithPath("video.[].comment_count").type(NUMBER).description("댓글수"),
                    fieldWithPath("video.[].order_count").type(NUMBER).description("주문수"),
                    fieldWithPath("video.[].report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("video.[].data").type(STRING).description("상품 정보등").optional(),
                    fieldWithPath("video.[].related_goods_count").type(NUMBER).description("관련 상품 갯수").optional(),
                    fieldWithPath("video.[].related_goods_thumbnail_url").type(STRING).description("상품 대표 URL")
                        .optional(),
                    fieldWithPath("video.[].like_id").type(NUMBER).description("좋아요 아이디").optional(),
                    fieldWithPath("video.[].scrap_id").type(NUMBER).description("스크랩 아이디").optional(),
                    fieldWithPath("video.[].blocked").type(BOOLEAN).description("차단 여부").optional(),
                    fieldWithPath("video.[].owner").type(OBJECT).description("비디오 작성자 정보"),
                    fieldWithPath("video.[].owner.id").type(NUMBER).description("아이디"),
                    fieldWithPath("video.[].owner.tag").type(STRING).description("태그"),
                    fieldWithPath("video.[].owner.status").type(STRING).description("상태"),
                    fieldWithPath("video.[].owner.grant_type").type(STRING).description(generateLinkCode(GRANT_TYPE))
                        .optional(),
                    fieldWithPath("video.[].owner.username").type(STRING).description("유저명"),
                    fieldWithPath("video.[].owner.email").type(STRING).description("이메일"),
                    fieldWithPath("video.[].owner.phone_number").type(STRING).description("전화번호"),
                    fieldWithPath("video.[].owner.avatar_url").type(STRING).description("아바타 URL"),
                    fieldWithPath("video.[].owner.follower_count").type(NUMBER).description("팔로워 수"),
                    fieldWithPath("video.[].owner.following_count").type(NUMBER).description("팔로잉 수"),
                    fieldWithPath("video.[].owner.video_count").type(NUMBER).description("비디오 수"),
                    fieldWithPath("video.[].owner.created_at").type(NUMBER).description("회원가입일"),
                    fieldWithPath("video.[].owner.modified_at").type(NUMBER).description("정보수정일"),
                    fieldWithPath("video.[].owner.deleted_at").type(NUMBER).description("탈퇴일").optional(),
                    fieldWithPath("video.[].owner.permission").type(OBJECT).description("권한").optional(),
                    fieldWithPath("video.[].owner.permission.chat_post").type(BOOLEAN).description("post 권한")
                        .optional(),
                    fieldWithPath("video.[].owner.permission.comment_post").type(BOOLEAN).description("댓글 권한")
                        .optional(),
                    fieldWithPath("video.[].owner.permission.live_post").type(BOOLEAN).description("라이브 권한").optional(),
                    fieldWithPath("video.[].owner.permission.motd_post").type(BOOLEAN).description("motd 권한")
                        .optional(),
                    fieldWithPath("video.[].owner.permission.revenue_return").type(BOOLEAN).description("수익배분 권한")
                        .optional(),
                    fieldWithPath("video.[].created_at").type(STRING).description("생성 일자")
                        .attributes(getZonedDateMilliFormat())
                )
            )
        )
    }

    @Test
    fun searchBroadcastTest() {

        // given
        val keyword = "1"
        val category: BroadcastCategory = broadcastCategoryRepository.save(makeBroadcastCategory(parentId = groupBroadcastCategory.id))
        broadcastRepository.save(makeBroadcast(category = category, title = keyword))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/api/2/search")
                    .header(AUTHORIZATION, requestUserToken)
                    .param("type", "BROADCAST")
                    .param("keyword", "1")
                    .param("size", "20")
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "search_broadcast",
                requestParameters(
                    parameterWithName("type").description("검색 타입").optional()
                        .description(generateLinkCode(SEARCH_TYPE)),
                    parameterWithName("keyword").description("검색어"),
                    parameterWithName("cursor").description("커서 (방송 아이디)").optional(),
                    parameterWithName("size").description("조회 개수").optional().attributes(getDefault(20))
                ),
                responseFields(
                    fieldWithPath("next_cursor").type(STRING).description("커서 정보 (방송 아이디)"),
                    fieldWithPath("count").type(NUMBER).description("검색 결과 수"),
                    fieldWithPath("broadcast").type(ARRAY).description("방송 목록"),
                    fieldWithPath("broadcast.[].id").type(NUMBER).description("방송 아이디"),
                    fieldWithPath("broadcast.[].status").type(STRING).description(generateLinkCode(BROADCAST_STATUS)),
                    fieldWithPath("broadcast.[].url").type(STRING).description("방송 URL").optional(),
                    fieldWithPath("broadcast.[].title").type(STRING).description("타이틀"),
                    fieldWithPath("broadcast.[].thumbnail_url").type(STRING).description("썸네일 URL"),
                    fieldWithPath("broadcast.[].viewer_count").type(NUMBER).description("시청자수"),
                    fieldWithPath("broadcast.[].started_at").type(STRING).description("시작 시간")
                        .attributes(getZonedDateFormat()),
                    fieldWithPath("broadcast.[].category").type(OBJECT).description("카테고리 정보"),
                    fieldWithPath("broadcast.[].category.id").type(NUMBER).description("카테고리 ID"),
                    fieldWithPath("broadcast.[].category.title").type(STRING).description("카테고리 타이틀"),
                    fieldWithPath("broadcast.[].created_by").type(OBJECT).description("진행자 정보"),
                    fieldWithPath("broadcast.[].created_by.id").type(NUMBER).description("진행자 아이디"),
                    fieldWithPath("broadcast.[].created_by.email").type(STRING).description("진행자 이메일").optional(),
                    fieldWithPath("broadcast.[].created_by.username").type(STRING).description("진행자 닉네임"),
                    fieldWithPath("broadcast.[].created_by.avatar_url").type(STRING).description("진행자 아바타 URL"),
                    fieldWithPath("broadcast.[].relation_info").type(OBJECT).description("요청자 연관 정보"),
                    fieldWithPath("broadcast.[].relation_info.is_notify_needed").type(BOOLEAN).description("요청자 연관 정보 - 알림 필요 여부"),
                )
            )
        )
    }

    @Test
    fun countTest() {

        // given
        val keyword = "key"
        val category: CommunityCategory = communityCategoryRepository.save(makeCommunityCategory(isInSummary = true))
        communityRepository.save(makeCommunity(category = category, member = defaultAdmin, contents = keyword))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/search/count")
                    .header(AUTHORIZATION, requestUserToken)
                    .param("keyword", keyword)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "count",
                requestParameters(
                    parameterWithName("type").description("검색 타입").attributes(getDefault(COMMUNITY)).optional()
                        .description(generateLinkCode(SEARCH_TYPE)),
                    parameterWithName("keyword").description("검색어")
                ),
                responseFields(
                    fieldWithPath("count").type(NUMBER).description("검색 개수")
                )
            )
        )
    }
}
