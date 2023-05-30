package com.jocoos.mybeautip.domain.product.api.admin;

import com.jocoos.mybeautip.domain.product.dto.DeleteProductRequest;
import com.jocoos.mybeautip.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/product")
public class AdminProductController {

    private final ProductService service;

    @DeleteMapping
    public ResponseEntity delete(@RequestBody DeleteProductRequest request) {
        service.delete(request.productIds());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
