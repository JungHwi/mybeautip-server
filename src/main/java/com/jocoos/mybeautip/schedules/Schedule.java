package com.jocoos.mybeautip.schedules;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.member.Member;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "schedules")
public class Schedule extends CreatedDateAuditable {

    @ManyToOne
    @JoinColumn(name = "created_by")
    protected Member createdBy;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String title;
    @Column
    private String thumbnailUrl;
    @Column
    private Date startedAt;

    @LastModifiedDate
    private Date modifiedAt;

    @Column
    private Date deletedAt;

    @Column
    private String instantTitle;

    @Column
    private String instantMessage;
}