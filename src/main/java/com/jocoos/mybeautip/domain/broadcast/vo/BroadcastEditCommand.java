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
    private final boolean isSoundOn;
    private final boolean isScreenShow;
    private final Long editorId;

    public static BroadcastEditCommand edit(BroadcastEditRequest request,
                                            String editedThumbnailUrl,
                                            BroadcastCategory editedCategory,
                                            Long editorId) {
        return BroadcastEditCommand.builder()
                .isStartNow(request.getIsStartNow())
                .editedStartedAt(request.getStartedAt())
                .editedTitle(request.getTitle())
                .editedNotice(request.getNotice())
                .editedCategory(editedCategory)
                .editedThumbnail(editedThumbnailUrl)
                .isScreenShow(request.getIsScreenShow())
                .isSoundOn(request.getIsSoundOn())
                .editorId(editorId)
                .build();
    }

    public static BroadcastEditCommand patch(Broadcast original,
                                             BroadcastPatchRequest request,
                                             Long editorId) {
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
                .isSoundOn(original.getIsSoundOn())
                .isScreenShow(original.getIsScreenShow())
                .editorId(editorId)
                .build();
    }
}
