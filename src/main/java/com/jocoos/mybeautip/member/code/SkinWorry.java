package com.jocoos.mybeautip.member.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SkinWorry implements CodeValue {

    WHITENING("미백"),
    TROUBLE("트러블"),
    DRY_INSIDE("속건조"),
    WRINKLES("탄력/주름"),
    DARK_CIRCLES("다크서클"),
    BLEMISHES("잡티"),
    BLUSHING("홍조"),
    EXTREMELY_DRY("악건성"),
    SKIN_TEXTURE("피부결"),
    PORES_SCARS("모공/흉터"),
    SEBUM("피지");

    private final String description;
}
