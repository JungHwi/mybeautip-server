package com.jocoos.mybeautip.domain.brand.dto;

import com.jocoos.mybeautip.domain.brand.code.BrandStatus;
import com.jocoos.mybeautip.global.vo.SearchRequest;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;

@Getter
@SuperBuilder
public class BrandSearchRequest extends SearchRequest {
    private final BrandStatus status;

    public BrandSearchRequest(BrandStatus status, Pageable pageable) {
        super(pageable);
        this.status = status;
    }
}
