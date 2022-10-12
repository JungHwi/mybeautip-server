package com.jocoos.mybeautip.domain.video.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.global.util.date.ZonedDateTimeUtil;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.video.Video;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class VideoResponse implements CursorInterface {
    private Long id;
    private String videoKey;
    private String type;
    private String state;
    private Boolean locked;
    private Boolean muted;
    private String visibility;
    private List<VideoCategoryResponse> category;
    private String title;
    private String content;
    private String url;
    private String originalFilename;
    private String thumbnailPath;
    private String thumbnailUrl;
    private String chatRoomId;
    private Integer duration;
    private String liveKey = "";
    private String outputType = "";
    private String data;
    private Integer watchCount;
    private Integer totalWatchCount;
    private Integer viewCount;
    private Integer heartCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer orderCount;
    private Long reportCount;
    private Integer relatedGoodsCount;
    private String relatedGoodsThumbnailUrl;
    private Long likeId;
    private Long scrapId;
    private MemberInfo owner;
    private Boolean blocked;
    private Date createdAt;
    /**
     * Real watchers count that was collected for 10 seconds
     */
    private Integer realWatchCount;

    public VideoResponse(Video video, MemberInfo owner, Long likeId, Boolean blocked) {
        BeanUtils.copyProperties(video, this);
        this.owner = owner;
        this.likeId = likeId;
        this.blocked = blocked;
        if (this.relatedGoodsCount == null) {
            this.relatedGoodsCount = 0;
        }  // FIXME: check policy
        if (this.relatedGoodsThumbnailUrl == null) {
            this.relatedGoodsThumbnailUrl = "";
        } // FIXME: check policy
    }

    @Override
    @JsonIgnore
    public String getCursor() {
        return ZonedDateTimeUtil.dateToCursorString(createdAt);
    }
}
