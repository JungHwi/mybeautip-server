package com.jocoos.mybeautip.domain.company.persistence.domain;

import com.jocoos.mybeautip.domain.company.vo.CompanyClaimVo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "company_claim")
public class CompanyClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @Column
    private String customerCenterPhone;

    @Column
    private String zipcode;

    @Column
    private String address1;

    @Column
    private String address2;

    public void mapping(Company company) {
        this.company = company;
    }

    public CompanyClaim(CompanyClaimVo vo) {
        if (vo == null) {
            return;
        }

        this.customerCenterPhone = vo.customerCenterPhone();
        this.zipcode = vo.zipcode();
        this.address1 = vo.address1();
        this.address2 = vo.address2();
    }

    public void edit(CompanyClaimVo vo) {
        if (vo == null) {
            return;
        }

        this.customerCenterPhone = vo.customerCenterPhone();
        this.zipcode = vo.zipcode();
        this.address1 = vo.address1();
        this.address2 = vo.address2();
    }
}
