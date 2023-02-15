package com.jocoos.mybeautip.domain.broadcast.api.front

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastViewer
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastRepository
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastViewerRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getDefault
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentAttributeGenerator.getZonedDateFormat
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.DocUrl.BROADCAST_VIEWER_TYPE
import com.jocoos.mybeautip.global.config.restdoc.util.DocumentLinkGenerator.generateLinkCode
import com.jocoos.mybeautip.member.Member
import com.jocoos.mybeautip.member.MemberRepository
import com.jocoos.mybeautip.testutil.fixture.makeBroadcast
import com.jocoos.mybeautip.testutil.fixture.makeMember
import com.jocoos.mybeautip.testutil.fixture.makeViewer
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class BroadcastViewerControllerTest(
    private val broadcastRepository: BroadcastRepository,
    private val broadcastViewerRepository: BroadcastViewerRepository,
    private val memberRepository: MemberRepository
) : RestDocsIntegrationTestSupport() {

    @Test
    fun search() {
        val broadcast = saveBroadcast(memberId = defaultInfluencer.id)
        saveViewer(broadcast = broadcast);

        val result: ResultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/1/broadcast/{broadcast_id}/viewer", broadcast.id)
                .header(HttpHeaders.AUTHORIZATION, defaultInfluencerToken)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())

        result.andDo(
            MockMvcRestDocumentation.document(
                "get_viewers",
                RequestDocumentation.pathParameters(
                    RequestDocumentation.parameterWithName("broadcast_id").description("방송 ID")
                ),
                RequestDocumentation.requestParameters(
                    RequestDocumentation.parameterWithName("type")
                        .description(generateLinkCode(BROADCAST_VIEWER_TYPE))
                        .optional()
                        .attributes(getDefault(1)),
                    RequestDocumentation.parameterWithName("cursor").description("커서. 회원 아이디").optional(),
                    RequestDocumentation.parameterWithName("size").description("페이지 사이즈").optional()
                ),
                PayloadDocumentation.responseFields(
                    PayloadDocumentation.fieldWithPath("[].type").type(JsonFieldType.STRING)
                        .description(generateLinkCode(BROADCAST_VIEWER_TYPE)),
                    PayloadDocumentation.fieldWithPath("[].member_id").type(JsonFieldType.NUMBER).description("회원 아이디"),
                    PayloadDocumentation.fieldWithPath("[].username").type(JsonFieldType.STRING).description("회원명"),
                    PayloadDocumentation.fieldWithPath("[].avatar_url").type(JsonFieldType.STRING)
                        .description("회원 아바타 URL"),
                    PayloadDocumentation.fieldWithPath("[].joined_at").type(JsonFieldType.STRING)
                        .description("채팅방 입장 시간").attributes(getZonedDateFormat())
                )
            )
        )
    }


    fun saveViewer(
        broadcast: Broadcast,
        member: Member = saveMember()
    ): BroadcastViewer {
        return broadcastViewerRepository.save(makeViewer(broadcast = broadcast, member = member));
    }

    fun saveBroadcast(
        memberId: Long
    ): Broadcast {
        return broadcastRepository.save(makeBroadcast(memberId = memberId))
    }

    fun saveMember(): Member {
        return memberRepository.save(makeMember())
    }
}