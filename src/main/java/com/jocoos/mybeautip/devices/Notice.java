package com.jocoos.mybeautip.devices;

import com.jocoos.mybeautip.audit.MemberAuditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "notices")
public class Notice extends MemberAuditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false)
  private String os;

  @Column(nullable = false)
  private String message;

  @Column(nullable = false)
  private String minVersion;

  @Column(nullable = false)
  private String maxVersion;

  @Column
  @LastModifiedDate
  private Date modifiedAt;
}
