package com.jocoos.mybeautip.domain.home.api.front

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.BLIND
import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.VOTE
import com.jocoos.mybeautip.domain.community.persistence.domain.Community
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityFile
import com.jocoos.mybeautip.domain.community.persistence.domain.vote.CommunityVote
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCategoryRepository
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityRepository
import com.jocoos.mybeautip.domain.video.persistence.domain.VideoCategory
import com.jocoos.mybeautip.domain.video.persistence.repository.VideoCategoryRepository
import com.jocoos.mybeautip.testutil.fixture.*
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateMilliFormat
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
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class SummaryControllerTest(
    private val communityCategoryRepository: CommunityCategoryRepository,
    private val communityRepository: CommunityRepository,
    private val videoRepository: VideoRepository,
    private val videoCategoryRepository: VideoCategoryRepository,
    private val videoCategoryMappingRepository: VideoCategoryMappingRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun summaryCommunityTop() {

        // given
        val category: CommunityCategory = communityCategoryRepository.save(makeCommunityCategory(isInSummary = true))
        communityRepository.save(makeCommunity(category = category))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/api/2/summary/community/top")
                    .header(AUTHORIZATION, requestUserToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "summary_community_top",
                responseFields(
                    fieldWithPath("category").type(ARRAY).description("커뮤니티 메인 상단 탭 카테고리"),
                    fieldWithPath("category.[].id").type(NUMBER).description("커뮤니티 아이디"),
                    fieldWithPath("category.[].type").type(STRING).description("카테고리 구분")
                        .description(generateLinkCode(COMMUNITY_CATEGORY_TYPE)),
                    fieldWithPath("category.[].title").type(STRING).description("제목"),
                    fieldWithPath("category.[].hint").type(STRING).description("힌트"),
                    fieldWithPath("content").type(ARRAY).description("커뮤니티 메인 상단 게시글 정보"),
                    fieldWithPath("content.[].category_id").type(NUMBER).description("커뮤니티 카테고리 아이디"),
                    fieldWithPath("content.[].community").type(ARRAY).description("게시글 목록"),
                    fieldWithPath("content.[].community.[].id").type(NUMBER).description("글 ID"),
                    fieldWithPath("content.[].community.[].is_win").type(BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부")
                        .optional(),
                    fieldWithPath("content.[].community.[].status").type(STRING)
                        .description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("content.[].community.[].event_id").type(NUMBER).description("이벤트 ID").optional(),
                    fieldWithPath("content.[].community.[].event_title").type(STRING).description("이벤트 제목").optional(),
                    fieldWithPath("content.[].community.[].title").type(STRING).description("제목").optional(),
                    fieldWithPath("content.[].community.[].contents").type(STRING).description("내용").optional(),
                    fieldWithPath("content.[].community.[].files").type(ARRAY).description("파일 List").optional(),
                    fieldWithPath("content.[].community.[].files.[].type").type(STRING)
                        .description(generateLinkCode(FILE_TYPE)),
                    fieldWithPath("content.[].community.[].files.[].thumbnail_url").type(STRING)
                        .description("파일 썸네일 URL").optional(),
                    fieldWithPath("content.[].community.[].files.[].url").type(STRING).description("파일 URL"),
                    fieldWithPath("content.[].community.[].view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("content.[].community.[].like_count").type(NUMBER).description("좋아요수"),
                    fieldWithPath("content.[].community.[].comment_count").type(NUMBER).description("댓글/대댓글수"),
                    fieldWithPath("content.[].community.[].report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("content.[].community.[].created_at").type(STRING).description("작성일")
                        .attributes(getZonedDateFormat()),
                    fieldWithPath("content.[].community.[].relation_info").type(OBJECT).description("유저와의 관계 정보"),
                    fieldWithPath("content.[].community.[].relation_info.is_like").type(BOOLEAN)
                        .description("글 좋아요 여부"),
                    fieldWithPath("content.[].community.[].relation_info.is_block").type(BOOLEAN)
                        .description("작성자 차단 여부"),
                    fieldWithPath("content.[].community.[].relation_info.is_report").type(BOOLEAN)
                        .description("글 신고 여부"),
                    fieldWithPath("content.[].community.[].relation_info.is_scrap").type(BOOLEAN)
                        .description("글 스크랩 여부"),
                    fieldWithPath("content.[].community.[].member").type(OBJECT).description("작성자 정보."),
                    fieldWithPath("content.[].community.[].member.id").type(NUMBER).description("작성자 아이디").optional(),
                    fieldWithPath("content.[].community.[].member.status").type(STRING)
                        .description(generateLinkCode(MEMBER_STATUS)),
                    fieldWithPath("content.[].community.[].member.username").type(STRING).description("작성자 이름")
                        .optional(),
                    fieldWithPath("content.[].community.[].member.avatar_url").type(STRING).description("작성자 아바타 URL")
                        .optional(),
                    fieldWithPath("content.[].community.[].category").type(OBJECT).description("카테고리 정보"),
                    fieldWithPath("content.[].community.[].category.id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("content.[].community.[].category.type").type(STRING).description("카테고리 구분")
                        .description(generateLinkCode(COMMUNITY_CATEGORY_TYPE)),
                    fieldWithPath("content.[].community.[].category.title").type(STRING).description("카테고리 제목"),
                    fieldWithPath("content.[].community.[].category.hint").type(STRING).description("카테고리 힌트")
                )
            )
        )
    }

    @Test
    fun summaryCommunityVote() {

        // given
        saveCommunityVote()

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/api/2/summary/community/{type}", VOTE)
                    .header(AUTHORIZATION, requestUserToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "summary_community_vote",
                pathParameters(
                    parameterWithName("type").description(generateLinkCode(COMMUNITY_CATEGORY_TYPE))
                ),
                responseFields(
                    fieldWithPath("[]").type(ARRAY).description("Vote 커뮤니티 목록").optional(),
                    fieldWithPath("[].id").type(NUMBER).description("글 ID"),
                    fieldWithPath("[].status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("[].is_win").type(BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                    fieldWithPath("[].title").type(STRING).description("제목").optional(),
                    fieldWithPath("[].contents").type(STRING).description("내용"),
                    fieldWithPath("[].files").type(ARRAY).description("파일 List").optional(),
                    fieldWithPath("[].files.[].type").type(STRING).description(generateLinkCode(FILE_TYPE)),
                    fieldWithPath("[].files.[].thumbnail_url").type(STRING).description("파일 썸네일 URL").optional(),
                    fieldWithPath("[].files.[].url").type(STRING).description("파일 URL"),
                    fieldWithPath("[].votes").type(ARRAY).description("투표 파일 List").optional(),
                    fieldWithPath("[].votes.[].id").type(NUMBER).description("투표 파일 아이디"),
                    fieldWithPath("[].votes.[].file_url").type(STRING).description("투표 파일 URL"),
                    fieldWithPath("[].view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("[].like_count").type(NUMBER).description("좋아요수"),
                    fieldWithPath("[].comment_count").type(NUMBER).description("댓글수"),
                    fieldWithPath("[].report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("[].created_at").type(STRING).description("작성일").attributes(getZonedDateFormat()),
                    fieldWithPath("[].relation_info").type(OBJECT).description("유저와의 관계 정보"),
                    fieldWithPath("[].relation_info.is_like").type(BOOLEAN).description("글 좋아요 여부"),
                    fieldWithPath("[].relation_info.is_block").type(BOOLEAN).description("작성자 차단 여부"),
                    fieldWithPath("[].relation_info.is_report").type(BOOLEAN).description("글 신고 여부"),
                    fieldWithPath("[].relation_info.is_scrap").type(BOOLEAN).description("글 스크랩 여부"),
                    fieldWithPath("[].member").type(OBJECT).description("작성자 정보"),
                    fieldWithPath("[].member.id").type(NUMBER).description("작성자 아이디").optional(),
                    fieldWithPath("[].member.status").type(STRING).description(generateLinkCode(MEMBER_STATUS)),
                    fieldWithPath("[].member.username").type(STRING).description("작성자 이름").optional(),
                    fieldWithPath("[].member.avatar_url").type(STRING).description("작성자 아바타 URL").optional(),
                    fieldWithPath("[].category").type(OBJECT).description("카테고리 정보"),
                    fieldWithPath("[].category.id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("[].category.type").type(STRING).description("카테고리 구분")
                        .description(generateLinkCode(COMMUNITY_CATEGORY_TYPE)),
                    fieldWithPath("[].category.title").type(STRING).description("카테고리 제목"),
                    fieldWithPath("[].category.hint").type(STRING).description("카테고리 힌트")
                )
            )
        )
    }

    @Test
    fun summaryCommunityBlind() {

        // given
        val category: CommunityCategory = communityCategoryRepository.save(makeCommunityCategory(type = BLIND))
        communityRepository.save(makeCommunity(category = category))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/api/2/summary/community/{type}", BLIND)
                    .header(AUTHORIZATION, requestUserToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "summary_community_blind",
                pathParameters(
                    parameterWithName("type")
                        .description(generateLinkCode(COMMUNITY_CATEGORY_TYPE))
                ),
                responseFields(
                    fieldWithPath("[].id").type(NUMBER).description("글 ID"),
                    fieldWithPath("[].status").type(STRING).description(generateLinkCode(COMMUNITY_STATUS)),
                    fieldWithPath("[].is_win").type(BOOLEAN).description("당첨 여부. 현재는 드립N드림의 당첨 여부").optional(),
                    fieldWithPath("[].title").type(STRING).description("제목").optional(),
                    fieldWithPath("[].files").type(ARRAY).description("파일 List").optional(),
                    fieldWithPath("[].files.[].type").type(STRING).description(generateLinkCode(FILE_TYPE)),
                    fieldWithPath("[].files.[].thumbnail_url").type(STRING).description("파일 썸네일 URL").optional(),
                    fieldWithPath("[].files.[].url").type(STRING).description("파일 URL"),
                    fieldWithPath("[].votes").type(ARRAY).description("투표 파일 List").optional(),
                    fieldWithPath("[].votes.[].id").type(NUMBER).description("투표 파일 아이디"),
                    fieldWithPath("[].votes.[].file_url").type(STRING).description("투표 파일 URL"),
                    fieldWithPath("[].votes.[].count").type(NUMBER).description("투표 수"),
                    fieldWithPath("[].votes.[].is_voted").type(BOOLEAN).description("투표 파일에 대한 유저 투표 여부"),
                    fieldWithPath("[].view_count").type(NUMBER).description("조회수"),
                    fieldWithPath("[].like_count").type(NUMBER).description("좋아요수"),
                    fieldWithPath("[].comment_count").type(NUMBER).description("댓글수"),
                    fieldWithPath("[].report_count").type(NUMBER).description("신고수"),
                    fieldWithPath("[].created_at").type(STRING).description("작성일").attributes(getZonedDateFormat()),
                    fieldWithPath("[].relation_info").type(OBJECT).description("유저와의 관계 정보"),
                    fieldWithPath("[].relation_info.is_like").type(BOOLEAN).description("글 좋아요 여부"),
                    fieldWithPath("[].relation_info.is_block").type(BOOLEAN).description("작성자 차단 여부"),
                    fieldWithPath("[].relation_info.is_report").type(BOOLEAN).description("글 신고 여부"),
                    fieldWithPath("[].relation_info.is_scrap").type(BOOLEAN).description("글 스크랩 여부"),
                    fieldWithPath("[].member").type(OBJECT).description("작성자 정보"),
                    fieldWithPath("[].member.id").type(NUMBER).description("작성자 아이디").optional(),
                    fieldWithPath("[].member.status").type(STRING).description(generateLinkCode(MEMBER_STATUS)),
                    fieldWithPath("[].member.username").type(STRING).description("작성자 이름").optional(),
                    fieldWithPath("[].member.avatar_url").type(STRING).description("작성자 아바타 URL").optional(),
                    fieldWithPath("[].category").type(OBJECT).description("카테고리 정보"),
                    fieldWithPath("[].category.id").type(NUMBER).description("카테고리 아이디"),
                    fieldWithPath("[].category.type").type(STRING).description("카테고리 구분")
                        .description(generateLinkCode(COMMUNITY_CATEGORY_TYPE)),
                    fieldWithPath("[].category.title").type(STRING).description("카테고리 제목"),
                    fieldWithPath("[].category.hint").type(STRING).description("카테고리 힌트")
                )
            )
        )
    }

    @Test
    fun summaryVideo() {

        // given
        val category: VideoCategory = videoCategoryRepository.save(makeVideoCategory())
        val video: Video = videoRepository.save(makeVideo(member = defaultAdmin, category = category))
        videoCategoryMappingRepository.save(makeVideoCategoryMapping(video, category))

        // when & then
        val result: ResultActions = mockMvc
            .perform(
                get("/api/1/summary/video")
                    .header(AUTHORIZATION, requestUserToken)
            )
            .andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "summary_video",
                responseFields(
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
                    fieldWithPath("[].category.[].mask_type").type(STRING).description(generateLinkCode(VIDEO_MASK_TYPE)).optional(),
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
                    fieldWithPath("[].owner.grant_type").type(STRING).description(generateLinkCode(GRANT_TYPE)).optional(),
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
                    fieldWithPath("[].created_at").type(STRING).description("생성 일자").attributes(getZonedDateMilliFormat())
                )
            )
        )
    }

    private fun saveCommunityVote(): Pair<Community, CommunityVote> {
        val category: CommunityCategory = communityCategoryRepository.save(makeCommunityCategory(type = VOTE))

        val community: Community = makeCommunity(category = category)

        val communityFileToVote: CommunityFile = makeCommunityFile(community = community)
        val communityFile: CommunityFile = makeCommunityFile(community = community)


        val communityVoteToVote: CommunityVote = makeCommunityVote(community, communityFileToVote)
        val communityVote: CommunityVote = makeCommunityVote(community, communityFile)

        community.communityFileList = listOf(communityFileToVote, communityFile)
        community.communityVoteList = listOf(communityVoteToVote, communityVote)

        communityRepository.save(community)
        return Pair(community, communityVoteToVote)
    }
}
