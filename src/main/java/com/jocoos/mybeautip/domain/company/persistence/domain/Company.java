package com.jocoos.mybeautip.domain.company.persistence.domain;

import com.jocoos.mybeautip.domain.company.code.CompanyStatus;
import com.jocoos.mybeautip.domain.company.dto.CreateCompanyRequest;
import com.jocoos.mybeautip.global.config.jpa.CreatedAtBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Company extends CreatedAtBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private CompanyStatus status;

    @Column
    private float salesFee;

    @Column
    private float shippingFee;

    @Column
    private String businessName;

    @Column
    private String businessNumber;

    @Column
    private String representativeName;

    @Column
    private String email;

    @Column
    private String phoneNumber;

    @Column
    private String businessType;

    @Column
    private String businessItem;

    @Column
    private String zipcode;

    @Column
    private String address1;

    @Column
    private String address2;

    @OneToOne(mappedBy = "company", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private CompanyClaim claim;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<CompanyAccount> accounts = new ArrayList<>();

    @PostPersist
    public void postPersist() {
        accounts.forEach(account -> account.mapping(this));

        if (claim != null) {
            claim.mapping(this);
        }
    }

    public Company(CreateCompanyRequest request) {
        this.name = request.name();
        this.status = request.status();
        this.salesFee = request.salesFee();
        this.shippingFee = request.shippingFee();
        this.businessName = request.businessName();
        this.businessNumber = request.businessNumber();
        this.representativeName = request.representativeName();
        this.email = request.email();
        this.phoneNumber = request.phoneNumber();
        this.businessType = request.businessType();
        this.businessItem = request.businessItem();
        this.zipcode = request.zipcode();
        this.address1 = request.address1();
        this.address2 = request.address2();

        if (request.accounts() != null) {
            request.accounts()
                    .forEach(account -> accounts.add(new CompanyAccount(account)));
        }

        if (request.claim() != null) {
            this.claim = new CompanyClaim(request.claim());
        }
    }
}
