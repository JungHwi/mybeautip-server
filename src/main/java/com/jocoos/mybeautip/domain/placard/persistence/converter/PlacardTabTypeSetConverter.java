package com.jocoos.mybeautip.domain.placard.persistence.converter;

import com.jocoos.mybeautip.domain.placard.code.PlacardTabType;
import com.jocoos.mybeautip.global.converter.GenericEnumSetConverter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Set;

@Converter
public class PlacardTabTypeSetConverter extends GenericEnumSetConverter<PlacardTabType> implements AttributeConverter<Set<PlacardTabType>, String> {

    public PlacardTabTypeSetConverter() {
        super(PlacardTabType.class);
    }
}
