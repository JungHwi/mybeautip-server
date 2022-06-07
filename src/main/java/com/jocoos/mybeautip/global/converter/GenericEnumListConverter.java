package com.jocoos.mybeautip.global.converter;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class GenericEnumListConverter<E extends Enum<E>> implements AttributeConverter<List<E>, String> {

    private static final String SPLIT_CHAR = ",";

    private final Class<E> clazz;

    protected GenericEnumListConverter(Class<E> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String convertToDatabaseColumn(List<E> values) {
        if (values.isEmpty()) {
            return null;
        }
        return values.stream()
                .map(Enum::name)
                .collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public List<E> convertToEntityAttribute(String string) {
        if (StringUtils.isBlank(string)) {
            return new ArrayList<>();
        }
        return Stream.of(string.split(SPLIT_CHAR))
                .map(e -> Enum.valueOf(clazz, e))
                .collect(Collectors.toList());
    }

}
