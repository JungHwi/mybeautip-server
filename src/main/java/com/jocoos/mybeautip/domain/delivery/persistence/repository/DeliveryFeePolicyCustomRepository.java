package com.jocoos.mybeautip.domain.delivery.persistence.repository;

import com.jocoos.mybeautip.domain.delivery.dto.DeliveryFeePolicySearchRequest;
import com.jocoos.mybeautip.domain.delivery.vo.DeliveryFeePolicySearchResult;
import org.springframework.data.domain.Page;

public interface DeliveryFeePolicyCustomRepository {

    Page<DeliveryFeePolicySearchResult> search(DeliveryFeePolicySearchRequest request);
}
