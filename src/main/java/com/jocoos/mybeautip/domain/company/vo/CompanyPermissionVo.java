package com.jocoos.mybeautip.domain.company.vo;

import com.jocoos.mybeautip.domain.company.code.ProcessPermission;
import lombok.Builder;

@Builder
public record CompanyPermissionVo(ProcessPermission createProduct,
                                  ProcessPermission updateProduct,
                                  ProcessPermission deleteProduct) {

}
