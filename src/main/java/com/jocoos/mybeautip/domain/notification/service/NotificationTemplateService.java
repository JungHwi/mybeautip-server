package com.jocoos.mybeautip.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationTemplateService {

    private final TemplateLoader templateLoader;

    public void refresh() {
        try {
            templateLoader.run(null);
        } catch (Exception e) {
            log.error("Failed to refresh notification template.");
        }
    }
}
