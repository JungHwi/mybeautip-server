package com.jocoos.mybeautip.domain.event.converter;

import com.jocoos.mybeautip.domain.event.dto.AdminEventProductResponse;
import com.jocoos.mybeautip.domain.event.dto.EventProductRequest;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventProduct;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Collections;
import java.util.List;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.MAX_INTEGER_STRING;

@Mapper(componentModel = "spring")
public abstract class AdminEventProductConverter {

    @Mappings({
            @Mapping(target = "name", constant = "name"),
            @Mapping(target = "quantity", constant = MAX_INTEGER_STRING),
            @Mapping(target = "imageFile", ignore = true),
            @Mapping(target = "event", ignore = true),
    })
    abstract EventProduct converts(EventProductRequest request);

    public List<EventProduct> convertToList(EventProductRequest request) {
        EventProduct product = converts(request);
        return Collections.singletonList(product);
    }

    abstract AdminEventProductResponse converts(EventProduct products);

    public AdminEventProductResponse converts(List<EventProduct> products) {
        if (CollectionUtils.isNotEmpty(products)) {
            return converts(products.get(0));
        }
        return null;
    }
}