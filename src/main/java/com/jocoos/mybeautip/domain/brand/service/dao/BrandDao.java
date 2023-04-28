package com.jocoos.mybeautip.domain.brand.service.dao;

import com.jocoos.mybeautip.domain.brand.code.BrandStatus;
import com.jocoos.mybeautip.domain.brand.dto.BrandSearchRequest;
import com.jocoos.mybeautip.domain.brand.dto.CreateBrandRequest;
import com.jocoos.mybeautip.domain.brand.dto.EditBrandRequest;
import com.jocoos.mybeautip.domain.brand.persistence.domain.Brand;
import com.jocoos.mybeautip.domain.brand.persistence.repository.BrandRepository;
import com.jocoos.mybeautip.domain.company.persistence.domain.Company;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.support.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional(readOnly = true)
    public Brand get(long brandId) {
        return repository.findById(brandId)
                .orElseThrow(() -> new NotFoundException("Brand not found. id - " + brandId));
    }

    @Transactional
    public Brand edit(long brandId, EditBrandRequest request) {
        Brand brand = this.get(brandId);
        disableProduct(brand, request.status());
        return brand.edit(request);
    }

    @Transactional
    public void delete(long brandId) {
        Brand brand = this.get(brandId);
        validDelete(brand);
        brand.delete();
    }

    @Transactional
    public void disable(Company company) {
        List<Brand> brandList = repository.findByCompany(company);
        repository.disable(company);
    }

    private void disableProduct(Brand brand, BrandStatus status) {
        if (brand.getStatus() == BrandStatus.ACTIVE && status == BrandStatus.INACTIVE) {
            // TODO 상품 Disable
        }
    }

    private void validDelete(Brand brand) {
        // TODO 브랜드에 속한 상품이 있으면 삭제 안됨.

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
