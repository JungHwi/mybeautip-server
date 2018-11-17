package com.jocoos.mybeautip.member.address;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

import com.jocoos.mybeautip.notification.MessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.DeliveryChargeArea;
import com.jocoos.mybeautip.goods.DeliveryChargeAreaRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.restapi.AddressController;
import org.springframework.web.bind.annotation.RequestHeader;

@Service
public class AddressService {

  private final MemberService memberService;
  private final MessageService messageService;
  private final AddressRepository addressRepository;
  private final DeliveryChargeAreaRepository deliveryChargeAreaRepository;

  public AddressService(AddressRepository addressRepository,
                        MemberService memberService,
                        MessageService messageService,
                        DeliveryChargeAreaRepository deliveryChargeAreaRepository) {
    this.addressRepository = addressRepository;
    this.memberService = memberService;
    this.messageService = messageService;
    this.deliveryChargeAreaRepository = deliveryChargeAreaRepository;
  }

  public Address insert(Address address) {
    address.setAreaShipping(calculateAreaShipping(address.getRoadAddrPart1()));
    return addressRepository.save(address);
  }

  @Transactional
  public Address update(Long id, AddressController.UpdateAddressRequest update, String lang) {
    Optional<Address> optional = addressRepository.findByIdAndCreatedByIdAndDeletedAtIsNull(id, memberService.currentMemberId());
    if (optional.isPresent()) {
      Address address = optional.get();
      BeanUtils.copyProperties(update, address);
      address.setAreaShipping(calculateAreaShipping(address.getRoadAddrPart1()));
      return addressRepository.save(address);
    } else {
      throw new NotFoundException("address_not_found", messageService.getAddressNotFoundMessage(lang));
    }
  }

  @Transactional
  public void delete(Long id, String lang) {
    Optional<Address> optional = addressRepository.findByIdAndCreatedByIdAndDeletedAtIsNull(id, memberService.currentMemberId());
    if (optional.isPresent()) {
      Address address = optional.get();
      address.setDeletedAt(new Date());
      addressRepository.save(address);
    } else {
      throw new NotFoundException("address_not_found", messageService.getAddressNotFoundMessage(lang));
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
