package com.jocoos.mybeautip.domain.video.vo;

import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.Comment.CommentState;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@Getter
public class VideoComments {

    private final List<Comment> comments;

    public VideoComments(Comment child) {
        this.comments = singletonList(child);
    }

    public VideoComments(Comment parent, List<Comment> children, CommentState state) {
        children.add(parent);
        this.comments = setComments(children, state);
    }

    public VideoComments(List<Comment> comments, CommentState state) {
        this.comments = setComments(comments, state);
    }

    private List<Comment> setComments(List<Comment> children, CommentState state) {
        return children.stream()
                .filter(comment -> comment.eqState(state))
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
        return countMemberIds(memberCountMap);
    }

    public Map<Long, List<Long>> parentCountMap() {
        Map<Long, Long> childCountMap = childCountMap();
        return countMemberIds(childCountMap);
    }

    private Map<Long, Long> memberIdCountMap() {
        return comments.stream()
                .collect(groupingBy(Comment::getMemberId, counting()));
    }

    private Map<Long, Long> childCountMap() {
        return comments.stream()
                .filter(Comment::isChild)
                .collect(groupingBy(Comment::getParentId, counting()));
    }

    private Map<Long, List<Long>> countMemberIds(Map<Long, Long> memberIdCountMap) {
        Map<Long, List<Long>> countMemberIdsMap = new HashMap<>();
        memberIdCountMap.forEach((k, v) -> countMemberIdsMap.computeIfAbsent(v, u -> new ArrayList<>()).add(k));
        return countMemberIdsMap;
    }
}
