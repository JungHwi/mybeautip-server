package com.jocoos.mybeautip.domain.community.api.admin;

import com.jocoos.mybeautip.domain.community.dto.AdminCommunityCommentResponse;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityCommentRequest;
import com.jocoos.mybeautip.domain.community.service.AdminCommunityCommentService;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import com.jocoos.mybeautip.global.dto.single.IdDto;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
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
    public ResponseEntity<AdminCommunityCommentResponse> writeComment(@PathVariable Long communityId, @RequestBody WriteCommunityCommentRequest request) {
        request.setCommunityId(communityId);
        AdminCommunityCommentResponse response = service.write(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .build()
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("community/{communityId}/comment")
    public ResponseEntity<PageResponse<AdminCommunityCommentResponse>> getCommunityComments(
            @PathVariable Long communityId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getComments(communityId, PageRequest.of(page - 1, size, DESC, "createdAt")));
    }

    @PatchMapping("community/comment/{commentId}/hiding")
    public ResponseEntity<IdDto> hideCommunityComment(@PathVariable Long commentId, @RequestBody BooleanDto request) {
        return ResponseEntity.ok(new IdDto(service.hide(commentId, request.isBool())));
    }
}
