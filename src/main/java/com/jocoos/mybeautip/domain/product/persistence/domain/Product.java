package com.jocoos.mybeautip.domain.product.persistence.domain;

import com.jocoos.mybeautip.domain.brand.persistence.domain.Brand;
import com.jocoos.mybeautip.domain.product.code.ProductStatus;
import com.jocoos.mybeautip.domain.product.vo.TemporaryProduct;
import com.jocoos.mybeautip.global.config.jpa.BaseEntity;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.jocoos.mybeautip.domain.product.code.ProductStatus.NORMAL;
import static com.jocoos.mybeautip.domain.product.code.ProductStatus.TEMP;
import static com.jocoos.mybeautip.global.validator.StringValidator.validateMaxLengthWithoutWhiteSpace;

@ParametersAreNonnullByDefault
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    String code;

    @Column
    boolean isVisible;

    @Enumerated(EnumType.STRING)
    ProductStatus status;

    @Column
    String name;

    @Column
    Long stock;

    @Column
    int weight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    Brand brand;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    List<ProductImage> images = new ArrayList<>();

    @Builder
    public Product(Long id,
                   Boolean isVisible,
                   String name,
                   Long stock,
                   int weight,
                   Brand brand,
                   List<String> imageUrls) {
        this.id = id;
        this.brand = brand;
        this.status = NORMAL;
        setStock(stock);
        setImages(imageUrls);
        setName(name);
        setIsVisible(isVisible);
        setWeight(weight);
        makeProductCode();
    }

    private void setImages(List<String> imageUrls) {
        if (!CollectionUtils.isEmpty(imageUrls) && imageUrls.size() > 5) {
            throw new BadRequestException("images size must be under 5");
        }
        replaceImages(imageUrls);
    }

    public Product(TemporaryProduct temporaryProduct) {
        this.status = TEMP;
        this.isVisible = false;
        this.id = temporaryProduct.id();
        this.name = temporaryProduct.name();
        this.stock = temporaryProduct.stock();
        this.weight = temporaryProduct.weight();
        this.brand = temporaryProduct.brand();
        replaceImages(temporaryProduct.imageUrls());
    }

    public void edit(boolean isVisible,
                     String name,
                     Long stock,
                     int weight) {
        this.stock = stock;
        setIsVisible(isVisible);
        setName(name);
        setWeight(weight);
    }

    private void replaceImages(List<String> imageUrls) {
        // TODO
    }

    private void setName(String name) {
        validateMaxLengthWithoutWhiteSpace(name, 50, "name");
        this.name = name;
    }

    private void setWeight(int weight) {
        if (weight < 0) {
            throw new BadRequestException("Weight must be greater than zero");
        }
        this.weight = weight;
    }

    private void setIsVisible(Boolean isVisible) {
        if (isVisible == null) this.isVisible = true;
        else this.isVisible = isVisible;
    }

    private void setStock(Long stock) {
        if (stock != null && stock < 0) {
            throw new BadRequestException("Stock must be greater than zero");
        }
        this.stock = stock;
    }

    private void makeProductCode() {
        // TODO  PS_yyyymmdd_000000(난수)
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", isVisible=" + isVisible +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", stock=" + stock +
                ", weight=" + weight +
                ", brand=" + brand +
                '}';
    }
}
