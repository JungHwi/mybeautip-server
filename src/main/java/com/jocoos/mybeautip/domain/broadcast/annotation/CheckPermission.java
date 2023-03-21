package com.jocoos.mybeautip.domain.broadcast.annotation;

import com.jocoos.mybeautip.global.code.PermissionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckPermission {

    PermissionType[] value();
}
