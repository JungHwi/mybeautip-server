package com.jocoos.mybeautip.domain.community.vo;

import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class CommunityComments {

    private final List<CommunityComment> comments;

    public CommunityComments(CommunityComment child) {
        this.comments = singletonList(child);
    }

    public CommunityComments(List<CommunityComment> comments, CommunityStatus status) {
        this.comments = setComments(comments, status);
    }

    public CommunityComments(CommunityComment parent, List<CommunityComment> children, CommunityStatus status) {
        children.add(parent);
        this.comments = setComments(children, status);
    }

    private List<CommunityComment> setComments(List<CommunityComment> children, CommunityStatus status) {
        return children.stream()
                .filter(comment -> comment.eqStatus(status))
                .toList();
    }

    public List<Long> ids() {
        return comments.stream()
                .map(CommunityComment::getId)
                .toList();
    }

    public int count() {
        return comments.size();
    }

    public List<Long> parentIds() {
        return comments.stream()
                .filter(CommunityComment::isParent)
                .map(CommunityComment::getId)
                .toList();
    }

    public Map<Long, List<Long>> activityCountMap() {
        Map<Long, Long> memberCountMap = memberIdCountMap();
        return countMemberIds(memberCountMap);
    }

    public Map<Long, List<Long>> parentCountMap() {
        Map<Long, Long> childCountMap = childCountMap();
        return countMemberIds(childCountMap);
    }

    private Map<Long, Long> memberIdCountMap() {
        return comments.stream()
                .collect(groupingBy(CommunityComment::getMemberId, counting()));
    }

    private Map<Long, Long> childCountMap() {
        return comments.stream()
                .filter(CommunityComment::isChild)
                .collect(groupingBy(CommunityComment::getParentId, counting()));
    }

    private Map<Long, List<Long>> countMemberIds(Map<Long, Long> memberIdCountMap) {
        Map<Long, List<Long>> countMemberIdsMap = new HashMap<>();
        memberIdCountMap.forEach((k, v) -> countMemberIdsMap.computeIfAbsent(v, u -> new ArrayList<>()).add(k));
        return countMemberIdsMap;
    }
}
