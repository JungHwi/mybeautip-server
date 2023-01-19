package com.jocoos.mybeautip.domain.slack.converter.impl;

import com.jocoos.mybeautip.admin.Dates;
import com.jocoos.mybeautip.domain.slack.aspect.code.MessageType;
import com.jocoos.mybeautip.domain.slack.converter.MessageConverter;
import com.jocoos.mybeautip.member.point.MemberPoint;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Optional;

import static com.jocoos.mybeautip.domain.slack.aspect.code.MessageType.POINT_EXPIRED;

@Log4j2
@Component
public class MemberPointExpiredMessageConverter implements MessageConverter {

    @Override
    public MessageType getType() {
        return POINT_EXPIRED;
    }

    @Override
    public Optional<String> toMessage(Object object) {
        if (object instanceof MemberPoint memberPoint) {
            log.debug("point: {}", memberPoint);

            String details = String.format("%s/%d - 포인트: -%s, 유효기간: %s",
                    memberPoint.getMember().getUsername(),
                    memberPoint.getMember().getId(),
                    memberPoint.getFormattedPoint(),
                    Dates.toString(memberPoint.getExpiryAt(),
                            ZoneId.of("Asia/Seoul")));

            String message = String.format("*포인트(%d) 차감*```%s```", memberPoint.getPoint(), details);
            return Optional.of(message);
        }
        return Optional.empty();
    }
}
