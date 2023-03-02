package com.jocoos.mybeautip.domain.broadcast.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@AllArgsConstructor
public enum BroadcastStatus implements CodeValue {

    SCHEDULED("예정"),
    READY("준비"),
    LIVE("방송중"),
    END("종료"),
    CANCEL("취소");

    private final String description;

    public static final List<BroadcastStatus> DEFAULT_SEARCH_STATUSES = List.of(SCHEDULED, READY, LIVE);
    private static final Map<BroadcastStatus, List<BroadcastStatus>> singletonListCache = new ConcurrentHashMap<>();

    public static List<BroadcastStatus> getSearchStatuses(BroadcastStatus status) {
        return singletonListCache.computeIfAbsent(status, List::of);
    }

    public BroadcastStatus toEnd() {
        if (this != LIVE) {
            throw new BadRequestException("");
        }
        return END;
    }

    @Override
    public String getName() {
        return this.name();
    }
}
