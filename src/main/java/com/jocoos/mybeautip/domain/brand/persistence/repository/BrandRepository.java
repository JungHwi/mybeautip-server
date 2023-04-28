package com.jocoos.mybeautip.domain.brand.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.brand.persistence.domain.Brand;
import com.jocoos.mybeautip.domain.company.persistence.domain.Company;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends ExtendedQuerydslJpaRepository<Brand, Long>, BrandCustomRepository {

    boolean existsByCode(String code);

    List<Brand> findByCompany(Company company);

    @Modifying
    @Query("UPDATE Brand b SET b.status = 'INACTIVE' WHERE  b.company = ?1")
    int disable(Company company);

}
