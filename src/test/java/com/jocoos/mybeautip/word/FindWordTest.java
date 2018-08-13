package com.jocoos.mybeautip.word;

import java.util.Hashtable;

public class FindWordTest {
  static String[] data = {"뷰팁", "어드민", "관리자", "관리인", "매니저", "테스트", "태스트", "체험단", "공지사항", "요쿠스", "오쿠스", "조쿠스", "죠쿠스",
    "beautip", "admin", "dev", "manager", "jocoos", "yocoos", "test"};


  public static void main(String[] args) {
    Hashtable<String, String> table = new Hashtable<>();
    for (String s: data) {
      table.put(s, s);
    }

    String username = "tester123";

    System.out.println("start list search");
    for (int i=0; i<data.length; i++) {
      String s = data[i];
      System.out.println(i + ":" + s);

      if (username.contains(s)) {
        System.out.println("find word! - " + s);
        break;
      }
    }

    int i=0;
    System.out.println("start hash table search");
    for (String key: table.keySet()) {
      System.out.println(i++ + ":" + key);
      if (username.contains(key)) {
        System.out.println("find word! - " + key);
        break;
      }
    }
  }
}
