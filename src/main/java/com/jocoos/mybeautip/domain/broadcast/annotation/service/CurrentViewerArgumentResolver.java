package com.jocoos.mybeautip.domain.broadcast.annotation.service;

import com.jocoos.mybeautip.domain.broadcast.annotation.CurrentViewer;
import com.jocoos.mybeautip.domain.broadcast.service.child.BroadcastViewerVoService;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastViewerVo;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CurrentViewerArgumentResolver implements HandlerMethodArgumentResolver {
    private final BroadcastViewerVoService service;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAnnotation = parameter.hasParameterAnnotation(CurrentViewer.class);
        boolean hasParameterType = parameter.getParameterType().equals(BroadcastViewerVo.class);
        return hasAnnotation && hasParameterType;
    }

    @Override
    public BroadcastViewerVo resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        long broadcastId = getBroadcastId(webRequest);

        return service.of(broadcastId, currentPrincipal().getUsername());
    }

    private MyBeautipUserDetails currentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof MyBeautipUserDetails userDetails) {
            return userDetails;
        }

        return null;
    }

    private long getBroadcastId(NativeWebRequest webRequest) {
        Map<String, Object> map = (Map<String, Object>) webRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        if (map == null) {
            throw new BadRequestException("CurrentViewer needs broadcastId.");
        }

        Long broadcastId = Long.parseLong((String) map.get("broadcastId"));
        if (broadcastId == null) {
            throw new BadRequestException("CurrentViewer needs broadcastId.");
        }

        return broadcastId;
    }
}
