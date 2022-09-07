package com.jocoos.mybeautip.member.point;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.domain.point.code.ActivityPointType;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

import static com.jocoos.mybeautip.member.point.MemberPoint.*;


@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@Entity
@Table(name = "member_point_details")
public class MemberPointDetail extends CreatedDateAuditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private int state;

    @Column(nullable = false)
    private int point;

    @Column
    private Long parentId;

    @Column(nullable = false)
    private Long memberPointId;

    @Column
    private Long orderId;

    @Column
    private Long eventId;

    @Column
    @Enumerated(EnumType.STRING)
    private ActivityPointType activityType;
    @Column
    private Date expiryAt;

    public MemberPointDetail(MemberPoint memberPoint, int state) {
        this(memberPoint, state, memberPoint.getPoint(), memberPoint.getId());
    }

    public MemberPointDetail(MemberPoint memberPoint, int state, int point, long parentId) {
        this(memberPoint, state, point, parentId, memberPoint.getId(), memberPoint.getExpiryAt());
    }

    public MemberPointDetail(MemberPoint memberPoint, int state, int point, long parentId, long memberPointId, Date expiryAt) {
        this.memberId = memberPoint.getMember().getId();
        setPointAndState(point, state);
        this.parentId = parentId;
        this.memberPointId = memberPointId;
        if (memberPoint.getOrder() != null) {
            this.orderId = memberPoint.getOrder().getId();
        } else {
            this.orderId = null;
        }
        this.expiryAt = expiryAt;
    }

    public MemberPointDetail(MemberPointDetail parent, int state, long memberPointId, int point, UsePointService usePointService, Long usePointServiceId) {
        this.memberId = parent.getMemberId();
        setPointAndState(point, state);
        this.parentId = parent.getParentId();
        this.memberPointId = memberPointId;
        this.expiryAt = parent.getExpiryAt();
        if (usePointService == UsePointService.ORDER) {
            this.orderId = usePointServiceId;
        } else {
            this.eventId = usePointServiceId;
        }

    }

    public MemberPointDetail(MemberPoint memberPoint, long memberPointId, int point, UsePointService userPointService, Long usePointServiceId) {
        this.memberId = memberPoint.getMember().getId();
        this.parentId = memberPoint.getId();
        this.memberPointId = memberPointId;
        this.expiryAt = memberPoint.getExpiryAt();
        if (userPointService == UsePointService.ORDER) {
            this.orderId = usePointServiceId;
        } else if (userPointService == UsePointService.EVENT) {
            this.eventId = usePointServiceId;
        } else {
            this.activityType = ActivityPointType.getActivityPointType(Math.toIntExact(usePointServiceId));
        }
        setPointAndState(point, STATE_USE_POINT);
    }

    public void setCommonData(MemberPoint memberPoint) {
        this.memberId = memberPoint.getMember().getId();
        this.memberPointId = memberPoint.getId();

        setExpiryAtIfNull(memberPoint);
        setUseService(memberPoint);
    }

    private void setExpiryAtIfNull(MemberPoint memberPoint) {
        if (this.expiryAt == null) {
            this.expiryAt = memberPoint.getExpiryAt();
        }
    }

    private void setUseService(MemberPoint memberPoint) {
        if (memberPoint.getOrder() != null) {
            this.orderId = memberPoint.getOrder().getId();
        } else {
            this.eventId = memberPoint.getEventId();
            this.activityType = memberPoint.getActivityType();
        }
    }

    public static MemberPointDetail slice(long parentId, int point, int state, Date expiryAt) {
        return MemberPointDetail.builder()
                .parentId(parentId)
                .point(point)
                .state(state)
                .expiryAt(expiryAt)
                .build();
    }


    public static MemberPointDetail slice(long parentId, int point, int state) {
       return MemberPointDetail.builder()
               .parentId(parentId)
               .point(point)
               .state(state)
               .build();
    }

    public static MemberPointDetail slice(int point, int state) {
        return MemberPointDetail.builder()
                .point(point)
                .state(state)
                .build();
    }

    public void setPointAndState(int point, int state) {
        switch (state) {
            case STATE_USE_POINT:
            case STATE_RETRIEVE_POINT:
            case MemberPoint.STATE_EXPIRED_POINT:
                this.state = STATE_USE_POINT;
                this.point = -point;
                break;
            case MemberPoint.STATE_EARNED_POINT:
            case MemberPoint.STATE_REFUNDED_POINT:
            default:
                this.state = MemberPoint.STATE_EARNED_POINT;
                this.point = point;
                break;
        }
    }

    public void setCreatedAt(Date date) {
        super.createdAt = date;
    }

    public void underZeroPointAllAddedUp() {
        this.parentId = this.getMemberPointId();
    }
}
