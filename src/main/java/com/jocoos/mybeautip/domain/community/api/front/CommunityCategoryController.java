package com.jocoos.mybeautip.domain.community.api.front;

import com.jocoos.mybeautip.domain.community.dto.CommunityCategoryResponse;
import com.jocoos.mybeautip.domain.community.service.CommunityCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommunityCategoryController {

    private final CommunityCategoryService communityCategoryService;

    @GetMapping("/1/community/category")
    public ResponseEntity<List<CommunityCategoryResponse>> getCommunityCategory() {

        return ResponseEntity.ok(communityCategoryService.getCommunityCategoryList());
    }
}
