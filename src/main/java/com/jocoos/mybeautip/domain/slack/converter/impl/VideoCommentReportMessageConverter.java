package com.jocoos.mybeautip.domain.slack.converter.impl;

import com.jocoos.mybeautip.domain.slack.aspect.code.MessageType;
import com.jocoos.mybeautip.domain.slack.converter.MessageConverter;
import com.jocoos.mybeautip.member.comment.CommentReport;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.jocoos.mybeautip.domain.slack.aspect.code.MessageType.VIDEO_COMMENT_REPORT;

@Log4j2
@Component
public class VideoCommentReportMessageConverter implements MessageConverter {

    private static final String VIDEO_COMMENT_REPORT_FORMAT = "*댓글신고*```%s/%d (이)가 %d 영상의 %s 댓글을 신고함\n신고이유: %s```";

    @Override
    public MessageType getType() {
        return VIDEO_COMMENT_REPORT;
    }

    @Override
    public Optional<String> toMessage(Object object) {
        if (object instanceof CommentReport report) {
            log.debug("commentReport: {}", report);
            return Optional.ofNullable(
                    String.format(VIDEO_COMMENT_REPORT_FORMAT,
                            report.getCreatedBy().getUsername(),
                            report.getCreatedBy().getId(),
                            report.getComment().getVideoId(),
                            report.getComment().getComment(),
                            report.getReason()));
        }
        return Optional.empty();
    }

    private String toReason(int reasonCode) {
        return switch (reasonCode) {
            case 1 -> "홍보 또는 상업적인 내용";
            case 2 -> "음란성 혹은 부적절한 내용";
            case 3 -> "명예훼손 및 저작권 침해등";
            case 4 -> "정치적 성향 및 갈등 조장";
            case 5 -> "허위사실 유포 등";
            default -> "";
        };
    }
}
