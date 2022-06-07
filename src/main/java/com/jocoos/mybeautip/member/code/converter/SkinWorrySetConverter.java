package com.jocoos.mybeautip.member.code.converter;

import com.jocoos.mybeautip.global.converter.GenericEnumSetConverter;
import com.jocoos.mybeautip.member.code.SkinWorry;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Set;

@Converter
public class SkinWorrySetConverter extends GenericEnumSetConverter<SkinWorry> implements AttributeConverter<Set<SkinWorry>, String> {

    public SkinWorrySetConverter() {
        super(SkinWorry.class);
    }
}
