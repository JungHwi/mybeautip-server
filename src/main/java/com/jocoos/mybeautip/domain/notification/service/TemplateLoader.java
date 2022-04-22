package com.jocoos.mybeautip.domain.notification.service;

import com.jocoos.mybeautip.domain.notification.code.TemplateType;
import com.jocoos.mybeautip.domain.notification.converter.NotificationTemplateConverter;
import com.jocoos.mybeautip.domain.notification.persistence.repository.NotificationTemplateRepository;
import com.jocoos.mybeautip.domain.notification.vo.NotificationTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TemplateLoader implements ApplicationRunner {
    private final NotificationTemplateRepository repository;
    private final NotificationTemplateConverter converter;

    public static Map<TemplateType, NotificationTemplate> templateMap;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<NotificationTemplate> notificationTemplates = converter.convert(repository.findAllBy());

        templateMap = notificationTemplates.stream()
                .collect(Collectors.toMap(NotificationTemplate::getId, Function.identity()));
    }
}
