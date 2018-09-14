package com.jocoos.mybeautip.restapi;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.godo.GodoService;
import com.jocoos.mybeautip.goods.DeliveryCharge;
import com.jocoos.mybeautip.goods.DeliveryChargeRepository;

@Slf4j
@RestController
@RequestMapping(path = "/api/1/delivery", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeliveryController {

  private final DeliveryChargeRepository deliveryChargeRepository;
  private final ObjectMapper mapper;

  public DeliveryController(DeliveryChargeRepository deliveryChargeRepository,
                                ObjectMapper mapper) {
    this.deliveryChargeRepository = deliveryChargeRepository;
    this.mapper = mapper;
  }

  @GetMapping("/{id:.+}")
  public ResponseEntity<DeliveryChargeInfo> getDeliveryInfo(@PathVariable Integer id) throws IOException {
    Optional<DeliveryCharge> optional = deliveryChargeRepository.findById(id);
    if (optional.isPresent()) {
      return new ResponseEntity<>(new DeliveryChargeInfo(optional.get()), HttpStatus.OK);
    } else {
      throw new NotFoundException("delivery_not_found", "delivery not found: " + id);
    }
  }

  @Data
  public class DeliveryChargeInfo {
    Integer id;
    String method;
    String description;
    String collectFl;
    String fixFl;
    List<GodoService.ChargeData> chargeData;

    public DeliveryChargeInfo(DeliveryCharge info) throws IOException {
      BeanUtils.copyProperties(info, this);
      if (info.getChargeData() != null) {
        this.chargeData = Arrays.asList(mapper.readValue(info.getChargeData(), GodoService.ChargeData[].class));
      }
    }
  }
}
