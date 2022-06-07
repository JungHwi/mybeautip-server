package com.jocoos.mybeautip.banner;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.post.Post;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Data
@Entity
@Table(name = "banners")
@EqualsAndHashCode(callSuper = false)
public class Banner extends MemberAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String thumbnailUrl;

    @Column(nullable = false)
    private String slimThumbnailUrl;

    @Deprecated
    @Column(nullable = false)
    private int category;

    @Column(nullable = false)
    private int seq;

    @Column(nullable = false)
    private Long viewCount = 0L;

    @Deprecated
    @Column(nullable = false)
    private String link;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column
    private Date startedAt;

    @Column
    private Date endedAt;

    @Column
    @LastModifiedDate
    private Date modifiedAt;

    @Column
    private Date deletedAt;
}
