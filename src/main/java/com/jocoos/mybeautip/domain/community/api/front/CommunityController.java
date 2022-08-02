package com.jocoos.mybeautip.domain.community.api.front;

import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityRequest;
import com.jocoos.mybeautip.domain.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.MAX_LONG_STRING;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService service;

    @PostMapping(value = "/1/community", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CommunityResponse> writeCommunity(WriteCommunityRequest request) {

        CommunityResponse response = service.write(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/1/community")
    public ResponseEntity<List<CommunityResponse>> getCommunities(@RequestParam(required = false, name = "category_id") Long categoryId,
                                                                  @RequestParam(required = false, defaultValue = MAX_LONG_STRING) long cursor,
                                                                  @RequestParam(required = false, defaultValue = "20") long size) {

        List<CommunityResponse> response = service.getCommunities();

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/1/community/{community_id}")
    public ResponseEntity<CommunityResponse> getCommunity(@PathVariable(name = "community_id") Long communityId) {

        CommunityResponse response = service.getCommunity(communityId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/1/community/{community_id}")
    public ResponseEntity deleteCommunity(@PathVariable(name = "community_id") Long communityId) {

        service.deleteCommunity(communityId);

        return new ResponseEntity(HttpStatus.OK);
    }


}
