package com.jocoos.mybeautip.domain.broadcast.service.batch;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Component
public class BroadcastBatchUseCaseFactory {

    private final List<BroadcastBatchUseCase> broadcastBatchUseCases;

    @Transactional
    public List<BroadcastUpdateResult> doBatches() {
        Map<BroadcastStatus, BroadcastUpdateResult> response = new EnumMap<>(BroadcastStatus.class);

        List<BroadcastUpdateResult> results = broadcastBatchUseCases.stream()
                .map(BroadcastBatchUseCase::batchUpdate)
                .flatMap(Collection::stream)
                .toList();

        // Merge Result Of Duplicate Status
        for (BroadcastUpdateResult result : results) {
            response.merge(result.status(), result, (existValue, newValue) -> {
                existValue.successIds().addAll(newValue.successIds());
                existValue.failIds().addAll(newValue.failIds());
                return existValue;
            });
        }

        return response.values().stream().toList();
    }
}
