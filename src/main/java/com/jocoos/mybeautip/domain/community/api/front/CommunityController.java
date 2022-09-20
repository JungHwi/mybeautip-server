package com.jocoos.mybeautip.domain.community.api.front;

import com.jocoos.mybeautip.domain.community.dto.*;
import com.jocoos.mybeautip.domain.community.service.CommunityService;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import com.jocoos.mybeautip.global.wrapper.CursorResultResponse;
import com.jocoos.mybeautip.member.LegacyMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService service;
    private final LegacyMemberService legacyMemberService;

    @PostMapping(value = "/1/community")
    public ResponseEntity<CommunityResponse> writeCommunity(@RequestBody @Valid WriteCommunityRequest request) {
        CommunityResponse response = service.write(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/1/community/files", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<List<String>> uploadFile(@RequestPart List<MultipartFile> files) {

        List<String> urls = service.upload(files);

        return ResponseEntity.ok(urls);
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

        CursorResultResponse<CommunityResponse> result = new CursorResultResponse<>(response);

        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/1/community/{community_id}")
    public ResponseEntity<CommunityResponse> getCommunity(@PathVariable(name = "community_id") long communityId) {

        CommunityResponse response = service.getCommunity(communityId);

        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/1/community/{community_id}")
    public ResponseEntity<CommunityResponse> editCommunity(@PathVariable(name = "community_id") long communityId,
                                                           @RequestBody EditCommunityRequest request) {
        request.setCommunityId(communityId);

        CommunityResponse response = service.edit(request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/1/community/{community_id}")
    public ResponseEntity deleteCommunity(@PathVariable(name = "community_id") long communityId) {

        service.delete(communityId);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping(value = "/1/community/{community_id}/like")
    public ResponseEntity<LikeResponse> likeCommunity(@PathVariable(name = "community_id") long communityId,
                                                      @RequestBody BooleanDto isLike) {
        long memberId = legacyMemberService.currentMemberId();

        LikeResponse result = service.like(memberId, communityId, isLike.isBool());

        return ResponseEntity.ok(result);
    }

    @PatchMapping(value = "/1/community/{community_id}/report")
    public ResponseEntity<ReportResponse> reportCommunity(@PathVariable(name = "community_id") long communityId,
                                          @RequestBody ReportRequest report) {
        long memberId = legacyMemberService.currentMemberId();

        ReportResponse result = service.report(memberId, communityId, report);

        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/1/community/{community_id}/report")
    public ResponseEntity<ReportResponse> isReportCommunity(@PathVariable(name = "community_id") long communityId) {
        long memberId = legacyMemberService.currentMemberId();

        ReportResponse result = service.isReport(memberId, communityId);

        return ResponseEntity.ok(result);
    }
}
