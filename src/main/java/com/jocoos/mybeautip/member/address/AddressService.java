package com.jocoos.mybeautip.member.address;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.DeliveryChargeArea;
import com.jocoos.mybeautip.goods.DeliveryChargeAreaRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.restapi.AddressController;

@Service
public class AddressService {

  private final MemberService memberService;
  private final AddressRepository addressRepository;
  private final DeliveryChargeAreaRepository deliveryChargeAreaRepository;

  public AddressService(AddressRepository addressRepository,
                        MemberService memberService,
                        DeliveryChargeAreaRepository deliveryChargeAreaRepository) {
    this.addressRepository = addressRepository;
    this.memberService = memberService;
    this.deliveryChargeAreaRepository = deliveryChargeAreaRepository;
  }

  public Address insert(Address address) {
    address.setAreaShipping(getAreaShipping(address.getRoadAddrPart1()));
    return addressRepository.save(address);
  }

  @Transactional
  public Address update(Long id, AddressController.UpdateAddressRequest update) {
    Optional<Address> optional = addressRepository.findByIdAndCreatedByIdAndDeletedAtIsNull(id, memberService.currentMemberId());
    if (optional.isPresent()) {
      Address address = optional.get();
      BeanUtils.copyProperties(update, address);
      address.setAreaShipping(getAreaShipping(address.getRoadAddrPart1()));
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

  private int getAreaShipping(String roadAddress1) {
    if (roadAddress1.startsWith("제주")) {
      return deliveryChargeAreaRepository.findByArea("제주시특자치도").map(DeliveryChargeArea::getPrice).orElse(0);
    }

    if (StringUtils.startsWith(roadAddress1, "인천")) {
      if (StringUtils.startsWithAny(roadAddress1, "인천광역시 중구", "인천광역시 강화군", "인천광역시 옹진군")) {
        return deliveryChargeAreaRepository.findByArea(roadAddress1).map(DeliveryChargeArea::getPrice).orElse(0);
      } else {
        return 0;
      }
    }

    if (StringUtils.startsWith(roadAddress1, "부산")) {
      if (StringUtils.startsWith(roadAddress1, "부산광역시 강서구")) {
        return deliveryChargeAreaRepository.findByArea(roadAddress1).map(DeliveryChargeArea::getPrice).orElse(0);
      } else {
        return 0;
      }
    }

    if (StringUtils.startsWith(roadAddress1, "경상남도")) {
      if (StringUtils.startsWithAny(roadAddress1,"경상남도 사천시", "경상남도 통영시")) {
        return deliveryChargeAreaRepository.findByArea(roadAddress1).map(DeliveryChargeArea::getPrice).orElse(0);
      } else {
        return 0;
      }
    }

    if (StringUtils.startsWith(roadAddress1, "경상북도")) {
      if (StringUtils.startsWith(roadAddress1, "경상북도 울릉군")) {
        return deliveryChargeAreaRepository.findByArea(roadAddress1).map(DeliveryChargeArea::getPrice).orElse(0);
      } else {
        return 0;
      }
    }

    if (StringUtils.startsWith(roadAddress1, "충청남도")) {
      if (StringUtils.startsWithAny(roadAddress1, "충청남도 당진시", "충청남도 보령시", "충청남도 태안군")) {
        return deliveryChargeAreaRepository.findByArea(roadAddress1).map(DeliveryChargeArea::getPrice).orElse(0);
      } else {
        return 0;
      }
    }

    if (StringUtils.startsWith(roadAddress1, "전라북도")) {
      if (StringUtils.startsWithAny(roadAddress1, "전라북도 군산시", "전라북도 부안군")) {
        return deliveryChargeAreaRepository.findByArea(roadAddress1).map(DeliveryChargeArea::getPrice).orElse(0);
      } else {
        return 0;
      }
    }

    if (StringUtils.startsWith(roadAddress1, "전라남도")) {
      return deliveryChargeAreaRepository.findByArea(roadAddress1).map(DeliveryChargeArea::getPrice).orElse(0);
    }

    return 0;
  }
}
