package com.jocoos.mybeautip.domain.product.persistence.domain;

import com.jocoos.mybeautip.global.code.CountryCode;
import com.jocoos.mybeautip.global.config.jpa.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

import static com.jocoos.mybeautip.global.validator.StringValidator.validateMaxLengthWithoutWhiteSpace;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ProductDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    CountryCode countryCode;

    @Column
    String name;

    @Column
    int fixedPrice;

    @Column
    int purchasePrice;

    @Column
    int discountPrice;

    @Column
    BigDecimal discountRate;

    @Column
    int salePrice;

    @Column
    String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    Product product;

    public ProductDetail(CountryCode countryCode,
                         String name,
                         int fixedPrice,
                         int purchasePrice,
                         int discountPrice,
                         BigDecimal discountRate,
                         int salePrice,
                         String description) {
        this.countryCode = countryCode;
        this.fixedPrice = fixedPrice;
        this.purchasePrice = purchasePrice;
        this.discountPrice = discountPrice;
        this.discountRate = discountRate;
        this.salePrice = salePrice;
        this.description = description;
        setName(name);
    }

    private void setName(String name) {
        validateMaxLengthWithoutWhiteSpace(name, 50, "name");
        this.name = name;
    }
}
