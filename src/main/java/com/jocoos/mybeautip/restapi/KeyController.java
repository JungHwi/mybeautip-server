package com.jocoos.mybeautip.restapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;

@Slf4j
@RestController
@RequestMapping("/api/1")
public class KeyController {

  @Value("${mybeautip.addr.confirm-key}")
  private String confirmKey;

  @GetMapping("/keys")
  public ResponseEntity<KeyInfo> getAddressConfirmKey(@RequestParam String name) {
    log.debug("confirmKey: {}", confirmKey);

    switch (name) {
      case "addr_confirm":
        return new ResponseEntity<KeyInfo>(new KeyInfo(confirmKey), HttpStatus.OK);
      default:
        throw new BadRequestException("invalid request name - " + name);
    }
  }

  @AllArgsConstructor
  @Getter
  static class KeyInfo {
    private String confirmKey;
  }
}
