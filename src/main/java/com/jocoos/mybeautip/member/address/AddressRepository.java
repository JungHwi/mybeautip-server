package com.jocoos.mybeautip.member.address;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

  List<Address> findByCreatedByIdAndDeletedAtIsNullOrderByIdDesc(Long memberId);

  Long countByCreatedByIdAndDeletedAtIsNull(Long memberId);

  Optional<Address> findByCreatedByIdAndDeletedAtIsNullAndBaseIsTrue(Long memberId);
  
  Optional<Address> findByIdAndCreatedByIdAndDeletedAtIsNull(Long id, Long memberId);

}
