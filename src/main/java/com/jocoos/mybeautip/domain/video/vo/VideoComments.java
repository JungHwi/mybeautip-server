package com.jocoos.mybeautip.domain.video.vo;

import com.jocoos.mybeautip.member.comment.Comment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

@Getter
@RequiredArgsConstructor
public class VideoComments {

    private final List<Comment> comments;

    public VideoComments(Comment child) {
        this.comments = singletonList(child);
    }

    public VideoComments(Comment comment, List<Comment> children) {
        children.add(comment);
        this.comments = children.stream()
                .filter(Comment::isNormal)
                .toList();
    }

    public List<Long> ids() {
        return comments.stream()
                .map(Comment::getId)
                .toList();
    }

    public int count() {
        return comments.size();
    }

    public List<Long> parentIds() {
        return comments.stream()
                .filter(Comment::isParent)
                .map(Comment::getId)
                .toList();
    }

    public Map<Long, List<Long>> activityCountMap() {
        Map<Long, Long> memberCountMap = memberIdCountMap();
        return countMemberIdsMap(memberCountMap);
    }

    private Map<Long, Long> memberIdCountMap() {
        return comments.stream()
                .collect(Collectors.groupingBy(Comment::getMemberId, Collectors.counting()));
    }

    private Map<Long, List<Long>> countMemberIdsMap(Map<Long, Long> memberCountMap) {
        Map<Long, List<Long>> countMemberIdsMap = new HashMap<>();
        memberCountMap.forEach((k, v) -> countMemberIdsMap.computeIfAbsent(v, u -> new ArrayList<>()).add(k));
        return countMemberIdsMap;
    }
}
