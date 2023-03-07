package com.jocoos.mybeautip.domain.broadcast.vo;

import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastEditRequest;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastPatchRequest;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.util.JsonNullableUtils.getIfPresent;
import static lombok.AccessLevel.PRIVATE;

@Getter
@Builder(access = PRIVATE)
@RequiredArgsConstructor(access = PRIVATE)
public class BroadcastEditCommand {

    private final boolean isStartNow;
    private final ZonedDateTime editedStartedAt;
    private final BroadcastCategory editedCategory;
    private final String editedTitle;
    private final String editedNotice;
    private final String editedThumbnail;

    public static BroadcastEditCommand edit(BroadcastEditRequest request, BroadcastCategory editedCategory) {
        return BroadcastEditCommand.builder()
                .isStartNow(request.getIsStartNow())
                .editedStartedAt(request.getStartedAt())
                .editedCategory(editedCategory)
                .editedTitle(request.getTitle())
                .editedNotice(request.getNotice())
                .editedThumbnail(request.getThumbnailUrl())
                .build();
    }

    public static BroadcastEditCommand patch(Broadcast original, BroadcastPatchRequest request) {
        String editedTitle = getIfPresent(request.getTitle(), original.getTitle());
        String editedNotice = getIfPresent(request.getNotice(), original.getNotice());
        String editedThumbnailUrl = getIfPresent(request.getThumbnailUrl(), original.getThumbnailUrl());
        return BroadcastEditCommand.builder()
                .isStartNow(false)
                .editedStartedAt(original.getStartedAt())
                .editedCategory(original.getCategory())
                .editedTitle(editedTitle)
                .editedNotice(editedNotice)
                .editedThumbnail(editedThumbnailUrl)
                .build();
    }
}
