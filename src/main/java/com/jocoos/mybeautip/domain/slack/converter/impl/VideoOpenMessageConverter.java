package com.jocoos.mybeautip.domain.slack.converter.impl;

import com.jocoos.mybeautip.domain.slack.aspect.code.MessageType;
import com.jocoos.mybeautip.domain.slack.converter.MessageConverter;
import com.jocoos.mybeautip.support.slack.SlackMessageFormat;
import com.jocoos.mybeautip.video.Video;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.jocoos.mybeautip.domain.slack.aspect.code.MessageType.VIDEO_OPEN;

@Component
public class VideoOpenMessageConverter implements MessageConverter {

    @Override
    public MessageType getType() {
        return VIDEO_OPEN;
    }

    @Override
    public Optional<String> toMessage(Object object) {
        if (object instanceof Video video && video.isOpenAndPublic()) {
            String title = String.format("비공개 컨텐츠(%d)가 공개되었습니다.", video.getId());
            String message = String.format("사용자: %s / %d, 비디오 키: %s, 영상제목: %s, visibility: %s",
                    video.getMember().getUsername(), video.getMember().getId(), video.getVideoKey(),
                    video.getTitle(), video.getVisibility());
            return Optional.ofNullable(new SlackMessageFormat(title, message).toString());
        }
        return Optional.empty();
    }
}
