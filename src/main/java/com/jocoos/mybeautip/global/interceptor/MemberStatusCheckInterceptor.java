package com.jocoos.mybeautip.global.interceptor;

import com.jocoos.mybeautip.domain.member.dto.ExceptionMemberResponse;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.global.exception.AuthenticationException;
import com.jocoos.mybeautip.global.util.date.ZonedDateTimeUtil;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.jocoos.mybeautip.global.exception.ErrorCode.EXILED_MEMBER;
import static com.jocoos.mybeautip.global.exception.ErrorCode.SUSPENDED_MEMBER;

// /api/1/token 접근시에만 동작, AuthorizationConfig 참고
@RequiredArgsConstructor
@Component
public class MemberStatusCheckInterceptor implements HandlerInterceptor {

    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String GRANT_TYPE = "grant_type";
    private final MemberDao memberDao;
    private final JwtTokenProvider tokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {

        if (REFRESH_TOKEN.equals(request.getParameter(GRANT_TYPE))) {
            String refreshToken = request.getParameter(REFRESH_TOKEN);
            long memberId = Long.parseLong(tokenProvider.getMemberId(refreshToken));
            Member member = memberDao.getMember(memberId);
            checkMemberStatus(member);
        }

        return true;
    }

    private static void checkMemberStatus(Member member) {
        switch (member.getStatus()) {
            case SUSPENDED -> {
                ExceptionMemberResponse errorResponse = ExceptionMemberResponse.builder()
                        .memberId(member.getId())
                        .date(ZonedDateTimeUtil.toString(member.getModifiedAtZoned().plusDays(14)))
                        .build();
                throw new AuthenticationException(SUSPENDED_MEMBER, errorResponse);
            }
            case EXILE -> throw new AuthenticationException(EXILED_MEMBER);
        }
    }
}
