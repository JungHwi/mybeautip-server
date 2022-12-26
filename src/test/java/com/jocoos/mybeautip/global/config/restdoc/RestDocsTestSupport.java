package com.jocoos.mybeautip.global.config.restdoc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.restapi.dto.SignupRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcOperationPreprocessorsConfigurer;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

@Disabled
@Import({RestDocsConfig.class})
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@SpringBootTest
public class RestDocsTestSupport {

    @Autowired
    protected RestDocumentationResultHandler restDocs;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected AwsS3Handler awsS3Handler;

    @BeforeEach
    void setUp(final WebApplicationContext context,
               final RestDocumentationContextProvider provider) {
        MockMvcOperationPreprocessorsConfigurer configuration = documentationConfiguration(provider)
                .operationPreprocessors()
                .withRequestDefaults(prettyPrint())
                .withResponseDefaults(prettyPrint());

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(configuration)
                .alwaysDo(MockMvcResultHandlers.print())
                .alwaysDo(restDocs)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        given(awsS3Handler.copy(any(FileDto.class), any())).willReturn("{domain}/{file_directory}/filename");
    }

    protected String createJson(Object dto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(dto);
    }

    protected Member defaultMember(Long memberId, String socialId) {;
        final String grantType = "naver";
        final String empty = "";
        SignupRequest request = createRequest(socialId, grantType, empty);

        Member member = new Member(request);
        member.setId(memberId);
        return member;
    }

    private SignupRequest createRequest(String socialId, String grantType, String empty) {
        SignupRequest request = new SignupRequest();
        request.setSocialId(socialId);
        request.setGrantType(grantType);
        request.setUsername(empty);
        request.setEmail(empty);
        request.setAvatarUrl(empty);
        return request;
    }
}
