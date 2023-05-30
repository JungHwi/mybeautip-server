package com.jocoos.mybeautip.domain.product.service;

import com.jocoos.mybeautip.domain.product.service.dao.ProductDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductDao dao;


    @Transactional
    public void delete(List<Long> productIds) {
        // TODO 노출상품에 포함된 상품이 있다면 throw exception
        // if (displayProductDao.isDisplayed(productIds) {
        //      throw new BadRequestException(CAN_NOT_DELETE_PRODUCT);
        // }

        dao.delete(productIds);
    }
}
