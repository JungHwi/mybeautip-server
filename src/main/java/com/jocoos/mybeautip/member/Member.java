package com.jocoos.mybeautip.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.member.vo.Birthday;
import com.jocoos.mybeautip.member.vo.BirthdayAttributeConverter;
import com.jocoos.mybeautip.restapi.dto.SignupRequest;
import com.jocoos.mybeautip.support.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Date;


@Data
@Builder
@AllArgsConstructor
@Slf4j
@EqualsAndHashCode(callSuper = false)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "members")
public class Member {

    public static final int LINK_FACEBOOK = 1;
    public static final int LINK_NAVER = 2;
    public static final int LINK_KAKAO = 4;
    public static final int LINK_APPLE = 8;
    public static final int CHAT_POST = 1;
    public static final int COMMENT_POST = 2;
    public static final int LIVE_POST = 4;
    public static final int MOTD_POST = 8;
    public static final int REVENUE_RETURN = 16;
    public static final int PERMISSION_ALL = (Member.CHAT_POST | Member.COMMENT_POST | Member.LIVE_POST | Member.MOTD_POST | Member.REVENUE_RETURN);
    // Changed store link from 8 to 32
    static final int LINK_STORE = 32;
    @Transient
    @JsonIgnore
    private final String defaultAvatarUrl = "https://mybeautip.s3.ap-northeast-2.amazonaws.com/avatar/img_profile_default.png";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 7)
    private String tag;

    @JsonIgnore
    @Column(nullable = false)
    private boolean visible;

    @Column(length = 50, nullable = false)
    private String username;

    @Column
    @Convert(converter = BirthdayAttributeConverter.class)
    private Birthday birthday;

    @Column(length = 200)
    private String avatarUrl;

    @Column(length = 50)
    private String email;

    @Column(length = 20)
    private String phoneNumber;

    @Column
    private int point;

    @Column(length = 200)
    private String intro;

    @Column(nullable = false)
    private int link;

    @Column
    private int permission;

    @Column(nullable = false)
    private int followerCount;

    @Column(nullable = false)
    private int followingCount;

    @Column(nullable = false)
    private int reportCount;

    @Column(nullable = false)
    private int publicVideoCount;

    @JsonIgnore
    @Column(nullable = false)
    private int totalVideoCount;

    @Column
    private int revenue;

    @Column
    private Date revenueModifiedAt;

    @Column
    private Boolean pushable;

    @Column
    @CreatedDate
    private Date createdAt;

    @Column
    @LastModifiedDate
    private Date modifiedAt;

    @Column
    private ZonedDateTime lastLoggedAt;

    @Column
    private Date deletedAt;

    public Member() {
        setTag();
    }

    public Member(SignupRequest request) {
        this.link = parseLink(request.getGrantType());
        this.username = StringUtils.isBlank(request.getUsername()) ? RandomUtils.generateUsername() : request.getUsername();
        this.email = StringUtils.isBlank(request.getEmail()) ? "" : request.getEmail();
        this.avatarUrl = StringUtils.isBlank(request.getAvatarUrl()) ? defaultAvatarUrl : request.getAvatarUrl();
        this.point = 0;
        this.visible = false;
        this.revenueModifiedAt = null;
        this.pushable = true; // default true
        this.permission = (Member.CHAT_POST | Member.COMMENT_POST | Member.LIVE_POST | Member.MOTD_POST | Member.REVENUE_RETURN);
        setTag();
    }

    public int parseLink(String grantType) {
        switch (grantType) {
            case "facebook": {
                return LINK_FACEBOOK;
            }
            case "naver": {
                return LINK_NAVER;
            }
            case "kakao": {
                return LINK_KAKAO;
            }
            case "apple": {
                return LINK_APPLE;
            }
            default: {
                throw new IllegalArgumentException("Unknown grant type");
            }
        }
    }

    public void setTag() {
        this.tag = RandomUtils.generateTag();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber.trim()
                .replace("-", "")
                .replace(" ", "");
    }
}