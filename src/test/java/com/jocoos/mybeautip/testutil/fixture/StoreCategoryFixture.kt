package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.domain.store.code.StoreCategoryStatus
import com.jocoos.mybeautip.domain.store.dto.CreateStoreCategoryRequest
import com.jocoos.mybeautip.domain.store.dto.StoreCategoryDetailDto
import com.jocoos.mybeautip.domain.store.persistence.domain.StoreCategory
import com.jocoos.mybeautip.global.code.CountryCode
import com.jocoos.mybeautip.global.dto.single.IntegerDto
import com.jocoos.mybeautip.global.dto.single.LongDto
import com.jocoos.mybeautip.support.RandomUtils

fun makeStoreCategory(
    request: CreateStoreCategoryRequest = makeStoreCategoryRequest(),
    sort: Int? = 1
): StoreCategory {
    return StoreCategory(request, RandomUtils.generateStoreCategoryCode(), sort!!)
}

fun makeStoreCategoryRequest(
    status: StoreCategoryStatus = StoreCategoryStatus.ACTIVE,
    name: String = "대표 카테고리",
    list: List<StoreCategoryDetailDto> = makeStoreCategoryDetailList()
): CreateStoreCategoryRequest {
    return CreateStoreCategoryRequest.builder()
        .status(status)
        .name(name)
        .categoryDetailList(list)
        .build()
}

fun makeDeleteCategoryRequest(
    newCategoryId: Long
): LongDto {
    return LongDto(newCategoryId)
}

fun makeChangeCategoryRequest(
    sort: Int
): IntegerDto {
    return IntegerDto(sort)
}

fun makeStoreCategoryDetailList(): List<StoreCategoryDetailDto> {
    return mutableListOf(makeStoreCategoryDetail(country = CountryCode.KR, name = "한국 카테고리"),
        makeStoreCategoryDetail(country = CountryCode.TH, name = "태국 카테고리"),
        makeStoreCategoryDetail(country = CountryCode.VN, name = "베트남 카테고리"))
}

fun makeStoreCategoryDetail(
    country: CountryCode? = CountryCode.KR,
    name: String? = "한국 카테고리"
): StoreCategoryDetailDto {
    return StoreCategoryDetailDto.builder()
        .country(country)
        .name(name)
        .build()
}