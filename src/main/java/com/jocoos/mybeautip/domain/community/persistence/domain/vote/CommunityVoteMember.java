package com.jocoos.mybeautip.domain.community.persistence.domain.vote;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CommunityVoteMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_vote_id")
    private CommunityVote communityVote;

    @Builder
    public CommunityVoteMember(Community community, Member member, CommunityVote vote) {
        this.community = community;
        this.member = member;
        this.communityVote = vote;
    }

    public void changeVote(CommunityVote vote) {
        this.communityVote = vote;
    }

    public Long getCommunityVoteId() {
        return this.communityVote == null ? null : this.communityVote.getId();
    }
}
