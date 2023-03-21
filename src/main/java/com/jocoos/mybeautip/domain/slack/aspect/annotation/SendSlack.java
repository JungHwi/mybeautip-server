package com.jocoos.mybeautip.domain.slack.aspect.annotation;


import com.jocoos.mybeautip.domain.slack.aspect.code.MessageType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SendSlack {
    MessageType messageType();
}
