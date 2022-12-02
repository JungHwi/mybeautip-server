package com.jocoos.mybeautip.domain.community.persistence.domain.vote;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityFile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CommunityVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private Community community;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_file_id")
    private CommunityFile communityFile;

    @Column
    private int voteCount;

    public CommunityVote(Community community, CommunityFile communityFile) {
        this.communityFile = communityFile;
        this.community = community;
        this.voteCount = 0;
    }
}
