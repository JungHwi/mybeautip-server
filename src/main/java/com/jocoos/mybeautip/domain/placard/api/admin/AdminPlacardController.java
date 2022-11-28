package com.jocoos.mybeautip.domain.placard.api.admin;

import com.jocoos.mybeautip.domain.placard.code.PlacardStatus;
import com.jocoos.mybeautip.domain.placard.dto.AdminPlacardResponse;
import com.jocoos.mybeautip.domain.placard.dto.PatchPlacardRequest;
import com.jocoos.mybeautip.domain.placard.dto.PlacardRequest;
import com.jocoos.mybeautip.domain.placard.service.AdminPlacardService;
import com.jocoos.mybeautip.domain.placard.vo.PlacardSearchCondition;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import com.jocoos.mybeautip.global.dto.single.IdDto;
import com.jocoos.mybeautip.global.vo.SearchOption;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class AdminPlacardController {

    private final AdminPlacardService service;

    @GetMapping("/placard")
    public ResponseEntity<PageResponse<AdminPlacardResponse>> getPlacardsWithStatus(
            @RequestParam(required = false) PlacardStatus status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(name = "start_at", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startAt,
            @RequestParam(name = "end_at", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endAt,
            @RequestParam(name = "is_top_fix", required = false) Boolean isTopFix) {

        SearchOption searchOption = SearchOption.builder()
                .searchQueryString(search)
                .startAt(startAt)
                .endAt(endAt)
                .zoneId(ZoneId.of("Asia/Seoul"))
                .isTopFix(isTopFix)
                .build();

        PlacardSearchCondition condition = PlacardSearchCondition.builder()
                .status(status)
                .pageable(PageRequest.of(page - 1, size))
                .searchOption(searchOption)
                .build();

        return ResponseEntity.ok(service.getPlacards(condition));
    }

    @PostMapping("/placard")
    public ResponseEntity<AdminPlacardResponse> create(@RequestBody @Valid PlacardRequest request) {
        AdminPlacardResponse response = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .build()
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/placard/{placardId}")
    public ResponseEntity<IdDto> edit(@PathVariable Long placardId, @RequestBody PatchPlacardRequest request) {
        return ResponseEntity.ok(new IdDto(service.edit(placardId, request)));
    }

    @DeleteMapping("/placard/{placardId}")
    public ResponseEntity<IdDto> delete(@PathVariable Long placardId) {
        return ResponseEntity.ok(new IdDto(service.delete(placardId)));
    }

    @PatchMapping("/placard/{placardId}/fix")
    public ResponseEntity<IdDto> topFix(@PathVariable Long placardId, @RequestBody BooleanDto isTopFix) {
        return ResponseEntity.ok(new IdDto(service.topFix(placardId, isTopFix.isBool())));
    }

    @PatchMapping("/placard/{placardId}/status")
    public ResponseEntity<IdDto> changeStatus(@PathVariable Long placardId, @RequestBody BooleanDto isActive) {
        return ResponseEntity.ok(new IdDto(service.changeStatus(placardId, isActive.isBool())));
    }

    @PatchMapping("/placard/order/change")
    public void changeOrder(@RequestBody List<Long> ids) {
        service.arrange(ids);
    }
}
