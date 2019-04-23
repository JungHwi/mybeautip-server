package com.jocoos.mybeautip.member.address;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.DeliveryChargeArea;
import com.jocoos.mybeautip.goods.DeliveryChargeAreaRepository;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.restapi.AddressController;

@Slf4j
@Service
public class AddressService {

  private final MemberService memberService;
  private final MessageService messageService;
  private final AddressRepository addressRepository;
  private final DeliveryChargeAreaRepository deliveryChargeAreaRepository;

  private final String ADDRESS_NOT_FOUND = "address.not_found";

  public AddressService(AddressRepository addressRepository,
                        MemberService memberService,
                        MessageService messageService,
                        DeliveryChargeAreaRepository deliveryChargeAreaRepository) {
    this.addressRepository = addressRepository;
    this.memberService = memberService;
    this.messageService = messageService;
    this.deliveryChargeAreaRepository = deliveryChargeAreaRepository;
  }
  
  @Transactional
  public Address create(AddressController.CreateAddressRequest request, Member member) {
    if (request.getBase() != null && request.getBase()) {
      addressRepository.findByCreatedByIdAndDeletedAtIsNullAndBaseIsTrue(member.getId())
          .ifPresent(prevBaseAddress -> {
            prevBaseAddress.setBase(false);
            addressRepository.save(prevBaseAddress);
          });
    }
  
    Address address = new Address();
    log.debug("CreateAddressRequest: {}", request);
  
    BeanUtils.copyProperties(request, address);
    if (request.getBase() == null) {
      address.setBase(false);
    }
    
    if (addressRepository.countByCreatedByIdAndDeletedAtIsNullAndBaseIsTrue(member.getId()) == 0) {
      address.setBase(true);
    }
    
    address.setAreaShipping(calculateAreaShipping(address.getRoadAddrPart1()));
    log.debug("address: {}", address);
    return addressRepository.save(address);
  }

  @Transactional
  public Address update(Long id, AddressController.UpdateAddressRequest update, String lang) {
    return addressRepository.findByIdAndCreatedByIdAndDeletedAtIsNull(id, memberService.currentMemberId())
        .map(address -> {
          boolean originalBase = address.getBase();
          BeanUtils.copyProperties(update, address);
          if (update.getBase() == null) {
            address.setBase(originalBase);
          }
          if (addressRepository.countByCreatedByIdAndDeletedAtIsNullAndBaseIsTrue(id) == 0) {
            address.setBase(true);
          }
          address.setAreaShipping(calculateAreaShipping(address.getRoadAddrPart1()));
          return addressRepository.save(address);
        })
        .orElseThrow(() -> new NotFoundException("address_not_found", messageService.getMessage(ADDRESS_NOT_FOUND, lang)));
  }

  @Transactional
  public void delete(Long id, String lang) {
    Address address = addressRepository.findByIdAndCreatedByIdAndDeletedAtIsNull(id, memberService.currentMemberId()).orElse(null);
    if (address == null) {
      throw new NotFoundException("address_not_found", messageService.getMessage(ADDRESS_NOT_FOUND, lang));
    }
    boolean deletedAddressIsBase = address.getBase();
    address.setDeletedAt(new Date());
    addressRepository.save(address);
    
    if (deletedAddressIsBase) {
      List<Address> addressList = addressRepository.findByCreatedByIdAndDeletedAtIsNullOrderByIdDesc(id);
      long baseCount = addressRepository.countByCreatedByIdAndDeletedAtIsNullAndBaseIsTrue(id);
      if (baseCount == 0 && addressList.size() > 0) {
        Address newBaseAddress = addressList.get(0);
        newBaseAddress.setBase(true);
        addressRepository.save(newBaseAddress);
      }
    }
  }
  
  
  private int calculateAreaShipping(String roadAddress1) {
    if (roadAddress1.startsWith("제주")) {
      return deliveryChargeAreaRepository.findByArea("제주시특별자치도").map(DeliveryChargeArea::getPrice).orElse(0);
    }
    
    if (StringUtils.startsWith(roadAddress1, "인천")) {
      return StringUtils.startsWithAny(roadAddress1, "인천광역시 중구", "인천광역시 강화군", "인천광역시 옹진군")
          ? getAreaShipping(roadAddress1) : 0;
    }

    if (StringUtils.startsWith(roadAddress1, "부산")) {
      return StringUtils.startsWith(roadAddress1, "부산광역시 강서구")
          ? getAreaShipping(roadAddress1) : 0;
    }

    if (StringUtils.startsWith(roadAddress1, "경상남도")) {
      return StringUtils.startsWithAny(roadAddress1,"경상남도 사천시", "경상남도 통영시")
          ? getAreaShipping(roadAddress1) : 0;
    }

    if (StringUtils.startsWith(roadAddress1, "경상북도")) {
      return StringUtils.startsWith(roadAddress1, "경상북도 울릉군")
          ? getAreaShipping(roadAddress1) : 0;
    }

    if (StringUtils.startsWith(roadAddress1, "충청남도")) {
      return StringUtils.startsWithAny(roadAddress1, "충청남도 당진시", "충청남도 보령시", "충청남도 태안군")
          ? getAreaShipping(roadAddress1) : 0;
    }

    if (StringUtils.startsWith(roadAddress1, "전라북도")) {
      return StringUtils.startsWithAny(roadAddress1, "전라북도 군산시", "전라북도 부안군")
          ? getAreaShipping(roadAddress1) : 0;
    }

    if (StringUtils.startsWith(roadAddress1, "전라남도")) {
      return getAreaShipping(roadAddress1);
    }

    return 0;
  }
  
  private int getAreaShipping(String roadAddress1) {
    String[] part = roadAddress1.split(" ");
    if (part.length < 4) {
      return 0;
    }
    return deliveryChargeAreaRepository.findByPart1AndPart2AndPart3AndPart4(part[0], part[1], part[2], part[3])
        .map(DeliveryChargeArea::getPrice).orElse(0);
  }
}
