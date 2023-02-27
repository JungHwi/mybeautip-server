package com.jocoos.mybeautip.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PermissionType implements CodeValue {

    INFLUENCER("인플루언서"),
    MANAGER("매니저");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
