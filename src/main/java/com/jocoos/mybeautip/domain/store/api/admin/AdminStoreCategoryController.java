package com.jocoos.mybeautip.domain.store.api.admin;

import com.jocoos.mybeautip.domain.store.dto.CreateStoreCategoryRequest;
import com.jocoos.mybeautip.domain.store.dto.StoreCategoryResponse;
import com.jocoos.mybeautip.domain.store.service.StoreCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/store/category")
public class AdminStoreCategoryController {

    private final StoreCategoryService service;

    @PostMapping
    public StoreCategoryResponse create(@RequestBody CreateStoreCategoryRequest request) {
        return service.create(request);
    }

}
