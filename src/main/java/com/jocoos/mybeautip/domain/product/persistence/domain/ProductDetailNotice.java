package com.jocoos.mybeautip.domain.product.persistence.domain;

import com.jocoos.mybeautip.domain.product.code.ProductDetailNoticeType;
import com.jocoos.mybeautip.global.config.jpa.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ProductDetailNotice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    ProductDetailNoticeType noticeType;

    @Column
    String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_detail_id")
    ProductDetail productDetail;
}
