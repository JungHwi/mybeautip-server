package com.jocoos.mybeautip.domain.community.api.admin;

import com.jocoos.mybeautip.domain.community.dto.AdminCommunityCommentResponse;
import com.jocoos.mybeautip.domain.community.dto.EditCommunityCommentRequest;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityCommentRequest;
import com.jocoos.mybeautip.domain.community.service.AdminCommunityCommentService;
import com.jocoos.mybeautip.global.annotation.CurrentMember;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import com.jocoos.mybeautip.global.dto.single.IdDto;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class AdminCommunityCommentController {

    private final AdminCommunityCommentService service;

    @PostMapping("/community/{communityId}/comment")
    public ResponseEntity<AdminCommunityCommentResponse> writeComment(@CurrentMember MyBeautipUserDetails userDetails,
                                                                      @PathVariable Long communityId,
                                                                      @RequestBody WriteCommunityCommentRequest request) {
        request.setCommunityId(communityId);
        request.setMember(userDetails.getMember());
        AdminCommunityCommentResponse response = service.write(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .build()
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/community/{communityId}/comment/{commentId}")
    public ResponseEntity<IdDto> editComment(@PathVariable long communityId,
                                             @PathVariable long commentId,
                                             @RequestBody EditCommunityCommentRequest request) {
        request.setCommunityId(communityId);
        request.setCommentId(commentId);
        return ResponseEntity.ok(new IdDto(service.edit(request)));
    }

    @GetMapping("community/{communityId}/comment")
    public ResponseEntity<PageResponse<AdminCommunityCommentResponse>> getCommunityComments(
            @PathVariable Long communityId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getComments(communityId, PageRequest.of(page - 1, size, DESC, "createdAt")));
    }

    @PatchMapping("community/comment/{commentId}/hide")
    public ResponseEntity<IdDto> hideCommunityComment(@PathVariable Long commentId, @RequestBody BooleanDto request) {
        return ResponseEntity.ok(new IdDto(service.hide(commentId, request.isBool())));
    }
}
