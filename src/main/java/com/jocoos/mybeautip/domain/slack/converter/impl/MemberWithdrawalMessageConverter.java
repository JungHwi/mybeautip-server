package com.jocoos.mybeautip.domain.slack.converter.impl;

import com.jocoos.mybeautip.domain.slack.aspect.code.MessageType;
import com.jocoos.mybeautip.domain.slack.converter.MessageConverter;
import com.jocoos.mybeautip.log.MemberLeaveLog;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.jocoos.mybeautip.domain.slack.aspect.code.MessageType.MEMBER_WITHDRAWAL;

@Log4j2
@Component
public class MemberWithdrawalMessageConverter implements MessageConverter {

    private static final String WITHDRAWAL_MEMBER_FORMAT = "*회원탈퇴*```사용자: %s/%d\nLink:%d (1:facebook 2:naver 4:kakao 8:apple)\n탈퇴이유: %s```";

    @Override
    public MessageType getType() {
        return MEMBER_WITHDRAWAL;
    }

    @Override
    public Optional<String> toMessage(Object object) {
        if (object instanceof MemberLeaveLog memberLeaveLog) {
            log.info("video: {}", memberLeaveLog);
            return Optional.ofNullable(
                    String.format(WITHDRAWAL_MEMBER_FORMAT,
                            memberLeaveLog.getMember().getUsername(),
                            memberLeaveLog.getMember().getId(),
                            memberLeaveLog.getMember().getLink(),
                            memberLeaveLog.getReason()));
        }
        return Optional.empty();
    }
}
