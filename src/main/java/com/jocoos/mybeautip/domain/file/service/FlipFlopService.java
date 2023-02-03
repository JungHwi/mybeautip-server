package com.jocoos.mybeautip.domain.file.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jocoos.mybeautip.client.flipflop.FlipFlopClient;
import com.jocoos.mybeautip.global.exception.InternalServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.global.exception.ErrorCode.JSON_SERIALIZATION_EXCEPTION;

@Log4j2
@RequiredArgsConstructor
@Service
public class FlipFlopService {

    @Value("${flipflop.access-token}")
    private String flipFlopAccessToken;

    @Value("${flipflop.default-filename}")
    private String defaultFilename;

    private final ObjectMapper objectMapper;
    private final FlipFlopClient flipFlopClient;

    public void transcode(List<String> videoUrls, long communityId) {
        String data = getData(communityId);
        for (String videoUrl : videoUrls) {
            flipFlopClient.uploadVideo(flipFlopAccessToken, videoUrl, data);
        }
    }

    public void delete(List<String> videoUrls) {

    }

    // flip flop api 문서 참고
    private String getData(long communityId) {
        try {
            return objectMapper.writeValueAsString(
                    Map.of(
                            "community_id", communityId,
                            "filename", defaultFilename
                    ));
        } catch (JsonProcessingException e) {
            throw new InternalServerException(JSON_SERIALIZATION_EXCEPTION, e);
        }
    }
}
