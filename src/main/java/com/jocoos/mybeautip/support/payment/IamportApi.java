package com.jocoos.mybeautip.support.payment;

public interface IamportApi {

  String getToken();

  PaymentResponse getPayment(String accessToken, String id);

  PaymentResponse cancelPayment(String accessToken, String impUid);

}
