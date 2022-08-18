package com.jocoos.mybeautip.domain.term.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.jocoos.mybeautip.domain.term.code.TermType.*;

// 2021-07-28 현재 서버에서 약관 관리하지 않기 때문에 임시로 만듦, 추후 서버에서 약관 관리한다면 삭제
@Getter
@RequiredArgsConstructor
public enum TermTypeGroup implements CodeValue {
    REQUIRED("필수", new HashSet<>(Arrays.asList(OVER_14, TERMS_OF_SERVICE, PRIVACY_POLICY))),
    OPTIONAL("선택", new HashSet<>(Collections.singletonList(MARKETING_INFO)));

    private final String description;
    private final Set<TermType> types;

    public static boolean isAllRequiredContains(Set<TermType> types) {
        return types.containsAll(TermTypeGroup.REQUIRED.getTypes());
    }

    public static void validTypeOptional(TermType type) {
        if (!TermTypeGroup.OPTIONAL.getTypes().contains(type)) {
            throw new BadRequestException("only optional term can change status");
        }
    }

    @Override
    public String getName() {
        return this.name();
    }
}
