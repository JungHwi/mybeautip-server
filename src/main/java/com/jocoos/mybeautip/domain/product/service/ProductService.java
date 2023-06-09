package com.jocoos.mybeautip.domain.product.service;

import com.jocoos.mybeautip.domain.brand.persistence.domain.Brand;
import com.jocoos.mybeautip.domain.brand.service.dao.BrandDao;
import com.jocoos.mybeautip.domain.product.converter.ProductConverter;
import com.jocoos.mybeautip.domain.product.dto.ProductCreateRequest;
import com.jocoos.mybeautip.domain.product.dto.ProductTempRequest;
import com.jocoos.mybeautip.domain.product.persistence.domain.Product;
import com.jocoos.mybeautip.domain.product.service.dao.ProductDao;
import com.jocoos.mybeautip.domain.product.vo.TemporaryProduct;
import com.jocoos.mybeautip.global.dto.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductDao dao;
    private final BrandDao brandDao;
    private final ProductDao productDao;
    private final ProductConverter productConverter;

    @Transactional
    public Product tempSave(ProductTempRequest request) {
        List<String> imageUrls = request.images().stream()
                .map(FileDto::getUrl)
                .toList();
        TemporaryProduct temporaryProduct = getTemporaryProduct(request, imageUrls);
        Product product = new Product(temporaryProduct);
        return productDao.save(product);
    }

    @Transactional
    public Product save(ProductCreateRequest request) {
        Product product = productConverter.convert(request);
        return productDao.save(product);
    }

    @Transactional
    public void delete(Collection<Long> productIds) {
        validDelete(productIds);
        productDao.delete(productIds);
    }

    private void validDelete(Collection<Long> productIds) {
        // TODO Store, PreOrder 에 사용중인 Product 가 있는지 체크 필요.
        /* if (storeService.existProducts(productIds) || preOrderService.existProduct(ProductIds)) {
              throw new BadRequestException(PRODUCT_IN_USE);
        } */
    }

    private TemporaryProduct getTemporaryProduct(ProductTempRequest request, List<String> imageUrls) {
        Long brandId = request.brandId();
        if (brandId != null) {
            Brand brand = brandDao.get(brandId);
            return productConverter.convert(request, brand, imageUrls);
        }
        return productConverter.convert(request, imageUrls);
    }
}
