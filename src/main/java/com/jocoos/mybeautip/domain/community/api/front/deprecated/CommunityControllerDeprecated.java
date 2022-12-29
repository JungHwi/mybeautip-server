package com.jocoos.mybeautip.domain.community.api.front.deprecated;

import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.community.dto.EditCommunityRequest;
import com.jocoos.mybeautip.domain.community.dto.SearchCommunityRequest;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityRequest;
import com.jocoos.mybeautip.domain.community.service.CommunityService;
import com.jocoos.mybeautip.global.wrapper.CursorResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @deprecated 파일 타입 추가 전 컨트롤러
 */
@Deprecated(since = "클라이언트 비디오 업로드 완성 후", forRemoval = true)
@Log4j2
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommunityControllerDeprecated {

    private final CommunityService service;

    @PostMapping(value = "/1/community")
    public ResponseEntity<CommunityResponse> writeCommunity(@RequestBody WriteCommunityRequest request) {
        CommunityResponse response = service.write(request);
        response.toV1();
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/1/community")
    public ResponseEntity<CursorResultResponse<CommunityResponse>> getCommunities(@RequestParam(required = false, defaultValue = "1", name = "category_id") Long categoryId,
                                                                                  @RequestParam(required = false, name = "event_id") Long eventId,
                                                                                  @RequestParam(required = false) ZonedDateTime cursor,
                                                                                  @RequestParam(required = false, defaultValue = "20") int size) {
        
        SearchCommunityRequest request = SearchCommunityRequest.builder()
                .categoryId(categoryId)
                .eventId(eventId)
                .cursor(cursor)
                .build();

        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "sortedAt"));

        List<CommunityResponse> response = service.getCommunities(request, pageable);

        for (CommunityResponse communityResponse : response) {
            communityResponse.toV1();
        }

        CursorResultResponse<CommunityResponse> result = new CursorResultResponse<>(response);

        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/1/community/{community_id}")
    public ResponseEntity<CommunityResponse> getCommunity(@PathVariable(name = "community_id") long communityId) {

        CommunityResponse response = service.getCommunity(communityId);
        response.toV1();
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/1/community/{community_id}")
    public ResponseEntity<CommunityResponse> editCommunity(@PathVariable(name = "community_id") long communityId,
                                                           @RequestBody EditCommunityRequest request) {
        request.setCommunityId(communityId);

        CommunityResponse response = service.edit(request);
        response.toV1();

        return ResponseEntity.ok(response);
    }
}
