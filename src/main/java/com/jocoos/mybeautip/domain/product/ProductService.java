package com.jocoos.mybeautip.domain.product;

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

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

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

    private TemporaryProduct getTemporaryProduct(ProductTempRequest request, List<String> imageUrls) {
        Long brandId = request.brandId();
        if (brandId != null) {
            Brand brand = brandDao.get(brandId);
            return productConverter.convert(request, brand, imageUrls);
        }
        return productConverter.convert(request, imageUrls);
    }
}
