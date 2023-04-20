package com.jocoos.mybeautip.domain.company.persistence.domain;

import com.jocoos.mybeautip.domain.company.code.ProcessPermission;
import com.jocoos.mybeautip.domain.company.vo.CompanyPermissionVo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "company_permission")
public class CompanyPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @Enumerated(EnumType.STRING)
    private ProcessPermission createProduct;

    @Enumerated(EnumType.STRING)
    private ProcessPermission updateProduct;

    @Enumerated(EnumType.STRING)
    private ProcessPermission deleteProduct;

    public void mapping(Company company) {
        this.company = company;
    }

    public CompanyPermission(CompanyPermissionVo vo) {
        if (vo == null) {
            return;
        }

        this.createProduct = vo.createProduct();
        this.updateProduct = vo.updateProduct();
        this.deleteProduct = vo.deleteProduct();
    }

    public void edit(CompanyPermissionVo vo) {
        if (vo == null) {
            return;
        }

        this.createProduct = vo.createProduct();
        this.updateProduct = vo.updateProduct();
        this.deleteProduct = vo.deleteProduct();
    }
}
