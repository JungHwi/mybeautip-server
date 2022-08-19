package com.jocoos.mybeautip.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.dto.CommunityCategoryResponse;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import lombok.*;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyCommunityResponse implements CursorInterface {

    private Long id;

    private Boolean isWin;

    private CommunityStatus status;

    private String title;

    private String contents;

    private String fileUrl;

    private Integer commentCount;

    private Integer reportCount;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private ZonedDateTime createdAt;

    private CommunityCategoryResponse category;

    @JsonIgnore
    private ZonedDateTime sortedAt;

    @Override
    @JsonIgnore
    public String getCursor() {
        return String.valueOf(this.id);
    }

    public void changeContents() {
        if (reportCount >= 3) {
            this.contents = "커뮤니티 운영방침에 따라 블라인드 되었어요.";
            if (this.category.getType() == CommunityCategoryType.BLIND) {
                this.title = "커뮤니티 운영방침에 따라 블라인드 되었어요.";
            }
        }
    }
}
