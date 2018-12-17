package com.jocoos.mybeautip.search;

import com.jocoos.mybeautip.audit.ModifiedDateAuditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "search_stats")
public class SearchStat extends ModifiedDateAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(nullable = false)
  private String keyword;
  
  @Column(nullable = false)
  private Integer count;
  
  public SearchStat(String keyword) {
    this.keyword = keyword;
    this.count = 1;
  }
}