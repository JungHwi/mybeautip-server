package com.jocoos.mybeautip.domain.brand.persistence.domain;

import com.jocoos.mybeautip.domain.brand.code.BrandStatus;
import com.jocoos.mybeautip.domain.brand.dto.CreateBrandRequest;
import com.jocoos.mybeautip.domain.company.persistence.domain.Company;
import com.jocoos.mybeautip.global.config.jpa.CreatedAtBaseEntity;
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

}