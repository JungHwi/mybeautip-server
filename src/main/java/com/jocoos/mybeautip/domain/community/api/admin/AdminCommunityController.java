package com.jocoos.mybeautip.domain.community.api.admin;

import com.jocoos.mybeautip.domain.community.dto.AdminCommunityResponse;
import com.jocoos.mybeautip.domain.community.dto.CommunityCategoryResponse;
import com.jocoos.mybeautip.domain.community.service.AdminCommunityService;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class AdminCommunityController {

    private final AdminCommunityService service;

    @GetMapping("/community/category")
    public ResponseEntity<List<CommunityCategoryResponse>> getCommunityCategories() {
        return ResponseEntity.ok(service.getCategories());
    }

    @GetMapping("/community")
    public ResponseEntity<PageResponse<List<AdminCommunityResponse>>> getCommunities() {
        return ResponseEntity.ok(service.getCommunities());
    }
}
