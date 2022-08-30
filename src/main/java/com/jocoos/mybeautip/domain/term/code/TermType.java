package com.jocoos.mybeautip.domain.term.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 2021-07-28 현재 서버에서 약관 관리하지 않기 때문에 임시로 만듦, 추후 서버에서 약관 관리한다면 삭제
@Getter
@RequiredArgsConstructor
public enum TermType implements CodeValue {
    OVER_14("만 14세 이상"),
    TERMS_OF_SERVICE("서비스 이용약관"),
    PRIVACY_POLICY("개인정보처리방침"),
    MARKETING_INFO("마케팅 정보 활용 동의");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
