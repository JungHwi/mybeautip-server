package com.jocoos.mybeautip.domain.vod.dto;

import org.springframework.data.domain.Sort.Direction;

public record SortFilter(String sortField, Direction order) {
}
