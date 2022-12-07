package com.jocoos.mybeautip.domain.video.api.front;

import com.jocoos.mybeautip.global.config.restdoc.RestDocsTestSupport;
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault;
import static com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateMilliFormat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VideoControllerTest extends RestDocsTestSupport {

    @Test
    void getVideos() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/videos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        result.andDo(document("get_videos",
                        requestParameters(
                                parameterWithName("category_id").description("비디오 카테고리 아이디").optional(),
                                parameterWithName("cursor").description("커서").optional().attributes(getZonedDateMilliFormat(), getDefault("현재 시간")),
                                parameterWithName("count").description("조회갯수").optional().attributes(getDefault(50))
                        ),
                        responseFields(
                                fieldWithPath("next_cursor").type(JsonFieldType.STRING).description("커서 정보").attributes(getZonedDateMilliFormat()),
                                fieldWithPath("content").type(JsonFieldType.ARRAY).description("비디오 글 목록"),
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("비디오 ID"),
                                fieldWithPath("content.[].video_key").type(JsonFieldType.STRING).description("비디오 키"),
                                fieldWithPath("content.[].live_key").type(JsonFieldType.STRING).description("라이브 키").optional(),
                                fieldWithPath("content.[].output_type").type(JsonFieldType.STRING).description("").optional(),
                                fieldWithPath("content.[].type").type(JsonFieldType.STRING).description("방송 타입. UPLOADED, BROADCASTED"),
                                fieldWithPath("content.[].state").type(JsonFieldType.STRING).description("방송 상태. VOD 뿐."),
                                fieldWithPath("content.[].locked").type(JsonFieldType.BOOLEAN).description("잠금 여부"),
                                fieldWithPath("content.[].muted").type(JsonFieldType.BOOLEAN).description("음소거 여부"),
                                fieldWithPath("content.[].visibility").type(JsonFieldType.STRING).description("노출 여부"),
                                fieldWithPath("content.[].category").type(JsonFieldType.ARRAY).description("카테고리 정보").optional(),
                                fieldWithPath("content.[].category.[].id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("content.[].category.[].type").type(JsonFieldType.STRING).description("카테고리 구분"),
                                fieldWithPath("content.[].category.[].title").type(JsonFieldType.STRING).description("카테고리 타이틀"),
                                fieldWithPath("content.[].category.[].shape_url").type(JsonFieldType.STRING).description("카테고리 쉐입 URL").optional(),
                                fieldWithPath("content.[].category.[].mask_type").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.VIDEO_MASK_TYPE)).optional(),
                                fieldWithPath("content.[].title").type(JsonFieldType.STRING).description("제목").optional(),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("컨텐츠").optional(),
                                fieldWithPath("content.[].url").type(JsonFieldType.STRING).description("비디오 파일 주소"),
                                fieldWithPath("content.[].original_filename").type(JsonFieldType.STRING).description("비디오 파일명").optional(),
                                fieldWithPath("content.[].thumbnail_path").type(JsonFieldType.STRING).description("썸네일 경로").optional(),
                                fieldWithPath("content.[].thumbnail_url").type(JsonFieldType.STRING).description("썸네일 URL").optional(),
                                fieldWithPath("content.[].chat_room_id").type(JsonFieldType.STRING).description("채팅방 아이디").optional(),
                                fieldWithPath("content.[].duration").type(JsonFieldType.NUMBER).description("방송 길이. mm 초 단위"),
                                fieldWithPath("content.[].total_watch_count").type(JsonFieldType.NUMBER).description("총 시청").optional(),
                                fieldWithPath("content.[].real_watch_count").type(JsonFieldType.NUMBER).description("실시청자수").optional(),
                                fieldWithPath("content.[].watch_count").type(JsonFieldType.NUMBER).description("실시간 시청자수"),
                                fieldWithPath("content.[].view_count").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("content.[].heart_count").type(JsonFieldType.NUMBER).description("하트수"),
                                fieldWithPath("content.[].like_count").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                fieldWithPath("content.[].comment_count").type(JsonFieldType.NUMBER).description("댓글수"),
                                fieldWithPath("content.[].order_count").type(JsonFieldType.NUMBER).description("주문수"),
                                fieldWithPath("content.[].report_count").type(JsonFieldType.NUMBER).description("신고수"),
                                fieldWithPath("content.[].data").type(JsonFieldType.STRING).description("상품 정보등").optional(),
                                fieldWithPath("content.[].related_goods_count").type(JsonFieldType.NUMBER).description("관련 상품 갯수").optional(),
                                fieldWithPath("content.[].related_goods_thumbnail_url").type(JsonFieldType.STRING).description("상품 대표 URL").optional(),
                                fieldWithPath("content.[].like_id").type(JsonFieldType.NUMBER).description("좋아요 아이디").optional(),
                                fieldWithPath("content.[].scrap_id").type(JsonFieldType.NUMBER).description("스크랩 아이디").optional(),
                                fieldWithPath("content.[].blocked").type(JsonFieldType.BOOLEAN).description("차단 여부").optional(),
                                fieldWithPath("content.[].owner").type(JsonFieldType.OBJECT).description("비디오 작성자 정보"),
                                fieldWithPath("content.[].owner.id").type(JsonFieldType.NUMBER).description("아이디"),
                                fieldWithPath("content.[].owner.tag").type(JsonFieldType.STRING).description("태그"),
                                fieldWithPath("content.[].owner.status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("content.[].owner.grant_type").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.GRANT_TYPE)).optional(),
                                fieldWithPath("content.[].owner.username").type(JsonFieldType.STRING).description("유저명"),
                                fieldWithPath("content.[].owner.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("content.[].owner.phone_number").type(JsonFieldType.STRING).description("전화번호"),
                                fieldWithPath("content.[].owner.avatar_url").type(JsonFieldType.STRING).description("아바타 URL"),
                                fieldWithPath("content.[].owner.follower_count").type(JsonFieldType.NUMBER).description("팔로워 수"),
                                fieldWithPath("content.[].owner.following_count").type(JsonFieldType.NUMBER).description("팔로잉 수"),
                                fieldWithPath("content.[].owner.video_count").type(JsonFieldType.NUMBER).description("비디오 수"),
                                fieldWithPath("content.[].owner.created_at").type(JsonFieldType.NUMBER).description("회원가입일"),
                                fieldWithPath("content.[].owner.modified_at").type(JsonFieldType.NUMBER).description("정보수정일"),
                                fieldWithPath("content.[].owner.permission").type(JsonFieldType.OBJECT).description("권한").optional(),
                                fieldWithPath("content.[].owner.permission.chat_post").type(JsonFieldType.BOOLEAN).description("post 권한").optional(),
                                fieldWithPath("content.[].owner.permission.comment_post").type(JsonFieldType.BOOLEAN).description("댓글 권한").optional(),
                                fieldWithPath("content.[].owner.permission.live_post").type(JsonFieldType.BOOLEAN).description("라이브 권한").optional(),
                                fieldWithPath("content.[].owner.permission.motd_post").type(JsonFieldType.BOOLEAN).description("motd 권한").optional(),
                                fieldWithPath("content.[].owner.permission.revenue_return").type(JsonFieldType.BOOLEAN).description("수익배분 권한").optional(),
                                fieldWithPath("content.[].created_at").type(JsonFieldType.STRING).description("생성 일자").attributes(getZonedDateMilliFormat())
                        )
                )
        );
    }

    @Test
    void getVideo() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/videos/{video_id}", 10001)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        result.andDo(document("get_video",
                        pathParameters(
                                parameterWithName("video_id").description("비디오 아이디")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("비디오 ID"),
                                fieldWithPath("video_key").type(JsonFieldType.STRING).description("비디오 키"),
                                fieldWithPath("live_key").type(JsonFieldType.STRING).description("라이브 키").optional(),
                                fieldWithPath("output_type").type(JsonFieldType.STRING).description("").optional(),
                                fieldWithPath("type").type(JsonFieldType.STRING).description("방송 타입. UPLOADED, BROADCASTED"),
                                fieldWithPath("state").type(JsonFieldType.STRING).description("방송 상태. VOD 뿐."),
                                fieldWithPath("locked").type(JsonFieldType.BOOLEAN).description("잠금 여부"),
                                fieldWithPath("muted").type(JsonFieldType.BOOLEAN).description("음소거 여부").optional(),
                                fieldWithPath("visibility").type(JsonFieldType.STRING).description("노출 여부"),
                                fieldWithPath("category").type(JsonFieldType.ARRAY).description("카테고리 정보"),
                                fieldWithPath("category.[].id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("category.[].type").type(JsonFieldType.STRING).description("카테고리 구분"),
                                fieldWithPath("category.[].title").type(JsonFieldType.STRING).description("카테고리 타이틀"),
                                fieldWithPath("category.[].shape_url").type(JsonFieldType.STRING).description("카테고리 쉐입 URL"),
                                fieldWithPath("category.[].mask_type").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.VIDEO_MASK_TYPE)),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목").optional(),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("컨텐츠").optional(),
                                fieldWithPath("url").type(JsonFieldType.STRING).description("비디오 파일 주소").optional(),
                                fieldWithPath("original_filename").type(JsonFieldType.STRING).description("비디오 파일명").optional(),
                                fieldWithPath("thumbnail_path").type(JsonFieldType.STRING).description("썸네일 경로").optional(),
                                fieldWithPath("thumbnail_url").type(JsonFieldType.STRING).description("썸네일 URL").optional(),
                                fieldWithPath("chat_room_id").type(JsonFieldType.STRING).description("채팅방 아이디").optional(),
                                fieldWithPath("duration").type(JsonFieldType.NUMBER).description("방송 길이. mm 초 단위"),
                                fieldWithPath("total_watch_count").type(JsonFieldType.NUMBER).description("총 시청").optional(),
                                fieldWithPath("real_watch_count").type(JsonFieldType.NUMBER).description("실시청자수").optional(),
                                fieldWithPath("watch_count").type(JsonFieldType.NUMBER).description("실시간 시청자수"),
                                fieldWithPath("view_count").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("heart_count").type(JsonFieldType.NUMBER).description("하트수"),
                                fieldWithPath("like_count").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                fieldWithPath("comment_count").type(JsonFieldType.NUMBER).description("댓글수"),
                                fieldWithPath("order_count").type(JsonFieldType.NUMBER).description("주문수"),
                                fieldWithPath("report_count").type(JsonFieldType.NUMBER).description("신고수"),
                                fieldWithPath("data").type(JsonFieldType.STRING).description("상품 정보등").optional(),
                                fieldWithPath("related_goods_count").type(JsonFieldType.NUMBER).description("관련 상품 갯수").optional(),
                                fieldWithPath("related_goods_thumbnail_url").type(JsonFieldType.STRING).description("상품 대표 URL").optional(),
                                fieldWithPath("like_id").type(JsonFieldType.NUMBER).description("좋아요 아이디").optional(),
                                fieldWithPath("scrap_id").type(JsonFieldType.NUMBER).description("스크랩 아이디").optional(),
                                fieldWithPath("blocked").type(JsonFieldType.BOOLEAN).description("차단 여부").optional(),
                                fieldWithPath("owner").type(JsonFieldType.OBJECT).description("비디오 작성자 정보"),
                                fieldWithPath("owner.id").type(JsonFieldType.NUMBER).description("아이디"),
                                fieldWithPath("owner.tag").type(JsonFieldType.STRING).description("태그"),
                                fieldWithPath("owner.status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("owner.grant_type").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.GRANT_TYPE)),
                                fieldWithPath("owner.username").type(JsonFieldType.STRING).description("유저명"),
                                fieldWithPath("owner.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("owner.phone_number").type(JsonFieldType.STRING).description("전화번호"),
                                fieldWithPath("owner.avatar_url").type(JsonFieldType.STRING).description("아바타 URL"),
                                fieldWithPath("owner.follower_count").type(JsonFieldType.NUMBER).description("팔로워 수"),
                                fieldWithPath("owner.following_count").type(JsonFieldType.NUMBER).description("팔로잉 수"),
                                fieldWithPath("owner.video_count").type(JsonFieldType.NUMBER).description("비디오 수"),
                                fieldWithPath("owner.created_at").type(JsonFieldType.NUMBER).description("회원가입일"),
                                fieldWithPath("owner.modified_at").type(JsonFieldType.NUMBER).description("정보수정일"),
                                fieldWithPath("owner.permission").type(JsonFieldType.OBJECT).description("권한"),
                                fieldWithPath("owner.permission.chat_post").type(JsonFieldType.BOOLEAN).description("post 권한"),
                                fieldWithPath("owner.permission.comment_post").type(JsonFieldType.BOOLEAN).description("댓글 권한"),
                                fieldWithPath("owner.permission.live_post").type(JsonFieldType.BOOLEAN).description("라이브 권한"),
                                fieldWithPath("owner.permission.motd_post").type(JsonFieldType.BOOLEAN).description("motd 권한"),
                                fieldWithPath("owner.permission.revenue_return").type(JsonFieldType.BOOLEAN).description("수익배분 권한"),
                                fieldWithPath("created_at").type(JsonFieldType.STRING).description("생성 일자").attributes(getZonedDateMilliFormat())
                        )
                )
        );
    }

    @Test
    void getRecommendedVideo() throws Exception {
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/1/videos/recommend")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        result.andDo(document("get_recommend_videos",
                        responseFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("비디오 글 목록"),
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("비디오 ID"),
                                fieldWithPath("[].video_key").type(JsonFieldType.STRING).description("비디오 키"),
                                fieldWithPath("[].live_key").type(JsonFieldType.STRING).description("라이브 키").optional(),
                                fieldWithPath("[].output_type").type(JsonFieldType.STRING).description("").optional(),
                                fieldWithPath("[].type").type(JsonFieldType.STRING).description("방송 타입. UPLOADED, BROADCASTED"),
                                fieldWithPath("[].state").type(JsonFieldType.STRING).description("방송 상태. VOD 뿐."),
                                fieldWithPath("[].locked").type(JsonFieldType.BOOLEAN).description("잠금 여부"),
                                fieldWithPath("[].muted").type(JsonFieldType.BOOLEAN).description("음소거 여부"),
                                fieldWithPath("[].visibility").type(JsonFieldType.STRING).description("노출 여부"),
                                fieldWithPath("[].category").type(JsonFieldType.ARRAY).description("카테고리 정보").optional(),
                                fieldWithPath("[].category.[].id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("[].category.[].type").type(JsonFieldType.STRING).description("카테고리 구분"),
                                fieldWithPath("[].category.[].title").type(JsonFieldType.STRING).description("카테고리 타이틀"),
                                fieldWithPath("[].category.[].shape_url").type(JsonFieldType.STRING).description("카테고리 쉐입 URL").optional(),
                                fieldWithPath("[].category.[].mask_type").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.VIDEO_MASK_TYPE)).optional(),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("제목").optional(),
                                fieldWithPath("[].content").type(JsonFieldType.STRING).description("컨텐츠").optional(),
                                fieldWithPath("[].url").type(JsonFieldType.STRING).description("비디오 파일 주소"),
                                fieldWithPath("[].original_filename").type(JsonFieldType.STRING).description("비디오 파일명").optional(),
                                fieldWithPath("[].thumbnail_path").type(JsonFieldType.STRING).description("썸네일 경로").optional(),
                                fieldWithPath("[].thumbnail_url").type(JsonFieldType.STRING).description("썸네일 URL").optional(),
                                fieldWithPath("[].chat_room_id").type(JsonFieldType.STRING).description("채팅방 아이디").optional(),
                                fieldWithPath("[].duration").type(JsonFieldType.NUMBER).description("방송 길이. mm 초 단위"),
                                fieldWithPath("[].total_watch_count").type(JsonFieldType.NUMBER).description("총 시청").optional(),
                                fieldWithPath("[].real_watch_count").type(JsonFieldType.NUMBER).description("실시청자수").optional(),
                                fieldWithPath("[].watch_count").type(JsonFieldType.NUMBER).description("실시간 시청자수"),
                                fieldWithPath("[].view_count").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("[].heart_count").type(JsonFieldType.NUMBER).description("하트수"),
                                fieldWithPath("[].like_count").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                fieldWithPath("[].comment_count").type(JsonFieldType.NUMBER).description("댓글수"),
                                fieldWithPath("[].order_count").type(JsonFieldType.NUMBER).description("주문수"),
                                fieldWithPath("[].report_count").type(JsonFieldType.NUMBER).description("신고수"),
                                fieldWithPath("[].data").type(JsonFieldType.STRING).description("상품 정보등").optional(),
                                fieldWithPath("[].related_goods_count").type(JsonFieldType.NUMBER).description("관련 상품 갯수").optional(),
                                fieldWithPath("[].related_goods_thumbnail_url").type(JsonFieldType.STRING).description("상품 대표 URL").optional(),
                                fieldWithPath("[].like_id").type(JsonFieldType.NUMBER).description("좋아요 아이디").optional(),
                                fieldWithPath("[].scrap_id").type(JsonFieldType.NUMBER).description("스크랩 아이디").optional(),
                                fieldWithPath("[].blocked").type(JsonFieldType.BOOLEAN).description("차단 여부").optional(),
                                fieldWithPath("[].owner").type(JsonFieldType.OBJECT).description("비디오 작성자 정보"),
                                fieldWithPath("[].owner.id").type(JsonFieldType.NUMBER).description("아이디"),
                                fieldWithPath("[].owner.tag").type(JsonFieldType.STRING).description("태그"),
                                fieldWithPath("[].owner.status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("[].owner.grant_type").type(JsonFieldType.STRING).description(DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.GRANT_TYPE)).optional(),
                                fieldWithPath("[].owner.username").type(JsonFieldType.STRING).description("유저명"),
                                fieldWithPath("[].owner.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("[].owner.phone_number").type(JsonFieldType.STRING).description("전화번호"),
                                fieldWithPath("[].owner.avatar_url").type(JsonFieldType.STRING).description("아바타 URL"),
                                fieldWithPath("[].owner.follower_count").type(JsonFieldType.NUMBER).description("팔로워 수"),
                                fieldWithPath("[].owner.following_count").type(JsonFieldType.NUMBER).description("팔로잉 수"),
                                fieldWithPath("[].owner.video_count").type(JsonFieldType.NUMBER).description("비디오 수"),
                                fieldWithPath("[].owner.created_at").type(JsonFieldType.NUMBER).description("회원가입일"),
                                fieldWithPath("[].owner.modified_at").type(JsonFieldType.NUMBER).description("정보수정일"),
                                fieldWithPath("[].owner.permission").type(JsonFieldType.OBJECT).description("권한").optional(),
                                fieldWithPath("[].owner.permission.chat_post").type(JsonFieldType.BOOLEAN).description("post 권한").optional(),
                                fieldWithPath("[].owner.permission.comment_post").type(JsonFieldType.BOOLEAN).description("댓글 권한").optional(),
                                fieldWithPath("[].owner.permission.live_post").type(JsonFieldType.BOOLEAN).description("라이브 권한").optional(),
                                fieldWithPath("[].owner.permission.motd_post").type(JsonFieldType.BOOLEAN).description("motd 권한").optional(),
                                fieldWithPath("[].owner.permission.revenue_return").type(JsonFieldType.BOOLEAN).description("수익배분 권한").optional(),
                                fieldWithPath("[].created_at").type(JsonFieldType.STRING).description("생성 일자").attributes(getZonedDateMilliFormat())
                        )
                )
        );
    }
}
