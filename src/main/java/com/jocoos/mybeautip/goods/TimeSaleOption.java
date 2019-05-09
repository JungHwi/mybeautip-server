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
@Table(name = "time_sale_options")
public class TimeSaleOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer goodsNo;
    private Integer optionNo;
    private Integer optionPrice;
    private Long broker;

    @Column
    private Date startedAt;

    @Column
    private Date endedAt;

    @Column
    private Date deletedAt;
}
