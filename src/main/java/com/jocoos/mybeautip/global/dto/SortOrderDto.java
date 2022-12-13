package com.jocoos.mybeautip.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SortOrderDto {
    private List<Long> sortedIds;
}
