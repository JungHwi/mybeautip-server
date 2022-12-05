package com.jocoos.mybeautip.domain.operation.api;

import com.jocoos.mybeautip.domain.operation.code.OperationType;
import com.jocoos.mybeautip.domain.operation.dto.OperationLogResponse;
import com.jocoos.mybeautip.domain.operation.dto.OperationLogSearchCondition;
import com.jocoos.mybeautip.domain.operation.service.OperationLogService;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/operation")
public class AdminOperationLogController {

    private final OperationLogService service;

    @GetMapping("/log")
    public ResponseEntity<PageResponse<OperationLogResponse>> getOperationLogs(@RequestParam(value = "types") Set<OperationType> types,
                                                                               @RequestParam(value = "target_id") Long targetId,
                                                                               @RequestParam(required = false, defaultValue = "1") int page,
                                                                               @RequestParam(required = false, defaultValue = "10") int size) {
        OperationLogSearchCondition condition = new OperationLogSearchCondition(types, targetId, page, size);

        PageResponse<OperationLogResponse> result = service.getOperationLogs(condition);

        return ResponseEntity.ok(result);
    }
}
