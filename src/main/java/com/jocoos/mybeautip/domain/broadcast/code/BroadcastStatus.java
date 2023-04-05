package com.jocoos.mybeautip.domain.broadcast.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@AllArgsConstructor
public enum BroadcastStatus implements CodeValue {

    SCHEDULED("예정", false),
    READY("준비", false),
    LIVE("방송중", true),
    END("종료", true),
    CANCEL("취소", true)
    ;

    private final String description;
    private final boolean canManuallyChange;

    public static final List<BroadcastStatus> ACTIVE_STATUSES = List.of(SCHEDULED, READY, LIVE);

    private static final Map<BroadcastStatus, Set<BroadcastStatus>> CHANGE_CANDIDATE_MAP = new EnumMap<>(BroadcastStatus.class);
    static {
        CHANGE_CANDIDATE_MAP.put(SCHEDULED, Set.of(READY, CANCEL));
        CHANGE_CANDIDATE_MAP.put(READY, Set.of(LIVE, CANCEL));
        CHANGE_CANDIDATE_MAP.put(LIVE, Set.of(END));
        CHANGE_CANDIDATE_MAP.put(END, Set.of());
        CHANGE_CANDIDATE_MAP.put(CANCEL, Set.of());
    }

    private static final Map<BroadcastStatus, List<BroadcastStatus>> singletonListCache = new ConcurrentHashMap<>();

    public static List<BroadcastStatus> getSearchStatuses(BroadcastStatus status) {
        return singletonListCache.computeIfAbsent(status, List::of);
    }

    public BroadcastStatus changeTo(BroadcastStatus status) {
        if (this == status) {
            return this;
        }
        Set<BroadcastStatus> candidate = CHANGE_CANDIDATE_MAP.get(this);
        if (!candidate.contains(status)) {
            throw new BadRequestException(this + " can only change status to " + candidate + " request status is " + status);
        }
        return status;
    }

    @Override
    public String getName() {
        return this.name();
    }
}
