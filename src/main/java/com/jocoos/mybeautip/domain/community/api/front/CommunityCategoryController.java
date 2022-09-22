package com.jocoos.mybeautip.domain.community.api.front;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.dto.CommunityCategoryResponse;
import com.jocoos.mybeautip.domain.community.service.CommunityCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommunityCategoryController {

    private final CommunityCategoryService communityCategoryService;

    @GetMapping("/1/community/category")
    public ResponseEntity<List<CommunityCategoryResponse>> getCommunityCategories(@RequestParam(value = "type", defaultValue = "GENERAL") CommunityCategoryType type) {

        return ResponseEntity.ok(communityCategoryService.getLowerCategoryList(type));
    }

    @GetMapping("/1/community/category/{categoryId}")
    public ResponseEntity<CommunityCategoryResponse> getCommunityCategory(@PathVariable long categoryId) {

        return ResponseEntity.ok(communityCategoryService.getCommunityCategory(categoryId));
    }
}
