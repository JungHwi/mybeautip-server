package com.jocoos.mybeautip.domain.product.persistence.domain;

import com.jocoos.mybeautip.domain.product.code.ProductImageType;
import com.jocoos.mybeautip.global.config.jpa.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ProductDetailImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    String image;

    @Enumerated(EnumType.STRING)
    ProductImageType type;

    @Column
    int sort;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_detail_id")
    ProductDetail productDetail;
}
