package com.jocoos.mybeautip.app;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Data
@Entity
@Table(name = "app_info")
@EqualsAndHashCode(callSuper = false)
public class AppInfo extends CreatedDateAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String os;

  @Column(nullable = false)
  private String version;
}
