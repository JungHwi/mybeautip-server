package com.jocoos.mybeautip.domain.scrap.service;

import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ScrapTypeFactory {

    private final Map<ScrapType, ScrapTypeService<?>> scrapTypeServiceMap;

    public ScrapTypeFactory(List<ScrapTypeService<?>> scrapTypeServices) {
        this.scrapTypeServiceMap = scrapTypeServices.stream()
                .collect(Collectors.toMap(ScrapTypeService::getType, scrapTypeService -> scrapTypeService));
    }


    @SuppressWarnings("unchecked")
    public <T extends CursorInterface> ScrapTypeService<T> get(ScrapType type) {
        return (ScrapTypeService<T>) scrapTypeServiceMap.get(type);
    }
}
