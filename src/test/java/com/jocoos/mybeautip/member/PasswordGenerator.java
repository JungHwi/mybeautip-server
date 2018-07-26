package com.jocoos.mybeautip.member;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordGenerator {

  public static void main(String[] args) {
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    System.out.println(passwordEncoder.encode("akdlqbxlq#1@Jocoos"));
    System.out.println(passwordEncoder.encode("Qkdrn!Ehdrn?"));
    System.out.println(passwordEncoder.encode("anrWlQk@@524"));
  }
}
