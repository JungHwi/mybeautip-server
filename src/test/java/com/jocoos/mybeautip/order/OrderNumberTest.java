package com.jocoos.mybeautip.order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class OrderNumberTest {

  public static void main(String[] args) {
    System.out.println("1808301647454359".length());

    for (int i=0; i<100; i++) {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmssSSS");
      String orderNumber = simpleDateFormat.format(new Date()) + new Random().nextInt(10);
      System.out.println(orderNumber + ", " + orderNumber.length());
    }
  }
}
