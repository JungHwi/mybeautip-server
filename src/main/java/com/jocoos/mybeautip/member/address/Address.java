package com.jocoos.mybeautip.member.address;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.audit.MemberAuditable;

@Slf4j
@Data
@Entity
@Table(name = "addresses")
@EqualsAndHashCode(callSuper = false)
public class Address extends MemberAuditable {

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

  @Column(nullable = false)
  private Integer areaShipping;

  @Column
  @LastModifiedDate
  private Date modifiedAt;

  @Column
  private Date deletedAt;

}
