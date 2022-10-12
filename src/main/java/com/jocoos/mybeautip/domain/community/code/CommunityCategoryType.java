package com.jocoos.mybeautip.domain.community.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Getter
public enum CommunityCategoryType implements CodeValue {

    GENERAL("일반 게시판"),
    ANONYMOUS("익명 게시판"),
    GROUP("그룹"),
    NORMAL("일반"),
    BLIND("속닥속닥"),
    DRIP("드립"),
    EVENT("이벤트"),
    VOTE("결정픽");

    private String description;

    private static final Set<CommunityCategoryType> summaryTypes = new HashSet<>(Arrays.asList(BLIND, VOTE));

    public static boolean supportsSummary(CommunityCategoryType type) {
        return summaryTypes.contains(type);
    }

    @Override
    public String getName() {
        return this.name();
    }
}
