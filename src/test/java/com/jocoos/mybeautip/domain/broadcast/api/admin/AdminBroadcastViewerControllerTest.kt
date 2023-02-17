package com.jocoos.mybeautip.domain.broadcast.api.admin

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastViewer
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastRepository
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastViewerRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.BROADCAST_VIEWER_TYPE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.global.dto.single.BooleanDto
import com.jocoos.mybeautip.member.Member
import com.jocoos.mybeautip.member.MemberRepository
import com.jocoos.mybeautip.testutil.fixture.makeBroadcast
import com.jocoos.mybeautip.testutil.fixture.makeMember
import com.jocoos.mybeautip.testutil.fixture.makeViewer
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AdminBroadcastViewerControllerTest(
    private val broadcastRepository: BroadcastRepository,
    private val broadcastViewerRepository: BroadcastViewerRepository,
    private val memberRepository: MemberRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun search() {

        val broadcast = saveBroadcast();
        saveViewer(broadcast = broadcast);

        val result: ResultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/admin/broadcast/{broadcast_id}/viewer", broadcast.id)
                .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
        ).andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_get_viewers",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 ID")
                ),
                requestParameters(
                    parameterWithName("type").description(generateLinkCode(BROADCAST_VIEWER_TYPE)).optional()
                        .attributes(getDefault(1)),
                    parameterWithName("cursor").description("커서. 회원 아이디").optional(),
                    parameterWithName("size").description("페이지 사이즈").optional()
                ),
                responseFields(
                    fieldWithPath("[].type").type(JsonFieldType.STRING).description(generateLinkCode(BROADCAST_VIEWER_TYPE)),
                    fieldWithPath("[].member_id").type(JsonFieldType.NUMBER).description("회원 아이디").optional(),
                    fieldWithPath("[].username").type(JsonFieldType.STRING).description("회원명"),
                    fieldWithPath("[].avatar_url").type(JsonFieldType.STRING).description("회원 아바타 URL"),
                    fieldWithPath("[].is_suspended").type(JsonFieldType.BOOLEAN).description("정지 여부"),
                    fieldWithPath("[].joined_at").type(JsonFieldType.STRING).description("채팅방 입장 시간").attributes(getZonedDateFormat())
                )
            )
        )
    }

    @Test
    fun grantManager() {
        val broadcast = saveBroadcast()
        val viewer = saveViewer(broadcast = broadcast)
        val request = BooleanDto(true)

        val result: ResultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/admin/broadcast/{broadcast_id}/viewer/{member_id}/manager", broadcast.id, viewer.memberId)
                .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_grant_manager",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 ID"),
                    parameterWithName("member_id").description("회원 ID"),
                ),
                PayloadDocumentation.requestFields(
                    fieldWithPath("bool").type(JsonFieldType.BOOLEAN).description("매니저 권한 여부")
                ),
                responseFields(
                    fieldWithPath("member_id").type(JsonFieldType.NUMBER).description("회원 ID"),
                    fieldWithPath("type").type(JsonFieldType.STRING).description(generateLinkCode(BROADCAST_VIEWER_TYPE)),
                    fieldWithPath("is_suspended").type(JsonFieldType.BOOLEAN).description("정지 여부"),
                    fieldWithPath("joined_at").type(JsonFieldType.STRING).description("참여 일시").attributes(getZonedDateFormat())
                ),
            )
        )
    }

    @Test
    fun suspend() {
        val broadcast = saveBroadcast()
        val viewer = saveViewer(broadcast = broadcast)
        val request = BooleanDto(true)

        val result: ResultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/admin/broadcast/{broadcast_id}/viewer/{member_id}/manager", broadcast.id, viewer.memberId)
                .header(HttpHeaders.AUTHORIZATION, defaultAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)
            .andDo(print())

        result.andDo(
            document(
                "admin_suspend",
                pathParameters(
                    parameterWithName("broadcast_id").description("방송 ID"),
                    parameterWithName("member_id").description("회원 ID"),
                ),
                PayloadDocumentation.requestFields(
                    fieldWithPath("bool").type(JsonFieldType.BOOLEAN).description("채팅 정지 여부.")
                ),
                responseFields(
                    fieldWithPath("member_id").type(JsonFieldType.NUMBER).description("회원 ID"),
                    fieldWithPath("type").type(JsonFieldType.STRING).description(generateLinkCode(BROADCAST_VIEWER_TYPE)),
                    fieldWithPath("is_suspended").type(JsonFieldType.BOOLEAN).description("정지 여부"),
                    fieldWithPath("joined_at").type(JsonFieldType.STRING).description("참여 일시").attributes(getZonedDateFormat())
                ),
            )
        )
    }


    fun saveViewer(
        broadcast: Broadcast,
        member: Member = saveMember()
    ): BroadcastViewer {
        return broadcastViewerRepository.save(makeViewer(broadcast = broadcast, member = member));
    }

    fun saveBroadcast(): Broadcast {
        return broadcastRepository.save(makeBroadcast())
    }

    fun saveMember(): Member {
        return memberRepository.save(makeMember())
    }

}