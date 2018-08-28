package com.jocoos.mybeautip.restapi;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.member.address.Address;
import com.jocoos.mybeautip.member.address.AddressService;

@Slf4j
@RestController
@RequestMapping("/api/1/members/me/addresses")
public class AddressController {

  private final AddressService addressService;

  public AddressController(AddressService addressService) {
    this.addressService = addressService;
  }

  @GetMapping
  public ResponseEntity<List<AddressInfo>> getAddresses(@RequestParam(defaultValue = "10") int count) {
    List<Address> addresses = addressService.getAddresses(PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id")));

    ImmutableList<AddressInfo> addressInfos = FluentIterable.from(addresses)
       .transform(addresse -> new AddressInfo(addresse))
       .toList();

    return new ResponseEntity<>(addressInfos, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<AddressInfo> createAddress(@RequestBody CreateAddressRequest request) {
    Address address = new Address();
    log.debug("CreateAddressRequest: {}", request);

    BeanUtils.copyProperties(request, address);
    log.debug("address: {}", address);

    return new ResponseEntity<>(new AddressInfo(addressService.save(address)), HttpStatus.OK);
  }

  @PatchMapping("/{id:.+}")
  public ResponseEntity<AddressInfo> updateAddress(@PathVariable Long id,
                                                   @RequestBody UpdateAddressRequest request) {
    log.debug("UpdateAddressRequest: {}", request);
    Address update = addressService.update(id, request);

    return new ResponseEntity<>(new AddressInfo(update), HttpStatus.OK);
  }

  @DeleteMapping("/{id:.+}")
  public ResponseEntity<?> deleteAddress(@PathVariable Long id) {
    log.debug("id to delete: {}", id);
    addressService.delete(id);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Data
  public static class CreateAddressRequest {
    private boolean base;

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

    private boolean base;

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
    private boolean base;
    private String title;
    private String recipient;
    private String phone;
    private String zipNo;
    private String roadAddrPart1;
    private String roadAddrPart2;
    private String jibunAddr;
    private String detailAddress;

    public AddressInfo(Address address) {
      BeanUtils.copyProperties(address, this);
    }
  }
}
