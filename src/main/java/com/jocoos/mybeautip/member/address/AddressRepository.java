package com.jocoos.mybeautip.member.address;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    boolean existsByPhone(String phone);

    List<Address> findByCreatedByIdAndDeletedAtIsNullOrderByIdDesc(Long memberId);

    Long countByCreatedByIdAndDeletedAtIsNull(Long memberId);

    int countByCreatedByIdAndDeletedAtIsNullAndBaseIsTrue(Long memberId);

    Optional<Address> findByCreatedByIdAndDeletedAtIsNullAndBaseIsTrue(Long memberId);

    Optional<Address> findByIdAndCreatedByIdAndDeletedAtIsNull(Long id, Long memberId);

}
