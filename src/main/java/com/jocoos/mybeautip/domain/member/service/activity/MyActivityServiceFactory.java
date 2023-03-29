package com.jocoos.mybeautip.domain.member.service.activity;

import com.jocoos.mybeautip.domain.member.code.MemberActivityType;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MyActivityServiceFactory {

    private final Map<MemberActivityType, MyActivityService<?>> myActivityServiceMap;

    public MyActivityServiceFactory(List<MyActivityService<?>> myActivityServices) {
        this.myActivityServiceMap = myActivityServices.stream()
                .collect(Collectors.toMap(MyActivityService::getType, myActivityService -> myActivityService));
    }

    @SuppressWarnings("unchecked")
    public <T extends CursorInterface> MyActivityService<T> get(MemberActivityType type) {
        return (MyActivityService<T>) myActivityServiceMap.get(type);
    }
}
