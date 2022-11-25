package com.jocoos.mybeautip.member.comment;

import com.jocoos.mybeautip.audit.MemberAuditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Date;

import static com.jocoos.mybeautip.member.comment.Comment.CommentState.DEFAULT;
import static com.jocoos.mybeautip.member.comment.Comment.CommentState.getType;
import static com.jocoos.mybeautip.global.util.date.ZonedDateTimeUtil.toUTCZoned;
import static com.jocoos.mybeautip.member.comment.Comment.CommentState.*;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "comments")
public class Comment extends MemberAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long postId;

    @Column
    private Long videoId;

    @Column
    private Boolean locked = false;

    @Column(nullable = false)
    private String comment;

    @Column
    private String originalComment;

    @Column
    private Long parentId;

    @Column
    private int commentCount;

    @Column
    private int likeCount;

    @Column
    private int reportCount;

    /**
     * 0: Default, 1: Deleted 2: Blinded, 4: Blinded by Admin
     */
    @Column
    private int state;

    @Column(nullable = false)
    @LastModifiedDate
    private Date modifiedAt;

    public void setState(CommentState commentState) {
        this.state = commentState.value();
    }

    public CommentState getCommentState() {
        return getType(this.state);
    }

    public boolean isCommentSameOrLongerThan(int length) {
        return StringUtils.trimAllWhitespace(this.comment).length() >= length;
    }

    public boolean isParent() {
        return parentId == null;
    }

    public boolean isNormal() {
        return DEFAULT.equals(getType(state));
    }

    public Long getMemberId() {
        return createdBy.getId();
    }

    public CommentState getStateString() {
        return getType(state);
    }

    public ZonedDateTime getCreatedAtUTCZoned() {
        return toUTCZoned(createdAt);
    }

    public void hide(boolean isHide) {
        this.state = isHide ? BLINDED_BY_ADMIN.value : DEFAULT.value;
    }

    public boolean eqState(CommentState state) {
        return this.state == state.value;
    }

    public boolean isChild() {
        return parentId != null;
    }

    public enum CommentState {
        DEFAULT(0),
        DELETED(1),
        BLINDED(2),
        BLINDED_BY_ADMIN(4);

        private int value;

        CommentState(int value) {
            this.value = value;
        }

        public static CommentState getType(int value) {
            CommentState state = DEFAULT;
            for (CommentState v : CommentState.values()) {
                if (v.value() == value) {
                    state = v;
                    break;
                }
            }

            return state;
        }

        public int value() {
            return this.value;
        }
    }
}

