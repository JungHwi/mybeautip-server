package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.address.Address;
import com.jocoos.mybeautip.member.address.AddressRepository;
import com.jocoos.mybeautip.member.address.AddressService;
import com.jocoos.mybeautip.notification.MessageService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/1/members/me/addresses")
public class AddressController {

    private static final String ADDRESS_TOO_MANY_ADDRESS = "address.too_many_addresses";
    private final AddressService addressService;
    private final LegacyMemberService legacyMemberService;
    private final MessageService messageService;
    private final AddressRepository addressRepository;

    public AddressController(AddressService addressService,
                             LegacyMemberService legacyMemberService,
                             MessageService messageService,
                             AddressRepository addressRepository) {
        this.addressService = addressService;
        this.legacyMemberService = legacyMemberService;
        this.messageService = messageService;
        this.addressRepository = addressRepository;
    }

    @GetMapping
    public ResponseEntity<List<AddressInfo>> getAddresses(@RequestParam(required = false) String type) {
        List<Address> addresses = new ArrayList<>();
        if (type != null && type.equals("base")) {
            Address address = addressRepository.findByCreatedByIdAndDeletedAtIsNullAndBaseIsTrue(
                    legacyMemberService.currentMemberId()).orElse(null);
            addresses.add(address);
        } else {
            addresses = addressRepository.findByCreatedByIdAndDeletedAtIsNullOrderByIdDesc(legacyMemberService.currentMemberId());
        }

        List<AddressInfo> addressInfos = addresses.stream().map(AddressInfo::new).collect(Collectors.toList());

        return new ResponseEntity<>(addressInfos, HttpStatus.OK);
    }

    @GetMapping("/phone/check")
    public ResponseEntity checkPhoneNumber(@RequestParam("phone_number") String phoneNumber) {
        addressService.validPhoneNumber(phoneNumber);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AddressInfo> createAddress(@RequestBody CreateAddressRequest request,
                                                     @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        if (addressRepository.countByCreatedByIdAndDeletedAtIsNull(legacyMemberService.currentMemberId()) >= 10) {
            throw new BadRequestException("too_many_addresses", messageService.getMessage(ADDRESS_TOO_MANY_ADDRESS, lang));
        }

        return new ResponseEntity<>(new AddressInfo(addressService.create(request, legacyMemberService.currentMember())), HttpStatus.OK);
    }

    @PatchMapping("/{id:.+}")
    public ResponseEntity<AddressInfo> updateAddress(@PathVariable Long id,
                                                     @RequestBody UpdateAddressRequest request,
                                                     @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        return new ResponseEntity<>(new AddressInfo(addressService.update(id, request, lang)), HttpStatus.OK);
    }

    @DeleteMapping("/{id:.+}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long id,
                                           @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        log.debug("id to delete: {}", id);
        addressService.delete(id, lang, legacyMemberService.currentMemberId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Data
    public static class CreateAddressRequest {
        private Boolean base;

        @Size(max = 20)
        @NotNull
        private String title;

        @Size(max = 50)
        @NotNull
        private String recipient;

        @Size(max = 20)
        @NotNull
        private String phone;

        @Size(max = 10)
        @NotNull
        private String zipNo;

        @Size(max = 255)
        @NotNull
        private String roadAddrPart1;

        @Size(max = 255)
        @NotNull
        private String roadAddrPart2;

        @Size(max = 255)
        @NotNull
        private String jibunAddr;

        @Size(max = 100)
        @NotNull
        private String detailAddress;

    }

    @Data
    public static class UpdateAddressRequest {

        private Boolean base;

        @Size(max = 20)
        @NotNull
        private String title;

        @Size(max = 50)
        @NotNull
        private String recipient;

        @Size(max = 20)
        @NotNull
        private String phone;

        @Size(max = 10)
        @NotNull
        private String zipNo;

        @Size(max = 255)
        @NotNull
        private String roadAddrPart1;

        @Size(max = 255)
        @NotNull
        private String roadAddrPart2;

        @Size(max = 255)
        @NotNull
        private String jibunAddr;

        @Size(max = 100)
        @NotNull
        private String detailAddress;

    }

    @Data
    @NoArgsConstructor
    public static class AddressInfo {
        private Long id;
        private Boolean base;
        private String title;
        private String recipient;
        private String phone;
        private String zipNo;
        private String roadAddrPart1;
        private String roadAddrPart2;
        private String jibunAddr;
        private String detailAddress;
        private Integer areaShipping;

        public AddressInfo(Address address) {
            BeanUtils.copyProperties(address, this);
        }
    }
}
