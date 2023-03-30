package com.jocoos.mybeautip.domain.broadcast.service.batch;

import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateResult;

import java.util.List;

public interface BroadcastBatchUseCase {
   List<BroadcastUpdateResult> batchUpdate();
}
