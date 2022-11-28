package com.jocoos.mybeautip.domain.member.persistence.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.member.vo.Birthday;
import com.jocoos.mybeautip.member.vo.BirthdayAttributeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dormant_member")
public class DormantMember {

    @Id
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
}
