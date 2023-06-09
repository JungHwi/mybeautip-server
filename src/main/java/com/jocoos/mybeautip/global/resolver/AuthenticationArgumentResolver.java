package com.jocoos.mybeautip.global.resolver;

import com.jocoos.mybeautip.global.annotation.CurrentMember;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import lombok.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthenticationArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAnnotation = parameter.hasParameterAnnotation(CurrentMember.class);
        boolean hasParameterType = parameter.getParameterType().equals(MyBeautipUserDetails.class);
        return hasAnnotation && hasParameterType;
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter, ModelAndViewContainer mavContainer, @NonNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return currentUserDetails();
    }

    public MyBeautipUserDetails currentUserDetails() {
        if (currentPrincipal() instanceof MyBeautipUserDetails userDetails) {
            return userDetails;
        }
        return null;
    }

    private Object currentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getPrincipal();
    }
}
