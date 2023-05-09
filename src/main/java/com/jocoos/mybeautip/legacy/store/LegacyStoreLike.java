package com.jocoos.mybeautip.legacy.store;

import com.jocoos.mybeautip.audit.MemberAuditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "legacy_store_likes")
public class LegacyStoreLike extends MemberAuditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private LegacyStore legacyStore;

    public LegacyStoreLike(LegacyStore legacyStore) {
        this.legacyStore = legacyStore;
    }
}