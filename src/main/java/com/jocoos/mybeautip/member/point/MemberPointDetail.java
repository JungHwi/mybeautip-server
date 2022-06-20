package com.jocoos.mybeautip.member.point;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

import static com.jocoos.mybeautip.member.point.MemberPoint.STATE_USE_POINT;

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
        } else {
            this.eventId = usePointServiceId;
        }
        setPointAndState(point, STATE_USE_POINT);
    }

    public static int getEarnedState() {
        return MemberPoint.STATE_EARNED_POINT;
    }

    public void setPointAndState(int point, int state) {
        switch (state) {
            case STATE_USE_POINT:
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
}
