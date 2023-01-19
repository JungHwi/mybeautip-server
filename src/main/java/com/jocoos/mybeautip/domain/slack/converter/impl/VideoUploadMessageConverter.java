package com.jocoos.mybeautip.domain.slack.converter.impl;

import com.jocoos.mybeautip.domain.slack.aspect.code.MessageType;
import com.jocoos.mybeautip.domain.slack.converter.MessageConverter;
import com.jocoos.mybeautip.support.DateUtils;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoGoods;
import com.jocoos.mybeautip.video.VideoGoodsRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.jocoos.mybeautip.domain.slack.aspect.code.MessageType.VIDEO_UPLOAD;

@RequiredArgsConstructor
@Component
public class VideoUploadMessageConverter implements MessageConverter {

    private final VideoGoodsRepository videoGoodsRepository;

    @Override
    public MessageType getType() {
        return VIDEO_UPLOAD;
    }

    @Override
    public Optional<String> toMessage(Object object) {
        if (object instanceof Video video) {
            String header;
            boolean isPrivateVideo = "PRIVATE".equals(video.getVisibility());
            if ("BROADCASTED".equals(video.getType())) {
                header = "라이브(" + video.getId() + ") 시작";
            } else {
                header = isPrivateVideo ?
                        String.format("#비공개 컨텐츠(%d) 업로드 완료", video.getId()) :
                        String.format("컨텐츠(%d) 업로드 완료", video.getId());
            }

            String message =
                    String.format("*%s*", header) +
                    String.format("```사용자: %s / %d%n", video.getMember().getUsername(), video.getMember().getId()) +
                    String.format("영상제목: [%s] %s, 비디오 키: %s, 공개일: %s%n", video.getCategoryNames(), video.getTitle(), video.getVideoKey(), DateUtils.toFormat(video.getStartedAt())) +
                    String.format("%s```", generateRelatedGoodsInfo(video));
            return Optional.of(message);
        }
        return Optional.empty();
    }

    private String generateRelatedGoodsInfo(Video video) {
        if (video.getRelatedGoodsCount() == null || video.getRelatedGoodsCount() == 0) {
            return "관련상품: 없음";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("관련상품: ").append(video.getRelatedGoodsCount()).append("개");
        List<VideoGoods> goodsList = videoGoodsRepository.findAllByVideoId(video.getId());
        for (VideoGoods vGoods : goodsList) {
            sb.append("\n - ").append(StringUtils.substring(vGoods.getGoods().getGoodsNm(), 0, 40));
        }
        return sb.toString();
    }
}
