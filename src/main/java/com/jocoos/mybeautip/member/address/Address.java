package com.jocoos.mybeautip.member.address;

import javax.persistence.*;

import java.util.Date;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.audit.Auditable;

@Slf4j
@Data
@Entity
@Table(name = "addresses")
public class Address extends Auditable<Long> {

  static final int BASE_PRIMARY = 0;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private boolean base;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String recipient;

  @Column(nullable = false)
  private String phone;

  @Column(nullable = false)
  private String zipNo;

  @Column(nullable = false)
  private String roadAddrPart1;

  @Column(nullable = false)
  private String roadAddrPart2;

  @Column(nullable = false)
  private String jibunAddr;

  @Column(nullable = false)
  private String detailAddress;

  @Column
  private Date deletedAt;

}
