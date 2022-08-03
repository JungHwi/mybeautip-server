package com.jocoos.mybeautip.domain.community.api.front;

import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.community.dto.EditCommunityRequest;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityRequest;
import com.jocoos.mybeautip.domain.community.service.CommunityService;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import com.jocoos.mybeautip.member.LegacyMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.MAX_LONG_STRING;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService service;
    private final LegacyMemberService legacyMemberService;

    @PostMapping(value = "/1/community")
    public ResponseEntity<CommunityResponse> writeCommunity(@RequestBody WriteCommunityRequest request) {

        CommunityResponse response = service.write(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/1/community/files", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<List<String>> uploadFile(@RequestPart List<MultipartFile> files) {

        List<String> urls = service.upload(files);

        return ResponseEntity.ok(urls);
    }

    @GetMapping(value = "/1/community")
    public ResponseEntity<List<CommunityResponse>> getCommunities(@RequestParam(required = false, name = "category_id") Long categoryId,
                                                                  @RequestParam(required = false, defaultValue = MAX_LONG_STRING) long cursor,
                                                                  @RequestParam(required = false, defaultValue = "20") long size) {

        List<CommunityResponse> response = service.getCommunities();

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/1/community/{community_id}")
    public ResponseEntity<CommunityResponse> getCommunity(@PathVariable(name = "community_id") long communityId) {

        CommunityResponse response = service.get(communityId);

        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/1/community/{community_id}")
    public ResponseEntity<CommunityResponse> editCommunity(@PathVariable(name = "community_id") long communityId, EditCommunityRequest request) {

        CommunityResponse response = service.edit(request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/1/community/{community_id}")
    public ResponseEntity deleteCommunity(@PathVariable(name = "community_id") long communityId) {

        service.delete(communityId);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping(value = "/1/community/{community_id}/like")
    public ResponseEntity likeCommunity(@PathVariable(name = "community_id") long communityId,
                                        @RequestBody BooleanDto isLike) {
        long memberId = legacyMemberService.currentMemberId();

        service.like(memberId, communityId, isLike.isBool());

        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping(value = "/1/community/{community_id}/report")
    public ResponseEntity reportCommunity(@PathVariable(name = "community_id") long communityId,
                                          @RequestBody BooleanDto isReport) {
        long memberId = legacyMemberService.currentMemberId();

        service.report(memberId, communityId, isReport.isBool());

        return new ResponseEntity(HttpStatus.OK);
    }
}
