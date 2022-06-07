package com.jocoos.mybeautip.store;

import com.jocoos.mybeautip.audit.ModifiedDateAuditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "stores")
public class Store extends ModifiedDateAuditable {
    @Id
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column
    private String centerPhone;

    @Column
    private String imageUrl;

    @Column
    private String thumbnailUrl;

    @Column
    private String refundUrl;

    @Column
    private String asUrl;

    @Column
    private String deliveryInfo;

    @Column
    private String cancelInfo;

    @Column(nullable = false)
    private int likeCount;

    @Column(nullable = false)
    private int goodsCount;

    @Column
    private Date deletedAt;

    public Store(Integer scmNo) {
        this.setId(scmNo);
    }
}