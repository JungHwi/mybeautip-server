package com.jocoos.mybeautip.devices;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.member.Member;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "devices")
public class Device extends MemberAuditable {

    public static final String OS_NAME_IOS = "ios";
    public static final String OS_NAME_ANDROID = "android";

    @Id
    private String id;

    @Column(nullable = false)
    private String arn;

    @Column(nullable = false)
    private String os;

    @Column(nullable = false)
    private String osVersion;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    private String timezone;

    @Column(nullable = false)
    private String appVersion;

    @Column(nullable = false)
    private boolean valid = true; // default value

    @Column(nullable = false)
    private boolean pushable;

    @Column
    @LastModifiedDate
    private Date modifiedAt;

    public void setCreatedBy(Member createdBy) {
        this.createdBy = createdBy;
    }
}
