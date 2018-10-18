package com.jocoos.mybeautip.tag;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "tags")
public class Tag extends CreatedDateAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(nullable = false)
  private String name;
  
  @Column
  private int refCount;
  
  @Column
  private Date modifiedAt;
  
  public Tag(String name, int refCount) {
    this.name = name;
    this.refCount = refCount;
    this.modifiedAt = new Date();
  }
}