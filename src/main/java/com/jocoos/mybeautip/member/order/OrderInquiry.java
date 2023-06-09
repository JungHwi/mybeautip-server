package com.jocoos.mybeautip.member.order;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.member.Member;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "order_inquiries")
public class OrderInquiry extends MemberAuditable {

    public static Byte STATE_CANCEL_ORDER = 0;
    public static Byte STATE_REQUEST_EXCHANGE = 1;
    public static Byte STATE_REQUEST_RETURN = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @OneToOne
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;

    @Column(nullable = false)
    private Byte state;

    @Column(length = 500)
    private String reason;

    @Column(length = 500)
    private String comment;

    @Column
    private String attachments;

    @Column
    private boolean completed;

    @LastModifiedDate
    private Date modifiedAt;

    public OrderInquiry(Order order, Byte state, String reason) {
        this.order = order;
        this.state = state;
        this.reason = reason;
    }

    public OrderInquiry(Order order, Byte state, String reason, Purchase purchase) {
        this(order, state, reason);
        this.purchase = purchase;
    }

    /**
     * This method is used when OrderInquiry by Admin
     */
    public void setCreatedBy(Member createdBy) {
        this.createdBy = createdBy;
    }
}
