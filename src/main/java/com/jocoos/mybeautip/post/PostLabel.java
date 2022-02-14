package com.jocoos.mybeautip.post;

import javax.persistence.*;

import com.jocoos.mybeautip.audit.ModifiedDateAuditable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "post_labels")
public class PostLabel extends ModifiedDateAuditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;
}
