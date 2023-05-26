package com.jocoos.mybeautip.domain.product.persistence.domain;

import com.jocoos.mybeautip.global.config.jpa.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.jocoos.mybeautip.global.util.FileUtil.getFileName;
import static com.jocoos.mybeautip.global.validator.ObjectValidator.requireNonNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ProductImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    String image;

    @Column
    int sort;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    Product product;

    public ProductImage(String imageUrl,
                        int sort,
                        Product product) {
        requireNonNull(imageUrl, "imageUrl");
        requireNonNull(product, "product");
        this.image = getFileName(imageUrl);
        this.sort = sort;
        this.product = product;
    }
}
