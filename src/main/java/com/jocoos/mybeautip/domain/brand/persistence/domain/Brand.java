package com.jocoos.mybeautip.domain.brand.persistence.domain;

import com.jocoos.mybeautip.domain.brand.code.BrandStatus;
import com.jocoos.mybeautip.domain.brand.dto.CreateBrandRequest;
import com.jocoos.mybeautip.domain.brand.dto.EditBrandRequest;
import com.jocoos.mybeautip.domain.company.code.CompanyStatus;
import com.jocoos.mybeautip.domain.company.persistence.domain.Company;
import com.jocoos.mybeautip.global.config.jpa.CreatedAtBaseEntity;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "brand")
public class Brand extends CreatedAtBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(updatable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    private BrandStatus status;

    @Column
    private String name;

    @Column
    private String description;

    public Brand(CreateBrandRequest request, Company company) {
        this.company = company;
        this.status = request.getStatus();
        this.name = request.getName();
        this.description = request.getDescription();
        this.code = request.getCode();
    }

    public Brand edit(EditBrandRequest request) {
        patchStatus(request.status());
        this.name = request.name();
        this.description = request.description();

        return this;
    }


    public void delete() {
        patchStatus(BrandStatus.DELETE);
    }

    private void patchStatus(BrandStatus newStatus) {
        switch (newStatus) {
            case ACTIVE -> {
                if (this.company.getStatus() == CompanyStatus.ACTIVE) {
                    this.status = newStatus;
                } else {
                    throw new BadRequestException("공급사가 활성상태가 아닙니다.");
                }
            }
            case INACTIVE, DELETE -> this.status = newStatus;
        }
    }
}