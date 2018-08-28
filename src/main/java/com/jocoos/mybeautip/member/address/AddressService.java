package com.jocoos.mybeautip.member.address;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jocoos.mybeautip.exception.AccessDeniedException;
import com.jocoos.mybeautip.exception.ConflictException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.restapi.AddressController;

@Service
public class AddressService {

  private final AddressRepository addressRepository;
  private final MemberService memberService;

  public AddressService(AddressRepository addressRepository,
                        MemberService memberService) {
    this.addressRepository = addressRepository;
    this.memberService = memberService;
  }

  @Transactional
  public Address save(Address address) {
    Long createdBy = memberService.currentMemberId();

    if (address.isBase() &&
        addressRepository.countByBaseAndCreatedByIdAndDeletedAtIsNull(true, createdBy) > 0) {
      throw new ConflictException("Already base address exists");
    }
    return addressRepository.save(address);
  }


  @Transactional
  public Address update(Long id, AddressController.UpdateAddressRequest update) {
    Long createdBy = memberService.currentMemberId();
    Address address = get(id, createdBy);

    BeanUtils.copyProperties(update, address);
    return addressRepository.save(address);
  }

  @Transactional
  public void delete(Long id) {
    Long createdBy = memberService.currentMemberId();
    Address address = get(id, createdBy);
    address.setDeletedAt(new Date());

    addressRepository.save(address);
  }

  public List<Address> getAddresses(Pageable pageable) {
    Long createdBy = memberService.currentMemberId();
    return addressRepository.findByCreatedByIdAndDeletedAtIsNull(createdBy, pageable);
  }

  public Address get(Long id, Long createdBy) {
    return addressRepository.findById(id)
       .map(a -> {
         if (a.getDeletedAt() != null) {
           throw new AccessDeniedException("Can't access an address");
         }
         if (!a.getCreatedBy().equals(createdBy)) {
           throw new AccessDeniedException("Can't access an address");
         }

         return a;
       })
       .orElseThrow(() -> new NotFoundException("address not found", "address not found"));
  }
}
