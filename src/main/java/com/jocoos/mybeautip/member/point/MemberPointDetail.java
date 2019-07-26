package com.jocoos.mybeautip.member.point;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
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
    private Date expiryAt;

    public MemberPointDetail(MemberPoint memberPoint, int state) {
        this(memberPoint, state, memberPoint.getPoint(), memberPoint.getId());
    }

    public MemberPointDetail(MemberPoint memberPoint, int state, int point, long parentId) {
        this(memberPoint, state, point, parentId, memberPoint.getId());
    }

    public MemberPointDetail(MemberPoint memberPoint, int state, int point, long parentId, long memberPointId) {
        this.memberId = memberPoint.getMember().getId();
        setPointAndState(point, state);
        this.parentId = parentId;
        this.memberPointId = memberPointId;
        if (memberPoint.getOrder() != null) {
            this.orderId = memberPoint.getOrder().getId();
        } else {
            this.orderId = null;
        }
        this.expiryAt = memberPoint.getExpiryAt();
    }

    public MemberPointDetail(MemberPointDetail parent, int state, long memberPointId, int point, Long orderId) {
        this.memberId = parent.getMemberId();
        setPointAndState(point, state);
        this.parentId = parent.getParentId();
        this.memberPointId = memberPointId;
        this.expiryAt = parent.getExpiryAt();
        this.orderId = orderId;
    }

    private void setPointAndState(int point, int state) {
        switch (state) {
            case MemberPoint.STATE_USE_POINT:
            case MemberPoint.STATE_EXPIRED_POINT:
                this.state = MemberPoint.STATE_USE_POINT;
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

    public static int getEarnedState() {
        return MemberPoint.STATE_EARNED_POINT;
    }

    public void setCreatedAt(Date date) {
        super.createdAt = date;
    }
}
