package com.jocoos.mybeautip.domain.brand.service.dao;

import com.jocoos.mybeautip.domain.brand.dto.BrandSearchRequest;
import com.jocoos.mybeautip.domain.brand.dto.CreateBrandRequest;
import com.jocoos.mybeautip.domain.brand.persistence.domain.Brand;
import com.jocoos.mybeautip.domain.brand.persistence.repository.BrandRepository;
import com.jocoos.mybeautip.domain.company.persistence.domain.Company;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.support.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BrandDao {

    private final BrandRepository repository;

    @Transactional
    public Brand create(CreateBrandRequest request, Company company) {
        request.setCode(generateCode());
        Brand brand = new Brand(request, company);

        return repository.save(brand);
    }

    @Transactional(readOnly = true)
    public Page<Brand> search(BrandSearchRequest request) {
        return repository.search(request);
    }

    private String generateCode() {
        for (int index = 0; index < 5; index++) {
            String code = RandomUtils.generateBrandCode();
            if (!repository.existsByCode(code)) {
                return code;
            }
        }
        throw new BadRequestException("Brand Code 생성에 실패하였습니다.");
    }
}
