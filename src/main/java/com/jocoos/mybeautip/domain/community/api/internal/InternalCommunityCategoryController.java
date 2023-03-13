package com.jocoos.mybeautip.domain.community.api.internal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.jocoos.mybeautip.domain.community.dto.CommunityCategoryResponse;
import com.jocoos.mybeautip.domain.community.service.AdminCommunityService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/internal")
@RestController
public class InternalCommunityCategoryController {

    private final AdminCommunityService service;

    @GetMapping("/1/community/category")
    public ResponseEntity<List<CommunityCategoryResponse>> getCommunityCategories() {
        return ResponseEntity.ok(service.getCategories());
    }
}
