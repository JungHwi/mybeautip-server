package com.jocoos.mybeautip.member.address;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
  public Address update(Long id, AddressController.UpdateAddressRequest update) {
    Optional<Address> optional = addressRepository.findByIdAndCreatedByIdAndDeletedAtIsNull(id, memberService.currentMemberId());
    if (optional.isPresent()) {
      Address address = optional.get();
      BeanUtils.copyProperties(update, address);
      return addressRepository.save(address);
    } else {
      throw new NotFoundException("address not found", "address not found");
    }
  }

  @Transactional
  public void delete(Long id) {
    Optional<Address> optional = addressRepository.findByIdAndCreatedByIdAndDeletedAtIsNull(id, memberService.currentMemberId());
    if (optional.isPresent()) {
      Address address = optional.get();
      address.setDeletedAt(new Date());
      addressRepository.save(address);
    } else {
      throw new NotFoundException("address not found", "address not found");
    }
  }
}
