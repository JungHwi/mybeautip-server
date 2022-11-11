package com.jocoos.mybeautip.domain.community.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommunityStatus implements CodeValue {

    NORMAL("일반적인 상태", true),
    HIDE("관리자 숨김 상태", false),
    DELETE("삭제된 상태", false);

    private final String description;
    private final boolean deletable;

    @Override
    public String getName() {
        return this.name();
    }

    public CommunityStatus hide(boolean isHide) {
        return isHide ? hide() : disableHide();
    }

    public CommunityStatus hide() {
        if (this.equals(DELETE)) {
            throw new BadRequestException("삭제 상태는 숨김 상태가 불가능합니다.");
        }
        return HIDE;
    }

    public CommunityStatus disableHide() {
        if (!this.equals(HIDE)) {
            throw new BadRequestException("숨김 상태 해제는 숨김 상태만 가능합니다");
        }
        return NORMAL;
    }

}
