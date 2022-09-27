package com.jocoos.mybeautip.domain.home.code;


import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SummaryCommunityType {
    PICK_SUMMARY(CommunityCategoryType.DRIP, 3L, 3),
    TIP_SUMMARY(CommunityCategoryType.NORMAL, 5L, 3),
    VOTE_SUMMARY(CommunityCategoryType.VOTE, 4L, 7),
    BLIND_SUMMARY(CommunityCategoryType.BLIND, 2L, 5);

    private final CommunityCategoryType type;
    private final Long categoryId;
    private final int size;
}
