package com.jocoos.mybeautip.domain.store.persistence.domain;

import com.jocoos.mybeautip.domain.store.code.StoreCategoryStatus;
import com.jocoos.mybeautip.domain.store.dto.CreateStoreCategoryRequest;
import com.jocoos.mybeautip.domain.store.dto.StoreCategoryDetailDto;
import com.jocoos.mybeautip.global.config.jpa.CreatedAtBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;


@Getter
@NoArgsConstructor
@Entity
@Table(name = "store_category")
public class StoreCategory extends CreatedAtBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @Column
    private String code;

    @Column
    private int sort;

    @Enumerated(EnumType.STRING)
    private StoreCategoryStatus status;

    @Column
    private String name;

    @OneToMany(mappedBy = "storeCategory", fetch = LAZY, cascade = ALL, orphanRemoval = true)
    private List<StoreCategoryDetail> categoryDetailList = new ArrayList<>();

    @PostPersist
    public void postPersist() {
        if (categoryDetailList != null) {
            categoryDetailList.forEach(detail -> detail.mapping(this));
        }
    }

    public StoreCategory(CreateStoreCategoryRequest request, String code, int sort) {
        this.code = code;
        this.sort = sort;
        this.status = request.status();
        this.name = request.name();

        for (StoreCategoryDetailDto dto : request.categoryDetailList()) {
            StoreCategoryDetail detail = StoreCategoryDetail.of(dto);
            this.categoryDetailList.add(detail);
        }
    }
}
