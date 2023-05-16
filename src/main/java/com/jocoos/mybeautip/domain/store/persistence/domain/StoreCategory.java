package com.jocoos.mybeautip.domain.store.persistence.domain;

import com.jocoos.mybeautip.domain.store.code.StoreCategoryStatus;
import com.jocoos.mybeautip.domain.store.dto.CreateStoreCategoryRequest;
import com.jocoos.mybeautip.domain.store.dto.EditStoreCategoryRequest;
import com.jocoos.mybeautip.domain.store.dto.StoreCategoryDetailDto;
import com.jocoos.mybeautip.global.code.CountryCode;
import com.jocoos.mybeautip.global.config.jpa.CreatedAtBaseEntity;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.tika.utils.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        validCreate(request);
        this.code = code;
        this.sort = sort;
        this.status = request.status();
        this.name = request.name();

        for (StoreCategoryDetailDto dto : request.categoryDetailList()) {
            StoreCategoryDetail detail = StoreCategoryDetail.of(dto);
            this.categoryDetailList.add(detail);
        }
    }

    public void edit(EditStoreCategoryRequest request) {
        validEdit(request);
        this.name = request.name();
        this.status = request.status();

        for (StoreCategoryDetailDto dto : request.categoryDetailList()) {
            categoryDetailList.stream()
                    .filter(detail -> detail.getCountry() == dto.country())
                    .forEach(detail -> detail.edit(dto.name()));
        }
    }

    public void delete() {
        this.status = StoreCategoryStatus.DELETE;
    }

    public void changeSort(int sort) {
        this.sort = sort;
    }

    private void validCreate(CreateStoreCategoryRequest request) {
        validName(request.name());
        validDetail(request.categoryDetailList());
    }

    private void validEdit(EditStoreCategoryRequest request) {
        validName(request.name());
        validDetail(request.categoryDetailList());
    }

    private void validName(String name) {
        if (StringUtils.isEmpty(name) || name.length() < 2 || name.length() > 30) {
            throw new BadRequestException("Category names must be at least 2 and no more than 30 characters long.");
        }
    }

    private void validDetail(List<StoreCategoryDetailDto> details) {
        Set<CountryCode> countryCodes = details.stream()
                .map(StoreCategoryDetailDto::country)
                .collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(countryCodes) || countryCodes.size() != 3) {
            throw new BadRequestException("카테고리의 국가별 정보가 잘못되었습니다.");
        }
    }
}
