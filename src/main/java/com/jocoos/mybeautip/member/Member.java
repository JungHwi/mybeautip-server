package com.jocoos.mybeautip.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.domain.member.code.GrantType;
import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.code.Role;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.vo.Birthday;
import com.jocoos.mybeautip.member.vo.BirthdayAttributeConverter;
import com.jocoos.mybeautip.restapi.dto.SignupRequest;
import com.jocoos.mybeautip.support.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Date;

import static com.jocoos.mybeautip.domain.member.code.GrantType.*;
import static com.jocoos.mybeautip.domain.member.code.Role.ADMIN;
import static com.jocoos.mybeautip.domain.member.code.Role.USER;
import static com.jocoos.mybeautip.global.code.UrlDirectory.AVATAR;
import static com.jocoos.mybeautip.global.constant.MybeautipConstant.*;
import static com.jocoos.mybeautip.global.util.ImageFileConvertUtil.toFileName;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;
import static com.jocoos.mybeautip.global.util.date.ZonedDateTimeUtil.toUTCZoned;


@Data
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 7)
    private String tag;

    @Column
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @JsonIgnore
    @Column(nullable = false)
    private boolean visible;

    @Column(length = 50, nullable = false)
    private String username;

    @Column
    @Convert(converter = BirthdayAttributeConverter.class)
    private Birthday birthday;

    // 디비에 이미지 파일명만 저장하기로 변경, 컬럼명은 일단 그대로둠
    @Column(name = "avatar_url", length = 200)
    private String avatarFilename;

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
        this.status = MemberStatus.ACTIVE;
        this.pushable = true;
        setTag();
    }

    public Member(SignupRequest request) {
        this.status = MemberStatus.ACTIVE;
        this.link = parseLink(request.getGrantType());
        this.email = StringUtils.isBlank(request.getEmail()) ? "" : request.getEmail();
        this.point = 0;
        this.visible = false;
        this.revenueModifiedAt = null;
        this.pushable = true; // default true
        this.permission = (Member.CHAT_POST | Member.COMMENT_POST | Member.REVENUE_RETURN);

        setTag();
        setAvatarFilenameFromUrl(request.getAvatarUrl());
    }

    public void setLink(int link) {
        this.link = link;
    }

    public void setLink(String grantType) {
        this.link = parseLink(grantType);
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

    public GrantType getGrantType() {
        return switch (link) {
            case LINK_FACEBOOK -> FACEBOOK;
            case LINK_NAVER -> NAVER;
            case LINK_KAKAO -> KAKAO;
            case LINK_APPLE -> APPLE;
            default -> null;
        };
    }

    public void setTag() {
        this.tag = RandomUtils.generateTag();
    }

    public void setPhoneNumber(String phoneNumber) {
        if (StringUtils.isEmpty(phoneNumber)) {
            this.phoneNumber = null;
        } else {
            this.phoneNumber = phoneNumber.trim()
                    .replace("-", "")
                    .replace(" ", "");
        }
    }

    public Member usePoint(int point) {
        if (this.point < point) {
            throw new BadRequestException("Member has " + this.point + " point. This event need " + point + " point.");
        }

        this.point = this.point - point;
        return this;
    }

    public Member retrievePoint(int point) {
        this.point -= point;
        return this;
    }

    public Member earnPoint(int point) {
        this.point = this.point + point;
        return this;
    }

    public void setAvatarFilenameFromUrl(String imgUrl) {
        if (StringUtils.isBlank(imgUrl)) {
            this.avatarFilename = DEFAULT_AVATAR_FILE_NAME;
        } else{
            this.avatarFilename = toFileName(imgUrl);
        }
    }

    public String getAvatarUrl() {
        if (StringUtils.isBlank(this.avatarFilename)) {
            return "";
        } else if (isUrl()) {
            return this.avatarFilename;
        } else {
            return toUrl(this.avatarFilename, AVATAR);
        }
    }

    private boolean isUrl() {
        return this.avatarFilename.startsWith(HTTP_PREFIX);
    }

    public boolean isAvatarUrlSame(String originalAvatar) {
        return getAvatarUrl().equals(originalAvatar);
    }

    public Integer getAgeGroup() {
        if (birthday == null) {
            return null;
        }
        return birthday.getAgeGroupByTen();
    }

    public Member changeStatus(MemberStatus status) {
        switch (status) {
            case WITHDRAWAL -> withdrawal();
            case DORMANT -> dormant();
            case SUSPENDED -> suspend();
            case EXILE -> exile();
        }

        return this;
    }

    public ZonedDateTime getCreatedAtZoned() {
        return toUTCZoned(createdAt);
    }

    public ZonedDateTime getModifiedAtZoned() {
        return toUTCZoned(modifiedAt);
    }

    private void dormant() {
        this.status = MemberStatus.DORMANT;
        this.username = DISABLE_USERNAME;
        this.avatarFilename = DEFAULT_AVATAR_FILE_NAME;
        this.tag = StringUtils.EMPTY;
        this.visible = false;
        this.email = StringUtils.EMPTY;
        this.phoneNumber = StringUtils.EMPTY;
        this.birthday = null;
        this.permission = NumberUtils.INTEGER_ZERO;
        this.intro = StringUtils.EMPTY;
        this.point = NumberUtils.INTEGER_ZERO;
        this.followerCount = NumberUtils.INTEGER_ZERO;
        this.followingCount = NumberUtils.INTEGER_ZERO;
        this.reportCount = NumberUtils.INTEGER_ZERO;
        this.publicVideoCount = NumberUtils.INTEGER_ZERO;
        this.totalVideoCount = NumberUtils.INTEGER_ZERO;
        this.revenue = NumberUtils.INTEGER_ZERO;
        this.revenueModifiedAt = null;
        this.pushable = false;
    }

    private void suspend() {
        this.status = MemberStatus.SUSPENDED;
    }

    private void exile() {
        this.status = MemberStatus.EXILE;
    }

    private void withdrawal() {
        this.status = MemberStatus.WITHDRAWAL;
        this.visible = false;
        this.avatarFilename = DEFAULT_AVATAR_FILE_NAME;
        this.followingCount = 0;
        this.followerCount = 0;
        this.publicVideoCount = 0;
        this.totalVideoCount = 0;
        this.deletedAt = new Date();
    }
    public Role getRole() {
        if (link == 0) {
            return ADMIN;
        }
        return USER;
    }
}
