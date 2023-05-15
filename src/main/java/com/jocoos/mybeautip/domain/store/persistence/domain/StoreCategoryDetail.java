package com.jocoos.mybeautip.domain.store.persistence.domain;

import com.jocoos.mybeautip.domain.store.dto.StoreCategoryDetailDto;
import com.jocoos.mybeautip.global.code.CountryCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "store_category_detail")
public class StoreCategoryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private StoreCategory storeCategory;

    @Enumerated(EnumType.STRING)
    private CountryCode country;

    @Column
    private String name;

    public void mapping(StoreCategory storeCategory) {
        this.storeCategory = storeCategory;
    }

    public StoreCategoryDetail(CountryCode country, String name) {
        this.country = country;
        this.name = name;
    }

    public static StoreCategoryDetail of(StoreCategoryDetailDto detailDto) {
        return new StoreCategoryDetail(detailDto.country(), detailDto.name());
    }
}
