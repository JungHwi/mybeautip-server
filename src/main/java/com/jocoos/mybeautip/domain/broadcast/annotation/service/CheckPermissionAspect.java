package com.jocoos.mybeautip.domain.broadcast.annotation.service;

import com.jocoos.mybeautip.domain.broadcast.annotation.CheckPermission;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastViewerDao;
import com.jocoos.mybeautip.global.code.PermissionType;
import com.jocoos.mybeautip.global.exception.AccessDeniedException;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.InternalServerException;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.jocoos.mybeautip.global.exception.ErrorCode.UNAUTHORIZED;


@Log4j2
@Aspect
@Component
@RequiredArgsConstructor
public class CheckPermissionAspect {

    private final BroadcastDao broadcastDao;
    private final BroadcastViewerDao broadcastViewerDao;

    @Before("@annotation(com.jocoos.mybeautip.domain.broadcast.annotation.CheckPermission)")
    public void checkPermission(JoinPoint joinPoint) {
        long broadcastId = getBroadcastId(joinPoint);
        long memberId = getCurrentMemberId();
        Set<PermissionType> availablePermission = getAvailablePermission(joinPoint);

        if (!hasPermission(availablePermission, broadcastId, memberId)) {
            throw new AccessDeniedException(memberId + " doesn't have permission to " + broadcastId + " broadcast.");
        }
    }

    private boolean hasPermission(Set<PermissionType> availablePermission, long broadcastId, long memberId) {
        if (availablePermission.contains(PermissionType.INFLUENCER)) {
            if (isInfluencer(broadcastId, memberId)) {
                return true;
            }
        }

        if (availablePermission.contains(PermissionType.MANAGER)) {
            if (isManager(broadcastId, memberId)) {
                return true;
            }
        }

        return false;
    }

    private boolean isInfluencer(long broadcastId, long memberId) {
        return broadcastDao.isCreator(broadcastId, memberId);
    }

    private boolean isManager(long broadcastId, long memberId) {
        return broadcastViewerDao.isManager(broadcastId, memberId);
    }

    private Set<PermissionType> getAvailablePermission(JoinPoint joinPoint) {
        CheckPermission checkPermission = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(CheckPermission.class);
        return new HashSet<>(Arrays.asList(checkPermission.value()));
    }

    private long getBroadcastId(JoinPoint joinPoint) {
        Object[] parameterValues = joinPoint.getArgs();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        for (int i = 0; i < method.getParameters().length; i++) {
            if (method.getParameters()[i].getName().equals("broadcastId")) {
                return (Long)parameterValues[i];
            }
        }

        throw new BadRequestException("CheckPermission needs broadcastId.");
    }

    private long getCurrentMemberId() {
        if (currentPrincipal() instanceof MyBeautipUserDetails userDetails) {
            return userDetails.getMember().getId();
        }
        throw new InternalServerException(UNAUTHORIZED, "사실 이 에러는 뜨면 안됨. 고치자");
    }

    private Object currentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getPrincipal();
    }
}
