package com.jocoos.mybeautip.global.config.restdoc

import com.fasterxml.jackson.databind.ObjectMapper
import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler
import com.jocoos.mybeautip.client.flipflop.FlipFlopClient
import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteClient
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLBroadcastMessageRequest
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLDirectMessageRequest
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLVideoRoomRequest
import com.jocoos.mybeautip.config.InternalConfig
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastCategoryRepository
import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.GROUP
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCategoryRepository
import com.jocoos.mybeautip.domain.file.service.FlipFlopService
import com.jocoos.mybeautip.domain.member.persistence.repository.InfluencerRepository
import com.jocoos.mybeautip.domain.system.code.SystemOptionType.FREE_LIVE_PERMISSION
import com.jocoos.mybeautip.domain.system.persistence.repository.SystemOptionRepository
import com.jocoos.mybeautip.global.dto.FileDto
import com.jocoos.mybeautip.member.Member
import com.jocoos.mybeautip.member.MemberRepository
import com.jocoos.mybeautip.security.JwtTokenProvider
import com.jocoos.mybeautip.support.RandomUtils
import com.jocoos.mybeautip.testutil.container.TestContainerConfig
import com.jocoos.mybeautip.testutil.fixture.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.*
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.filter.CharacterEncodingFilter
import java.util.Base64
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Disabled
@Import(RestDocsConfig::class)
@ExtendWith(RestDocumentationExtension::class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class RestDocsIntegrationTestSupport : TestContainerConfig() {

    @Autowired
    protected lateinit var restDocs: RestDocumentationResultHandler;

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var influencerRepository: InfluencerRepository

    @Autowired
    private lateinit var communityCategoryRepository: CommunityCategoryRepository

    @Autowired
    private lateinit var broadcastCategoryRepository: BroadcastCategoryRepository

    @Autowired
    private lateinit var systemOptionRepository: SystemOptionRepository

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Autowired
    private lateinit var internalConfig: InternalConfig

    @MockBean
    protected lateinit var awsS3Handler: AwsS3Handler

    @MockBean
    protected lateinit var flipFlopService: FlipFlopService

    @MockBean
    protected lateinit var flipFlopClient: FlipFlopClient

    @MockBean
    protected lateinit var flipFlopLiteClient: FlipFlopLiteClient

    protected lateinit var requestUser: Member
    protected lateinit var requestUserToken: String
    protected lateinit var requestInternalToken: String

    protected lateinit var defaultAdmin: Member
    protected lateinit var defaultAdminToken: String

    protected lateinit var defaultInfluencer: Member
    protected lateinit var defaultInfluencerToken: String

    protected lateinit var groupBroadcastCategory: BroadcastCategory

    // FIXME IS THIS METHOD REALLY CALLED ONCE? SHOULD CHECK
    @PostConstruct
    private fun postConstruct() {
        systemOptionRepository.save(makeSystemOption(FREE_LIVE_PERMISSION, false))

        communityCategoryRepository.save(makeCommunityCategory(id = 1, parentId = null, type = GROUP))
        groupBroadcastCategory =
            broadcastCategoryRepository.save(makeBroadcastCategory(parentId = null, title = "전체"))
        systemOptionRepository.save(makeSystemOption(FREE_LIVE_PERMISSION, false))

        defaultAdmin = memberRepository.save(makeMember(link = 0))
        requestUser = memberRepository.save(makeMember(link = 2))
        defaultInfluencer = memberRepository.save(makeMember(link = 2))
        influencerRepository.save(makeInfluencer(id = defaultInfluencer.id))

        defaultAdminToken = "Bearer " + jwtTokenProvider.auth(defaultAdmin).accessToken
        requestUserToken = "Bearer " + jwtTokenProvider.auth(requestUser).accessToken
        defaultInfluencerToken = "Bearer " + jwtTokenProvider.auth(defaultInfluencer).accessToken
        requestInternalToken = "Basic " + internalConfig.accessToken
    }

    @PreDestroy
    private fun preDestroy() {
        communityCategoryRepository.deleteAll()
        broadcastCategoryRepository.deleteAll()
        memberRepository.deleteAllInBatch(listOf(requestUser, defaultAdmin, defaultInfluencer))
        systemOptionRepository.deleteAll()
    }

    @BeforeEach
    fun setUp(
        context: WebApplicationContext,
        provider: RestDocumentationContextProvider
    ) {
        val configuration = documentationConfiguration(provider)
            .operationPreprocessors()
            .withRequestDefaults(prettyPrint())
            .withResponseDefaults(prettyPrint())

        mockMvc = webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(configuration)
            .apply<DefaultMockMvcBuilder>(springSecurity())
            .alwaysDo<DefaultMockMvcBuilder>(print())
            .alwaysDo<DefaultMockMvcBuilder>(restDocs)
            .addFilters<DefaultMockMvcBuilder>(CharacterEncodingFilter("UTF-8", true))
            .build()

        given(awsS3Handler.copy(any(FileDto::class.java), any())).willReturn("{domain}/{file_directory}/filename")
        flipFlopService.transcode(anyList(), anyLong())
        given(flipFlopLiteClient.createVideoRoom(any(FFLVideoRoomRequest::class.java)))
            .willReturn(makeFFLVideoRoomResponse())
        given(flipFlopLiteClient.startVideoRoom(anyLong())).willReturn(makeFFLVideoRoomResponse())
        given(flipFlopLiteClient.endVideoRoom(anyLong())).willReturn(makeFFLVideoRoomResponse())
        given(flipFlopLiteClient.getChatToken(anyLong())).willReturn(makeFFLChatTokenResponse())
        given(flipFlopLiteClient.getStreamKey(anyLong())).willReturn(makeFFLStreamKeyResponse())
        given(flipFlopLiteClient.cancelVideoRoom(anyLong())).willReturn(makeFFLVideoRoomResponse())
        given(flipFlopLiteClient.broadcastMessage(anyLong(), any(FFLBroadcastMessageRequest::class.java))).willReturn(makeFFLMessageInfo())
        given(flipFlopLiteClient.directMessage(anyLong(), any(FFLDirectMessageRequest::class.java))).willReturn(makeFFLMessageInfo())
        given(flipFlopLiteClient.visibleMessage(anyLong(), anyLong())).willReturn(null)
        given(flipFlopLiteClient.invisibleMessage(anyLong(), anyLong())).willReturn(null)
    }

}
