package com.jocoos.mybeautip.recommendation;


import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Data
@Entity
@Table(name = "recommended_motd_bases")
public class MotdRecommendationBase extends CreatedDateAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Date baseDate;

    @Column
    private int motdCount;

    @OneToMany(mappedBy = "baseId")
    private List<MotdRecommendation> motds;

    public void addMotd(MotdRecommendation motd) {
        if (this.motds == null) {
            this.motds = new ArrayList<>();
        }

        this.motds.add(motd);
    }
}
