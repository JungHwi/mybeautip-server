package com.jocoos.mybeautip.domain.store.persistence.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "store")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private StoreCategory category;


}
