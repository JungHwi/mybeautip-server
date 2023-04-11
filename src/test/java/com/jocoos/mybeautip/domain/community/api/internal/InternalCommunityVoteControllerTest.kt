package com.jocoos.mybeautip.domain.community.api.internal

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.VOTE
import com.jocoos.mybeautip.domain.community.persistence.domain.Community
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityFile
import com.jocoos.mybeautip.domain.community.persistence.domain.vote.CommunityVote
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCategoryRepository
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.member.LegacyMemberService
import com.jocoos.mybeautip.member.Member
import com.jocoos.mybeautip.member.MemberRepository
import com.jocoos.mybeautip.testutil.fixture.*
import org.junit.jupiter.api.*
import org.mockito.BDDMockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InternalCommunityVoteControllerTest(
    private val communityRepository: CommunityRepository,
    private val communityCategoryRepository: CommunityCategoryRepository,
    private val memberRepository: MemberRepository
) : RestDocsIntegrationTestSupport() {

    private lateinit var writer: Member

    @MockBean
    private val legacyMemberService: LegacyMemberService? = null

    @BeforeAll
    fun beforeAll() {
        writer = memberRepository.save(makeMember(link = 2))
    }

    @BeforeEach
    fun setUp() {
        BDDMockito.given(legacyMemberService!!.currentMember()).willReturn(writer)
    }

    @DisplayName("투표 성공 테스트")
    @Test
    fun voteSuccess() {

        val (community: Community, communityVoteToVote: CommunityVote) = saveCommunityVote()


        val resultActions = mockMvc.perform(
            patch("/internal/1/community/{community_id}/vote/{vote_id}", community.id, communityVoteToVote.id)
                .header(AUTHORIZATION, requestInternalToken)
                .header(InternalCommunityControllerTest.MEMBER_ID, writer.id)
        )
            .andExpect(status().isOk)
            .andDo(print())

        resultActions.andDo(
            document(
                "internal_community_vote",
                pathParameters(
                    parameterWithName("community_id").description("커뮤니티 ID"),
                    parameterWithName("vote_id").description("투표 ID")
                ),
                responseFields(
                    fieldWithPath("votes").type(ARRAY).description("투표 파일 List"),
                    fieldWithPath("votes.[].id").type(NUMBER).description("투표 파일 아이디"),
                    fieldWithPath("votes.[].file_url").type(STRING).description("투표 파일 URL"),
                    fieldWithPath("votes.[].count").type(NUMBER).description("투표 수"),
                    fieldWithPath("votes.[].is_voted").type(BOOLEAN).description("유저 투표 여부")
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
