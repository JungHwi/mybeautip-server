package com.jocoos.mybeautip.member.point;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.order.Order;
import lombok.*;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.Locale;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "member_points")
@Builder
@AllArgsConstructor
public class MemberPoint extends CreatedDateAuditable {
    public static final int STATE_WILL_BE_EARNED = 0;
    public static final int STATE_EARNED_POINT = 1;
    public static final int STATE_USE_POINT = 2;
    public static final int STATE_EXPIRED_POINT = 3;
    public static final int STATE_REFUNDED_POINT = 8;
    public static final int STATE_PRESENT_POINT = 9;
    private static DecimalFormat POINT_FORMAT = new DecimalFormat("#,###", new DecimalFormatSymbols(Locale.KOREA));
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int state;

    @Column(nullable = false)
    private int point;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column
    private Long eventId;

    @Column
    private Date earnedAt;

    /**
     * Expiry At is Expiration Date of Present point to bj.
     * Maybe Expiry At is 1 month or something.
     */
    @Column
    private Date expiryAt;

    /**
     * Don't confuse this.
     * This is generally null.
     * This has date if point is expired like deleted at.
     */
    @Column
    private Date expiredAt;

    @Column
    private boolean remind;

    public MemberPoint(Member member, Order order, int point) {
        this(member, order, point, STATE_WILL_BE_EARNED);
    }

    public MemberPoint(Member member, Order order, int point, int state) {
        this.member = member;
        this.order = order;
        this.point = point;
        this.state = state;
    }

    public MemberPoint(Member member, Order order, int point, int state, Date expiryAt) {
        this(member, order, point, state);
        this.earnedAt = new Date();
        this.expiryAt = expiryAt;
    }

    public MemberPoint(Member member, Order order, int point, int state, Date expiryAt, boolean remind) {
        this(member, order, point, state);
        this.earnedAt = new Date();
        this.expiryAt = expiryAt;
        this.remind = remind;
    }

    public void setCreatedAt(Date date) {
        super.createdAt = date;
    }

    public String getFormattedPoint() {
        return POINT_FORMAT.format(this.point);
    }
}
