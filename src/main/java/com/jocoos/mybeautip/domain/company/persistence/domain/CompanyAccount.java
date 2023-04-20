package com.jocoos.mybeautip.domain.company.persistence.domain;

import com.jocoos.mybeautip.domain.company.vo.CompanyAccountVo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "company_account")
public class CompanyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column
    private String bankName;

    @Column
    private String accountNumber;

    @Column
    private String ownerName;

    public void mapping(Company company) {
        this.company = company;
    }

    public CompanyAccount(CompanyAccountVo vo) {
        this.bankName = vo.bankName();
        this.accountNumber = vo.accountNumber();
        this.ownerName = vo.ownerName();
    }

    public CompanyAccount(Company company, CompanyAccountVo vo) {
        this.company = company;
        this.bankName = vo.bankName();
        this.accountNumber = vo.accountNumber();
        this.ownerName = vo.ownerName();
    }

    public void edit(CompanyAccountVo vo) {
        this.bankName = vo.bankName();
        this.accountNumber = vo.accountNumber();
        this.ownerName = vo.ownerName();
    }
}
