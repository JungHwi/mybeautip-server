package com.jocoos.mybeautip.goods;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "time_sales")
public class TimeSale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String goodsNo;
    private Integer fixedPrice;
    private Integer goodsPrice;
    private Long broker;

    @Column
    private Date startedAt;

    @Column
    private Date endedAt;

    @Column
    private Date deletedAt;

    public TimeSale(String goodsNo, Integer fixedPrice, Integer goodsPrice, Long broker, Date startedAt, Date endedAt) {
        this.goodsNo = goodsNo;
        this.fixedPrice = fixedPrice;
        this.goodsPrice = goodsPrice;
        this.broker = broker;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }
}
