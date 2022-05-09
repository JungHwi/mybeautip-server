package com.jocoos.mybeautip.global.converter;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class GenericEnumSetConverter<E extends Enum<E>> implements AttributeConverter<Set<E>, String> {

    private static final String SPLIT_CHAR = ",";

    private final Class<E> clazz;

    protected GenericEnumSetConverter(Class<E> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String convertToDatabaseColumn(Set<E> values) {
        if (values.isEmpty()) {
            return null;
        }
        return values.stream()
                .map(Enum::name)
                .collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public Set<E> convertToEntityAttribute(String string) {
        if (StringUtils.isBlank(string)) {
            return EnumSet.noneOf(clazz);
        }
        Set<E> values = Stream.of(string.split(SPLIT_CHAR))
                .map(e -> Enum.valueOf(clazz, e))
                .collect(Collectors.toSet());

        return EnumSet.copyOf(values);
    }

}
