package com.jocoos.mybeautip.domain.member.api.front.deprecated;

import com.jocoos.mybeautip.domain.community.service.CommunityCommentService;
import com.jocoos.mybeautip.domain.community.service.CommunityService;
import com.jocoos.mybeautip.domain.member.dto.MyCommunityCommentResponse;
import com.jocoos.mybeautip.domain.member.dto.MyCommunityResponse;
import com.jocoos.mybeautip.global.wrapper.CursorResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.MAX_LONG_STRING;

// Use MyActivityController Instead
@Deprecated(since = "PlanE")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MyCommunityController {

    private final CommunityService communityService;
    private final CommunityCommentService commentService;

    @GetMapping(value = "/1/my/community")
    public ResponseEntity<CursorResultResponse<MyCommunityResponse>> getMyCommunitiesV1(@RequestParam(required = false, defaultValue = MAX_LONG_STRING) long cursor,
                                                                                      @RequestParam(required = false, defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        List<MyCommunityResponse> response = communityService.getMyCommunities(cursor, pageable);
        for (MyCommunityResponse myCommunityResponse : response) {
            myCommunityResponse.toV1();
        }
        CursorResultResponse<MyCommunityResponse> result = new CursorResultResponse<>(response);

        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/2/my/community")
    public ResponseEntity<CursorResultResponse<MyCommunityResponse>> getMyCommunities(@RequestParam(required = false, defaultValue = MAX_LONG_STRING) long cursor,
                                                                                      @RequestParam(required = false, defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        List<MyCommunityResponse> response = communityService.getMyCommunities(cursor, pageable);
        CursorResultResponse<MyCommunityResponse> result = new CursorResultResponse<>(response);

        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/1/my/community/comment")
    public ResponseEntity<CursorResultResponse<MyCommunityCommentResponse>> getMyCommunityComments(@RequestParam(required = false, defaultValue = MAX_LONG_STRING) long cursor,
                                                                                                   @RequestParam(required = false, defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        List<MyCommunityCommentResponse> response = commentService.getMyComments(cursor, pageable);

        CursorResultResponse<MyCommunityCommentResponse> result = new CursorResultResponse<>(response);

        return ResponseEntity.ok(result);
    }
}
