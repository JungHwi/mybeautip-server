package com.jocoos.mybeautip.domain.term.persistence.converter;

import com.jocoos.mybeautip.domain.term.code.TermUsedInType;
import com.jocoos.mybeautip.global.converter.GenericEnumListConverter;

import javax.persistence.AttributeConverter;
import java.util.List;

public class TermUsedInTypeListConverter extends GenericEnumListConverter<TermUsedInType> implements AttributeConverter<List<TermUsedInType>, String> {

    public TermUsedInTypeListConverter() {
        super(TermUsedInType.class);
    }
}
