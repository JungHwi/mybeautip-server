package com.jocoos.mybeautip.global.config.restdoc

import com.fasterxml.jackson.databind.ObjectMapper
import com.jocoos.mybeautip.testutil.container.TestContainerConfig
import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler
import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.GROUP
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCategoryRepository
import com.jocoos.mybeautip.domain.file.service.FlipFlopService
import com.jocoos.mybeautip.global.dto.FileDto
import com.jocoos.mybeautip.testutil.fixture.makeCommunityCategory
import com.jocoos.mybeautip.testutil.fixture.makeMember
import com.jocoos.mybeautip.member.Member
import com.jocoos.mybeautip.member.MemberRepository
import com.jocoos.mybeautip.security.JwtTokenProvider
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
    private lateinit var communityCategoryRepository: CommunityCategoryRepository

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @MockBean
    protected lateinit var awsS3Handler: AwsS3Handler

    @MockBean
    protected lateinit var flipFlopService: FlipFlopService

    protected lateinit var requestUser: Member
    protected lateinit var requestUserToken: String

    protected lateinit var defaultAdmin: Member
    protected lateinit var defaultAdminToken: String

    @PostConstruct
    private fun postConstruct() {
        communityCategoryRepository.save(makeCommunityCategory(id = 1, parentId = null, type = GROUP))
        defaultAdmin = memberRepository.save(makeMember(link = 0))
        requestUser = memberRepository.save(makeMember(link = 2))
        defaultAdminToken = "Bearer " + jwtTokenProvider.auth(defaultAdmin).accessToken
        requestUserToken = "Bearer " + jwtTokenProvider.auth(requestUser).accessToken
    }

    @PreDestroy
    private fun preDestroy() {
        communityCategoryRepository.deleteAll()
        memberRepository.deleteAllInBatch(listOf(requestUser, defaultAdmin))
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
    }

}
