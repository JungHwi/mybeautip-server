package com.jocoos.mybeautip.domain.store.api.admin;

import com.jocoos.mybeautip.domain.store.code.StoreCategoryStatus;
import com.jocoos.mybeautip.domain.store.dto.*;
import com.jocoos.mybeautip.domain.store.service.StoreCategoryService;
import com.jocoos.mybeautip.global.dto.single.IntegerDto;
import com.jocoos.mybeautip.global.dto.single.LongDto;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/store/category")
public class AdminStoreCategoryController {

    private final StoreCategoryService service;

    @PostMapping
    public StoreCategoryResponse create(@RequestBody CreateStoreCategoryRequest request) {
        return service.create(request);
    }

    @GetMapping
    public PageResponse<StoreCategoryListResponse> search(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "100") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "sort"));
        SearchStoreCategoryRequest request = SearchStoreCategoryRequest.builder()
                .statuses(List.of(StoreCategoryStatus.ACTIVE, StoreCategoryStatus.INACTIVE))
                .pageable(pageable)
                .build();

        Page<StoreCategoryListResponse> result = service.search(request);

        return new PageResponse(result) ;
    }

    @GetMapping("/{categoryId}")
    public StoreCategoryResponse get(@PathVariable long categoryId) {
        return service.get(categoryId);
    }

    @PutMapping("/{categoryId}")
    public StoreCategoryResponse edit(@PathVariable long categoryId,
                                      @RequestBody EditStoreCategoryRequest request) {

        return service.edit(categoryId, request);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity delete(@PathVariable long categoryId,
                                 @RequestBody LongDto request) {
        service.delete(categoryId, request.getNumber());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{categoryId}")
    public StoreCategoryResponse changeSort(@PathVariable long categoryId,
                                            @RequestBody IntegerDto request) {
        return service.changeSort(categoryId, request.getNumber());
    }
}
