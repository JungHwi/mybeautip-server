package com.jocoos.mybeautip.domain.notification.aspect.annotation;

import com.jocoos.mybeautip.domain.notification.code.TemplateType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SendNotification {
    TemplateType[] templateTypes();
}
