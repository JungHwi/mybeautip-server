package com.jocoos.mybeautip.domain.notification.service;

public interface NotificationService<E> {
    void send(E object);
}
