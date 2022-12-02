package com.jocoos.mybeautip.domain.operation.api;

import com.jocoos.mybeautip.domain.operation.code.OperationType;
import com.jocoos.mybeautip.domain.operation.dto.OperationLogResponse;
import com.jocoos.mybeautip.domain.operation.service.OperationLogService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/operation")
public class AdminOperationLogController {

    private final OperationLogService service;

    @GetMapping("/log")
    public ResponseEntity<CursorResultResponse<OperationLogResponse>> getOperationLog(@RequestParam List<OperationType> typeList,
                                                                                      @RequestParam long memberId,
                                                                                      @RequestParam(required = false, defaultValue = "1") int page,
                                                                                      @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        List<OperationLogResponse> result = service.getOperationLogs(typeList, memberId, pageable);

        return ResponseEntity.ok(new CursorResultResponse<>(result));
    }
}
