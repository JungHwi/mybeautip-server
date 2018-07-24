package com.jocoos.mybeautip.post;

import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "post_contents")
public class PostContent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long postId;

  @Column(nullable = false)
  private int priority;

  @Column(nullable = false)
  private int category;

  @Column(nullable = false)
  private String content;

}
