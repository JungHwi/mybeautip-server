package com.jocoos.mybeautip.domain.company.persistence.domain;

import com.jocoos.mybeautip.domain.company.code.CompanyStatus;
import com.jocoos.mybeautip.domain.company.dto.CreateCompanyRequest;
import com.jocoos.mybeautip.domain.company.dto.EditCompanyRequest;
import com.jocoos.mybeautip.domain.company.vo.CompanyAccountVo;
import com.jocoos.mybeautip.domain.company.vo.CompanyClaimVo;
import com.jocoos.mybeautip.domain.company.vo.CompanyPermissionVo;
import com.jocoos.mybeautip.global.config.jpa.CreatedAtBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @OneToOne(mappedBy = "company", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private CompanyPermission permission;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<CompanyAccount> accounts = new ArrayList<>();

    @PostPersist
    public void postPersist() {
        accounts.forEach(account -> account.mapping(this));

        if (claim != null) {
            claim.mapping(this);
        }

        if (permission != null) {
            permission.mapping(this);
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

        if (request.permission() != null) {
            this.permission = new CompanyPermission(request.permission());
        }
    }

    public Company edit(EditCompanyRequest request) {
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

        editClaim(request.claim());
        editPermission(request.permission());
        editAccounts(request.accounts());

        return this;
    }

    public void editClaim(CompanyClaimVo claim) {
        if (this.claim == null) {
            this.claim = new CompanyClaim(claim);
        } else {
            this.claim.edit(claim);
        }
    }

    public void editPermission(CompanyPermissionVo permission) {
        if (this.permission == null) {
            this.permission = new CompanyPermission(permission);
        } else {
            this.permission.edit(permission);
        }
    }

    public void editAccounts(List<CompanyAccountVo> accounts) {
        List<CompanyAccount> editedAccounts = new ArrayList<>();

        Map<Long, CompanyAccountVo> newAccounts = accounts.stream()
                .collect(Collectors.toMap(CompanyAccountVo::id, Function.identity()));

        for (CompanyAccountVo vo : accounts) {
            if (vo.id() == null) {
                editedAccounts.add(new CompanyAccount(this, vo));
            }
        }

        for (CompanyAccount account : this.accounts) {
            if (newAccounts.containsKey(account.getId())) {
                account.edit(newAccounts.get(account.getId()));
                editedAccounts.add(account);
            }
        }

        this.accounts.clear();
        this.accounts.addAll(editedAccounts);
    }
}
