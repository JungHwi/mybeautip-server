package com.jocoos.mybeautip.tag;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
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