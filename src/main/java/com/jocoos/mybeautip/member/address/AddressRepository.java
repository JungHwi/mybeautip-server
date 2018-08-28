package com.jocoos.mybeautip.member.address;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AddressRepository extends JpaRepository<Address, Long> {

  Long countByBaseAndCreatedByIdAndDeletedAtIsNull(boolean base, Long createdBy);

  Optional<Address> findByIdAndCreatedById(Long id, Long createdBy);

  List<Address> findByCreatedByIdAndDeletedAtIsNull(Long createdBy, Pageable pageable);

}
