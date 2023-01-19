package com.jocoos.mybeautip.domain.slack.converter.impl;

import com.jocoos.mybeautip.domain.slack.aspect.code.MessageType;
import com.jocoos.mybeautip.domain.slack.converter.MessageConverter;
import com.jocoos.mybeautip.member.report.Report;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.jocoos.mybeautip.domain.slack.aspect.code.MessageType.MEMBER_REPORT;

@Log4j2
@Component
public class MemberReportMessageConverter implements MessageConverter {
    private static final String REPORT_MEMBER_FORMAT = "*회원신고*```%s/%d (이)가 %s/%d 회원을 신고함\n신고이유: %s```";

    @Override
    public MessageType getType() {
        return MEMBER_REPORT;
    }

    @Override
    public Optional<String> toMessage(Object object) {
        if (object instanceof Report report) {
            log.debug("memberReport: {}", report);
            return Optional.ofNullable(
                    String.format(REPORT_MEMBER_FORMAT,
                            report.getMe().getUsername(),
                            report.getMe().getId(),
                            report.getYou().getUsername(),
                            report.getYou().getId(),
                            report.getReason()));
        }
        return Optional.empty();
    }
}
