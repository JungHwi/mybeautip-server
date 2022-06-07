package com.jocoos.mybeautip.goods;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "delivery_charge_details")
public class DeliveryChargeDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer deliveryChargeId;

    @Column(nullable = false)
    private Integer unitStart;

    @Column(nullable = false)
    private Integer unitEnd;

    @Column(nullable = false)
    private Integer price;
}
