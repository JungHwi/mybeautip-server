package com.jocoos.mybeautip.support.payment;

import com.jocoos.mybeautip.restapi.AccountController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

public interface IamportApi {

  String getToken();

  PaymentResponse getPayment(String accessToken, String id);

  PaymentResponse cancelPayment(String accessToken, String impUid);
  
  ResponseEntity<VbankResponse> validAccountInfo(AccountController.UpdateAccountInfo info, String lang);
}
