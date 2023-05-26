package com.jocoos.mybeautip.domain.product.dto;

import com.jocoos.mybeautip.global.dto.FileDto;

import java.util.List;

public record ProductTempRequest(Long id,
                                 String name,
                                 Long stock,
                                 Integer weight,
                                 Long brandId,
                                 List<FileDto> images) {
}
