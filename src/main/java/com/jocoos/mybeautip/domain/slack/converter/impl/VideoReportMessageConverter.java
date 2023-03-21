package com.jocoos.mybeautip.domain.slack.converter.impl;

import com.jocoos.mybeautip.domain.slack.aspect.code.MessageType;
import com.jocoos.mybeautip.domain.slack.converter.MessageConverter;
import com.jocoos.mybeautip.video.report.VideoReport;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.jocoos.mybeautip.domain.slack.aspect.code.MessageType.VIDEO_REPORT;

@Log4j2
@Component
public class VideoReportMessageConverter implements MessageConverter {

    private static final String VIDEO_REPORT_FORMAT = "*영상신고*```%s/%d (이)가 %s/%d 영상을 신고함\n신고이유: %s```";

    @Override
    public MessageType getType() {
        return VIDEO_REPORT;
    }

    @Override
    public Optional<String> toMessage(Object object) {
        if (object instanceof VideoReport report) {
            log.debug("videoReport: {}", report);
            return Optional.ofNullable(
                    String.format(VIDEO_REPORT_FORMAT,
                            report.getCreatedBy().getUsername(),
                            report.getCreatedBy().getId(),
                            report.getVideo().getTitle(),
                            report.getVideo().getId(),
                            report.getReason()));
        }
        return Optional.empty();
    }
}
